package com.example.mycontacts.navigation
// Import statements
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.mycontacts.ContactsApp
import com.example.mycontacts.presentation.FavoriteHomeBody
import com.example.mycontacts.presentation.contact.ContactsScreen
import com.example.mycontacts.presentation.dialar.DialerScreen
import com.example.mycontacts.ui.theme.md_theme_dark_secondary
import com.example.mycontacts.ui.theme.md_theme_light_secondary
import androidx.compose.material3.Text as Text1

// Define Bottom Navigation Items
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Filled.Call, "Dial")
    object Settings : BottomNavItem("settings", Icons.Filled.AccountCircle, "My Contact")
    object Contact : BottomNavItem("contact", Icons.Filled.AccountBox, "All Contact")
    object Favorite : BottomNavItem("contact", Icons.Filled.Favorite, "Favorites")
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = listOf(BottomNavItem.Home, BottomNavItem.Settings, BottomNavItem.Contact,BottomNavItem.Favorite),
                currentRoute = currentRoute ?: BottomNavItem.Home.route,
                onItemSelected = { item ->
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to avoid building up a large stack of destinations
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            SetupNavGraph(navController = navController)
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemSelected: (BottomNavItem) -> Unit
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.onPrimary,
        contentColor = Color.White
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text1(text = item.label) },
                selected = currentRoute == item.route,
                onClick = { onItemSelected(item) },
                selectedContentColor = md_theme_light_secondary,
                unselectedContentColor = md_theme_dark_secondary
            )
        }
    }
}

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) {
            DialerScreen()
        }
        composable(BottomNavItem.Settings.route) {
            ContactsApp()
        }
        composable(BottomNavItem.Contact.route) {
            ContactsScreen()
        }
        composable(BottomNavItem.Favorite.route) {
            FavoriteHomeBody()
        }
    }
}
