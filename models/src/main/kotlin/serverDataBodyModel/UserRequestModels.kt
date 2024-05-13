package serverDataBodyModel

import kotlinx.serialization.Serializable

@Serializable
data class SignupBody(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String,
)
