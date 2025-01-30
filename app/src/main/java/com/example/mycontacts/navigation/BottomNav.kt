package com.example.mycontacts.navigation
// Import statements
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
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
import com.example.mycontacts.presentation.dialar.DialerScreen
import com.example.mycontacts.ui.theme.MyContactsTheme
import com.example.mycontacts.ui.theme.md_theme_dark_secondary
import com.example.mycontacts.ui.theme.md_theme_light_secondary
import androidx.compose.material3.Text as Text1

// Define Bottom Navigation Items
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Filled.Call, "Dialer")
    object Settings : BottomNavItem("settings", Icons.Filled.AccountCircle, "Contacts")
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = listOf(BottomNavItem.Home, BottomNavItem.Settings),
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
    }
}

@Composable
fun HomeScreen() {
    // Replace with your Home screen content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text1(
            text = "Home Screen",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SettingsScreen() {
    // Replace with your Settings screen content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text1(
            text = "Settings Screen",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
