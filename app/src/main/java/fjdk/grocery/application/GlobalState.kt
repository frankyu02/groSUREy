package fjdk.grocery.application

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fjdk.grocery.application.api.ApiClient
import fjdk.grocery.model.GroceryItem
import fjdk.grocery.model.GroceryList
import fjdk.grocery.model.GroceryListSummary
import kotlinx.coroutines.*
import kotlin.math.max

object GlobalState {
    var kitchenId by mutableStateOf("")
    var groceryList by mutableStateOf(GroceryList("", "", emptyList<GroceryItem>().toMutableList()))
    var kitchenLists by mutableStateOf(emptyList<GroceryListSummary>())
    var lastUpdate = 0L

    fun updateList() {
        updateList(false)
    }

    fun updateList(force: Boolean) {
        if (force || System.currentTimeMillis() - lastUpdate >= 1000) {
            GlobalScope.launch {
                val result = try {
                    ApiClient.getGroceryList(kitchenId).copy()
                } catch (e: Exception) {
                    GroceryList("", "", emptyList<GroceryItem>().toMutableList())
                }

                withContext(Dispatchers.Main) {
                    if (force || System.currentTimeMillis() - lastUpdate >= 1000) {
                        groceryList = result
                        lastUpdate = System.currentTimeMillis()
                    }
                }
            }
        }
    }

    fun updateKitchenLists() {
        GlobalScope.launch {
            val result = try {
                ApiClient.getAllLists()
            } catch (e: Exception) {
                kitchenLists
            }

            withContext(Dispatchers.Main) {
                kitchenLists = result
            }
        }
    }

    fun updateKitchenId(id: String) {
        kitchenId = id
        updateList()
    }

    fun addItem(item: GroceryItem) {
        GlobalScope.launch {
            try {
                ApiClient.addItem(item)
            } catch (e: Exception) {
                // do nothing - network err
            }
            updateList()
        }
    }

    fun removeItem(item: GroceryItem) {
        GlobalScope.launch {
            val updatedItems = groceryList.items.filter {
                it.name == item.name
            }.toMutableList()
            val newList = groceryList.copy(items = updatedItems)

            withContext(Dispatchers.Main) {
                groceryList = newList
                lastUpdate = System.currentTimeMillis()
            }
        }

        GlobalScope.launch {
            try {
                ApiClient.removeItem(item)
            } catch (e: Exception) {
                // do nothing - network error
            }
        }
    }

    fun purchaseItem(item: GroceryItem) {
        GlobalScope.launch {
            val updatedItems = groceryList.items.map { listItem ->
                if (listItem.name == item.name) {
                    // Create a new instance with updated quantity
                    listItem.copy(quantity = listItem.quantity + 1)
                } else {
                    listItem
                }
            }.toMutableList()
            val newList = groceryList.copy(items = updatedItems)

            withContext(Dispatchers.Main) {
                groceryList = newList
                lastUpdate = System.currentTimeMillis()
            }
        }

        GlobalScope.launch {
            try {
                ApiClient.purchaseItem(item)
            } catch (e: Exception) {
                // do nothing - network error
            }
        }
    }

    fun consumeItem(item: GroceryItem) {
        GlobalScope.launch {
            val updatedItems = groceryList.items.map { listItem ->
                if (listItem.name == item.name) {
                    // Create a new instance with updated quantity
                    listItem.copy(quantity = max(0.0, listItem.quantity - 1))
                } else {
                    listItem
                }
            }.toMutableList()
            val newList = groceryList.copy(items = updatedItems)

            withContext(Dispatchers.Main) {
                groceryList = newList
                lastUpdate = System.currentTimeMillis()
            }
        }

        GlobalScope.launch {
            try {
                ApiClient.consumeItem(item)
            } catch (e: Exception) {
                // do nothing - network error
            }
        }
    }
}
