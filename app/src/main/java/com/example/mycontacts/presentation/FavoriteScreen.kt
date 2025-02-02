package com.example.mycontacts.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.runtime.Composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycontacts.R
import com.example.mycontacts.domain.model.Contact
import com.example.mycontacts.presentation.homeScreen.HomeViewModel
import com.example.mycontacts.presentation.homeScreen.composables.ContactItems
import com.example.mycontacts.presentation.homeScreen.composables.SearchComponent

// A composable similar to HomeBody, but filtering to only show favorites.
@Composable
fun FavoriteHomeBody(
    viewModel: HomeViewModel = hiltViewModel()
) {

    val homeUiState by viewModel.homeUiState.collectAsState()
    var context =LocalContext.current
    // Filter the passed list to only include favorites.
    val favoriteContacts = homeUiState.contactList.filter { it.isFavorite }
    if (favoriteContacts.isEmpty()) {
        // Show an empty state if there are no favorite contacts.
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.list_empty),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
            Text(
                text =" stringResource(R.string.no_favorites)",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
            )
        }
    } else {
        // Use a contact list composable that supports search and item clicks.
        FavoriteContactList(
            contactList = favoriteContacts,
            onContactClick = {
                initiatePhoneDialer(context,it.address)
            }
        )
    }
}

// A composable that displays the favorite contacts in a searchable list.
@Composable
fun FavoriteContactList(
    contactList: List<Contact>,
    onContactClick: (Contact) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Reuse the same search-related state from your HomeViewModel.
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(40.dp))
        SearchComponent(searchText, isSearching)

        Spacer(modifier = Modifier.height(15.dp))

        // Apply the search filter on the provided contact list.
        val filteredList = viewModel.searchUser(contactList)

        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            LazyColumn {
                items(items = filteredList, key = { it.id }) { contact ->
                    ContactItems(
                        contact = contact,
                        onContactClick = onContactClick
                    )
                    Divider()
                }
            }
        }
    }
}
fun initiatePhoneDialer(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        context.startActivity(intent)
    }
}