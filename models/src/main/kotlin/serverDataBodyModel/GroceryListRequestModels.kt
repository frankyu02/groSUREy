package serverDataBodyModel

import kotlinx.serialization.Serializable

@Serializable
data class AddItemToGroceryListBody(
    val kitchenId: String = "",
    val itemID: String = "",
    val name: String = "",
    val quantity: Double = 0.0,
    val unit: String = "",
    var expirationDate: String? = null,
    var category: List<String>? = null,
)

@Serializable
data class UpdateItemInListBody(
    val kitchenId: String = "",
    val name: String = "",
    var quantity: Double? = null,
)

@Serializable
data class RemoveItemInListBody(
    val kitchenId: String = "",
    val name: String = "",
)

@Serializable
data class AddUserToListBody(
    val code: String = "",
    val uuid: String = "",
)

@Serializable
data class RemoveUserFromListBody(
    val kitchenId: String = "",
    val uuid: String = "",
)

@Serializable
data class InviteCodeBody(
    val kitchenId: String = "",
    val uuid: String = "",
)
