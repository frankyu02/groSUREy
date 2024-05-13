package fjdk.grocery.application

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import fjdk.grocery.application.api.ApiClient
import serverDataBodyModel.SignupBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                // Password input
                TextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val email = emailState.value
                        val password = passwordState.value

                        ApiClient.loginAttempt(email, password, { user ->
                            // On Success
                            onLoginSuccess()
                        }, {
                            // On Failure
                            Toast.makeText(
                                context,
                                "Incorrect email/password.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }, {
                            // On Error
                            Toast.makeText(
                                context,
                                "Invalid email/password.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        })
                    },
                ) {
                    Text("Log in")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(onSignupSuccess: () -> Unit) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val firstNameState = remember { mutableStateOf("") }
    val lastNameState = remember { mutableStateOf("") }
    val context = LocalContext.current

    val fixedWidth = 120.dp

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Sign up", style = MaterialTheme.typography.headlineMedium)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                ) {
                    TextField(
                        value = firstNameState.value,
                        onValueChange = { firstNameState.value = it },
                        label = { Text("First Name") },
                        singleLine = true,
                        modifier = Modifier.width(fixedWidth),
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = lastNameState.value,
                        onValueChange = { lastNameState.value = it },
                        label = { Text("Last Name") },
                        singleLine = true,
                        modifier = Modifier.width(fixedWidth),
                    )
                }

                TextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                // Password input
                TextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val signupBody = SignupBody(
                            firstName = firstNameState.value,
                            lastName = lastNameState.value,
                            username = "",
                            email = emailState.value,
                            password = passwordState.value,
                        )

                        ApiClient.signupAttempt(signupBody, { user ->
                            // On Success
                            onSignupSuccess()
                        }, { errMsg ->
                            // On Failure
                            var message = "Error signing up"
                            if (errMsg != "") {
                                message = errMsg
                            }
                            Toast.makeText(
                                context,
                                message,
                                Toast.LENGTH_SHORT,
                            ).show()
                        }, {
                            // On Error
                            Toast.makeText(
                                context,
                                "Error signing up",
                                Toast.LENGTH_SHORT,
                            ).show()
                        })
                    },
                ) {
                    Text("Sign up")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(onLoginSuccess: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            LoginScreen(onLoginSuccess)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            SignUpScreen(onLoginSuccess)
        }
    }
}
