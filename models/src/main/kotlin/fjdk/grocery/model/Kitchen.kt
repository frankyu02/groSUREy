package fjdk.grocery.model

import kotlinx.serialization.Serializable

@Serializable
data class Kitchen(
    var kitchenID: String,
    var kitchenName: String? = "My Kitchen",
    var kitchenItems: GroceryList,
    var kitchenOwners: MutableList<String>, // TODO: Update this when we create users
)
