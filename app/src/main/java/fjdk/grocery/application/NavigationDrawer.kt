package fjdk.grocery.application

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fjdk.grocery.model.*
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    onLogout: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf("MyKitchens") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    { selectedScreen: String ->
                        currentScreen = selectedScreen
                        scope.launch { drawerState.close() }
                    },
                    onLogout = onLogout,
                )
            }
        },
    ) {
        Scaffold {
            when (currentScreen) {
                "AddToList" -> AddForm()
                "MyKitchens" -> Kitchens()
                "Recipes" -> Recipes()
                "GroceryList" -> GroceryList()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(onScreenSelected: (String) -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
//            .wrapContentSize(Alignment.TopStart),
    ) {
        DrawerItem("My Kitchens", Icons.Filled.List) {
            onScreenSelected("MyKitchens")
        }
        DrawerItem("Add to list", Icons.Filled.Add) {
            onScreenSelected("AddToList")
        }
        DrawerItem("Grocery Cart", Icons.Filled.ShoppingCart) {
            onScreenSelected("GroceryList")
        }
//        DrawerItem("Recipes") {
//            onScreenSelected("Recipes")
//        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onLogout) {
            Text("Log out")
        }
    }
}
