package serverDataBodyModel

import kotlinx.serialization.Serializable

@Serializable
data class RenameKitchenBody(
    val newName: String,
)

@Serializable
data class CreateKitchenBody(
    val name: String,
)
