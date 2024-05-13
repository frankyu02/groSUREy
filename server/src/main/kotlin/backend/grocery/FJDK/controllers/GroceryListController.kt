package backend.grocery.FJDK.controllers

import backend.grocery.FJDK.firebase.FirebaseAdmin
import backend.grocery.FJDK.utils.getGroceryItemFromMap
import com.google.cloud.firestore.DocumentSnapshot
import fjdk.grocery.model.GroceryItem
import fjdk.grocery.model.GroceryList
import fjdk.grocery.model.GroceryListSummary
import fjdk.grocery.model.User
import serverDataBodyModel.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

private fun getUpdatedGroceryList(document: DocumentSnapshot, list: MutableList<MutableMap<String, Any>>): GroceryList {
    val documentID = document.id
    val name = document.get("name") as? String ?: ""
    val items = list.map { getGroceryItemFromMap(it) } as MutableList
    return GroceryList(listName = name, listId = documentID, items = items)
}

fun makeEmptyList(user: User): Boolean {
    if (user.id == null) {
        return false
    }
    val db = FirebaseAdmin.db
    // check if document exists
    val groceryListDocument = db.collection("grocery-lists").document(user.id.toString()).get().get()
    if (groceryListDocument.exists()) {
        return false
    }
    val defaultList = hashMapOf(
        "name" to "${user.firstName}'s List",
        "items" to emptyList<Any>(),
    )
    try {
        db.collection("grocery-lists").document(user.id.toString()).set(defaultList)
        return true
    } catch (e: Exception) {
        throw e
    }
}

fun getSingleList(id: String): GroceryList? {
    val db = FirebaseAdmin.db
    try {
        val groceryListReference = db.collection("grocery-lists").document(id).get()
        val groceryListDocument = groceryListReference.get()
        return if (groceryListDocument.exists()) {
            @Suppress("UNCHECKED_CAST")
            val documentItems = groceryListDocument.get("items") as List<Map<String, Any>>
            val items = mutableListOf<GroceryItem>()
            if (documentItems.isNotEmpty()) {
                documentItems.forEach {
                    items.add(getGroceryItemFromMap(it))
                }
            }
            val documentID = groceryListDocument.id
            val name = groceryListDocument.get("name") as? String ?: ""
            GroceryList(listName = name, listId = documentID, items = items)
        } else {
            null
        }
    } catch (e: Exception) {
        throw e
    }
}

fun getAllLists(id: String): List<GroceryListSummary> {
    val db = FirebaseAdmin.db
    try {
        val userKitchen = db.collection("grocery-lists").document(id).get().get()
        if (!userKitchen.exists()) {
            return emptyList()
        }
        val otherKitchen = db.collection("grocery-lists").whereArrayContains("users", id).get().get()
        val ret = mutableListOf<GroceryListSummary>()
        ret.add(GroceryListSummary(listName = userKitchen["name"] as? String ?: "", listId = userKitchen.id))
        for (document in otherKitchen) {
            val name = document["name"] as? String ?: ""
            val docId = document.id
            ret.add(GroceryListSummary(listId = docId, listName = name))
        }
        return ret
    } catch (e: Exception) {
        throw e
    }
}

fun addItemToList(body: AddItemToGroceryListBody): GroceryList? {
    val db = FirebaseAdmin.db
    try {
        val groceryListReference = db.collection("grocery-lists").document(body.kitchenId)
        val groceryListData = groceryListReference.get().get()
        if (groceryListData.exists()) {
            @Suppress("UNCHECKED_CAST")
            val documentItems = groceryListData.get("items") as MutableList<MutableMap<String, Any>>
            // check for duplicates
            var found = false
            for (items in documentItems) {
                if (items["name"] == body.name) {
                    found = true
                    val quantity = abs(body.quantity)
                    items["quantity"] = items["quantity"].toString().toDouble() + quantity
                    break
                }
            }
            if (!found) {
                val newItem = mutableMapOf<String, Any>(
                    "category" to body.category as Any,
                    "expirationDate" to body.expirationDate as Any,
                    "id" to body.itemID,
                    "name" to body.name,
                    "quantity" to body.quantity,
                    "unit" to body.unit,
                )
                documentItems.add(newItem)
            }
            groceryListReference.update("items", documentItems)
            return getUpdatedGroceryList(groceryListData, documentItems)
        } else {
            return null
        }
    } catch (e: Exception) {
        throw e
    }
}

fun incrementItemFromList(body: UpdateItemInListBody): GroceryList? {
    val db = FirebaseAdmin.db
    try {
        val groceryListReference = db.collection("grocery-lists").document(body.kitchenId)
        val groceryListData = groceryListReference.get().get()
        return if (groceryListData.exists()) {
            @Suppress("UNCHECKED_CAST")
            val documentItems = groceryListData.get("items") as MutableList<MutableMap<String, Any>>
            for (items in documentItems) {
                if (items["name"] == body.name) {
                    val quantity = body.quantity ?: 1.0
                    items["quantity"] = items["quantity"].toString().toDouble() + quantity
                    break
                }
            }
            groceryListReference.update("items", documentItems)
            getUpdatedGroceryList(groceryListData, documentItems)
        } else {
            null
        }
    } catch (e: Exception) {
        throw e
    }
}

fun decrementItemFromList(body: UpdateItemInListBody): GroceryList? {
    val db = FirebaseAdmin.db
    try {
        val groceryListReference = db.collection("grocery-lists").document(body.kitchenId)
        val groceryListData = groceryListReference.get().get()
        return if (groceryListData.exists()) {
            @Suppress("UNCHECKED_CAST")
            val documentItems = groceryListData.get("items") as MutableList<MutableMap<String, Any>>
            for (item in documentItems) {
                if (item["name"] == body.name) {
                    val quantity = body.quantity ?: 1.0
                    item["quantity"] = max(item["quantity"].toString().toDouble() - quantity, 0.0)
                    break
                }
            }
            groceryListReference.update("items", documentItems)
            getUpdatedGroceryList(groceryListData, documentItems)
        } else {
            null
        }
    } catch (e: Exception) {
        throw e
    }
}

fun removeItemFromList(body: RemoveItemInListBody): GroceryList? {
    val db = FirebaseAdmin.db
    try {
        val groceryListReference = db.collection("grocery-lists").document(body.kitchenId)
        val groceryListData = groceryListReference.get().get()
        if (groceryListData.exists()) {
            @Suppress("UNCHECKED_CAST")
            val documentItems = groceryListData.get("items") as MutableList<MutableMap<String, Any>>
            var index = 0
            for (item in documentItems) {
                if (item["name"] == body.name) {
                    break
                }
                ++index
            }
            if (index < documentItems.size) {
                documentItems.removeAt(index)
            }
            groceryListReference.update("items", documentItems)
            return getUpdatedGroceryList(groceryListData, documentItems)
        } else {
            return null
        }
    } catch (e: Exception) {
        throw e
    }
}

fun addUserToList(body: AddUserToListBody): GroceryListSummary? {
    val db = FirebaseAdmin.db
    try {
        val groceryListReference =
            db.collection("grocery-lists").whereEqualTo("inviteCode", body.code.uppercase()).get().get()
        if (groceryListReference.isEmpty) {
            return null
        }
        val kitchen = groceryListReference.toList()[0]
        if (kitchen.id == body.uuid) {
            throw Exception("Cannot add to List. You are already in this list")
        }
        @Suppress("UNCHECKED_CAST")
        val userList = kitchen["users"] as? MutableList<String> ?: mutableListOf()
        if (body.uuid in userList) {
            throw Exception("Cannot add to List. You are already in this list")
        }
        userList.add(body.uuid)
        db.collection("grocery-lists").document(kitchen.id).update("users", userList)
        return GroceryListSummary(listId = kitchen.id, listName = (kitchen["name"] as? String ?: ""))
    } catch (e: Exception) {
        throw e
    }
}

fun removeUserFromList(body: RemoveUserFromListBody): Boolean {
    val db = FirebaseAdmin.db
    try {
        val groceryListReference = db.collection("grocery-lists").document(body.kitchenId)
        val groceryList = groceryListReference.get().get()
        return if (groceryList.exists()) {
            @Suppress("UNCHECKED_CAST")
            var userList = groceryList["users"] as? MutableList<String> ?: mutableListOf()
            userList = userList.filter { user -> user != body.uuid }.toMutableList()
            groceryListReference.update("users", userList)
            true
        } else {
            false
        }
    } catch (e: Exception) {
        throw e
    }
}

fun getRandomLetters(random: Random): List<Int> = List(4) {
    random.nextInt(65, 90)
}

fun generateCode(body: InviteCodeBody): String? {
    val db = FirebaseAdmin.db
    try {
        val groceryListReference = db.collection("grocery-lists").document(body.kitchenId)
        val groceryListData = groceryListReference.get().get()
        if (groceryListData.exists()) {
            if (body.kitchenId != body.uuid) {
                val userList = groceryListData["users"] as? List<*> ?: emptyList<String>()
                if (body.kitchenId !in userList) {
                    return null
                }
            }
            var salt = 0
            for (char in body.uuid) {
                salt += char.code
            }
            val seed = System.currentTimeMillis() + salt
            val letters = getRandomLetters(Random(seed)).map { it.toChar() }.toCharArray()
            val code = String(letters)
            groceryListReference.update("inviteCode", code)
            return code
        } else {
            return null
        }
    } catch (e: Exception) {
        throw e
    }
}
