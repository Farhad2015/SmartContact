package com.example.mycontacts.presentation.contact

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Data class representing a contact.
data class Contact(val name: String, val phone: String)

// Function to query contacts using the ContentResolver.
fun getContacts(context: Context): List<Contact> {
    val contacts = mutableListOf<Contact>()
    val contentResolver = context.contentResolver

    // Query the contacts content URI for name and phone number.
    val cursor = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        ),
        null,
        null,
        "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
    )

    cursor?.use { cur ->
        val nameIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val numberIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        while (cur.moveToNext()) {
            val name = cur.getString(nameIndex)
            val phone = cur.getString(numberIndex)
            contacts.add(Contact(name, phone))
        }
    }
    return contacts
}

@Composable
fun ContactsScreen() {
    val context = LocalContext.current

    // State variables for permission and search query.
    var hasPermission by remember { mutableStateOf(false) }
    var permissionRequested by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Create a launcher for requesting a single permission.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        permissionRequested = true
    }

    // Request the permission when the composable is first shown.
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacts List") },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (hasPermission) {
                // Load contacts when permission is granted.
                val contacts by produceState<List<Contact>>(initialValue = emptyList()) {
                    value = getContacts(context)
                }

                // Filter contacts based on the search query.
                val filteredContacts = if (searchQuery.isEmpty()) {
                    contacts
                } else {
                    contacts.filter { contact ->
                        contact.name.contains(searchQuery, ignoreCase = true) ||
                                contact.phone.contains(searchQuery, ignoreCase = true)
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Search bar at the top.
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search Contacts") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )

                    if (filteredContacts.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(filteredContacts) { contact ->
                                ContactItem(contact = contact, context = context)
                            }
                        }
                    } else {
                        // Display message when no contacts match the search query.
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No contacts found.",
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                }
            } else {
                // When permission is not granted.
                val message = if (permissionRequested) {
                    "Permission denied. Please enable contacts permission in your device settings."
                } else {
                    "Requesting permission to access contacts..."
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.body1,
                        color = if (permissionRequested) MaterialTheme.colors.error else MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun ContactItem(contact: Contact, context: Context) {
    // A card for each contact with clickable functionality to launch the dialer.
    Card(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Create an intent to open the dialer with the phone number.
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${contact.phone}")
                }
                context.startActivity(dialIntent)
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = contact.phone,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

