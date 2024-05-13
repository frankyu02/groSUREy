package fjdk.grocery.application

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import fjdk.grocery.application.ui.theme.ApplicationTheme
import fjdk.grocery.model.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var preferencesManager: PreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(applicationContext)

        // Setup firebase for authentication
        FirebaseApp.initializeApp(this)
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            preferencesManager.setLoginState(false)
        } else {
            GlobalState.updateKitchenId(user.uid)
        }

        setContent {
            ApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val isLoggedIn = remember { mutableStateOf(preferencesManager.getLoginState()) }

                    AuthenticationWrapper(
                        isLoggedIn = isLoggedIn.value,
                        setLoggedInState = { loggedIn ->
                            isLoggedIn.value = loggedIn
                            preferencesManager.setLoginState(loggedIn)
                        },
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationWrapper(isLoggedIn: Boolean, setLoggedInState: (Boolean) -> Unit) {
    if (isLoggedIn) {
        MainContent(onLogout = {
            setLoggedInState(false)
            FirebaseAuth.getInstance().signOut()
        })
    } else {
        AuthenticationScreen(onLoginSuccess = { setLoggedInState(true) })
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Column {
        TopAppBar(
            title = { Text("GroceryGoats") },
            navigationIcon = {
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open/Close Drawer",
                    )
                }
            },
        )

        NavigationDrawer(
            drawerState = drawerState,
            onLogout = onLogout,
        )
    }
}
