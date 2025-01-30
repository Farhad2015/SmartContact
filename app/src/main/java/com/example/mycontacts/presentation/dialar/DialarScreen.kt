package com.example.mycontacts.presentation.dialar

// Import statements
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.mycontacts.ui.theme.md_theme_light_tertiary

@Composable
fun DialerScreen() {
    var enteredNumber by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                initiateCall(context, enteredNumber)
            } else {
                // Inform the user that permission was denied
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DisplayNumber(number = enteredNumber)
        Spacer(modifier = Modifier.height(32.dp))
        DialerPad(
            onNumberClick = { digit ->
                if (enteredNumber.length < 15) { // Limit the number length
                    enteredNumber += digit
                }
            },
            onDeleteClick = {
                if (enteredNumber.isNotEmpty()) {
                    enteredNumber = enteredNumber.dropLast(1)
                }
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        CallButton(onClick = {
            if (enteredNumber.isNotEmpty()) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    initiateCall(context, enteredNumber)
                } else {
                    // Request permission
                    permissionLauncher.launch(Manifest.permission.CALL_PHONE)
                }
            }
        }, isEnabled = enteredNumber.isNotEmpty())
    }
}

@Composable
fun DisplayNumber(number: String) {
    Text(
        text = number,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun DialerPad(onNumberClick: (String) -> Unit, onDeleteClick: () -> Unit) {
    val buttons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    DialerButton(label = label, onClick = { onNumberClick(label) })
                }
            }
        }
        // Delete button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { onDeleteClick() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Delete",
                    tint = Color.Blue,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
fun DialerButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape),
        shape = CircleShape
    ) {
        Text(text = label, style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun CallButton(onClick: () -> Unit, isEnabled: Boolean) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .size(width = 200.dp, height = 56.dp),
        shape = RoundedCornerShape(28.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Call",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Call", style = MaterialTheme.typography.headlineMedium)
    }
}

fun initiateCall(context: Context, phoneNumber: String) {
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
