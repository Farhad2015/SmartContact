package com.example.mycontacts.presentation.contact.detailScreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.mycontacts.R
import com.example.mycontacts.presentation.contact.ContactUiState
import com.example.mycontacts.presentation.contact.entryScreen.ContactInputForm

/**
 * @param onFavoriteChanged Callback invoked with the new favorite status.
 * Typically, you will pass a ViewModel function here to update the contact in the database.
 */
@Composable
fun DetailsBody(
    contactUiState: ContactUiState,
    onDelete: () -> Unit,
    onFavoriteChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Manage the delete confirmation and favorite state.
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    // Initialize favorite state from the contactUiState
    var isFavorite by rememberSaveable(contactUiState.id) {
        mutableStateOf(contactUiState.isFavorite)
    }
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Image
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Image(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = "stringResource(R.string.profile_image)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }

        // Contact Details (Read-Only)
        ContactInputForm(
            contactUiState = contactUiState,
            enabled = false,
        )

        FavoriteButton(
            isFavorite = isFavorite,
            onFavoriteClick = {
                isFavorite = !isFavorite
                onFavoriteChanged(isFavorite)
            }
        )


        // Call Button
        CallButton(
            phoneNumber = contactUiState.address,
            modifier = Modifier
        )

        // Delete Button
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(text = stringResource(R.string.delete_button))
        }

        // Delete Confirmation Dialog
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false }
            )
        }
    }
}

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Use an IconButton to toggle the favorite state.
    IconButton(
        onClick = onFavoriteClick,
        modifier = modifier
    ) {
        if (isFavorite) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "stringResource(R.string.remove_from_favorites)",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription =" stringResource(R.string.add_to_favorites)",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CallButton(phoneNumber: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = { initiatePhoneDialer(context, phoneNumber) },
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = Icons.Filled.Call,
            contentDescription = "",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Call Now")
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
