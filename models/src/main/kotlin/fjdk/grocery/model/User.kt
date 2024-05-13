package fjdk.grocery.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String?,
    var email: String?,
    var username: String?,
    var firstName: String?,
    var lastName: String?,
) {
    constructor(
        email: String?,
        username: String?,
        firstName: String?,
        lastName: String?,
    ) : this(null, email, username, firstName, lastName)
}
