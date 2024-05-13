package fjdk.grocery.application

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import fjdk.grocery.application.api.ApiClient
import fjdk.grocery.model.GroceryItem
import kotlinx.coroutines.*
import kotlin.math.absoluteValue

@Composable
private fun InventoryCard(
    item: GroceryItem,
    onConsume: (GroceryItem) -> Unit,
) {
    val majorFont = 20.sp
    val minorFont = 16.sp
    val spacing = 12.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray) // Adds a border around the Row
            .padding(8.dp), // Adds padding inside the border
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(item.name, fontSize = majorFont)
                Spacer(modifier = Modifier.width(spacing))

                Text(item.quantity.toString(), fontSize = majorFont)
                Spacer(modifier = Modifier.width(4.dp))

                Text(item.unit, fontSize = minorFont)
                Spacer(modifier = Modifier.width(spacing))
            }

            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                if (item.expirationDate != "") {
                    Text("Expires: ${item.expirationDate}", fontSize = minorFont)
                }
            }
        }

        Button(
            onClick = { onConsume(item) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff009688)),
            modifier = Modifier,
        ) {
            Icon(Icons.Filled.Done, contentDescription = "Consume", tint = Color.White)
        }
    }
}

@Composable
fun KitchenList() {
    val groceryList by remember { GlobalState::groceryList }

    LazyColumn(
        state = rememberLazyListState(),
        contentPadding = PaddingValues(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(items = groceryList.items, key = { _, listItem ->
            listItem.hashCode()
        }) { _, item ->
            var offsetX by remember { mutableStateOf(0f) }
            val dismissThreshold = 300f // Threshold for swipe to dismiss

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            offsetX += dragAmount.x
                            if (offsetX.absoluteValue >= dismissThreshold) { // swipe away item
                                GlobalState.removeItem(item)
                            }
                        }
                    },
            ) {
                if (offsetX != 0f) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Red),
                    )
                }
                InventoryCard(
                    item = item,
                    onConsume = { consumedItem ->
                        GlobalState.consumeItem(consumedItem)
                    },
                )
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun Kitchens() {
    val showAddCode = remember { mutableStateOf(false) }
    val showKitchens = remember { mutableStateOf(false) }
    var kitchenCode by remember { mutableStateOf("") }
    val groceryList by remember { GlobalState::groceryList }
    val kitchenLists by remember { GlobalState::kitchenLists }
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        GlobalState.updateList(force = true)
        GlobalState.updateKitchenLists()
        while (true) {
            GlobalState.updateList()
            delay(100L)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(groceryList.listName, fontSize = 24.sp)
        Divider(color = Color.Blue, thickness = 2.dp)
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            KitchenList()
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                FloatingActionButton( // left button
                    onClick = { showKitchens.value = true },
                    modifier = Modifier
                        .padding(16.dp),
                ) {
                    Icon(Icons.Filled.Menu, "Show Kitchens")
                }

                if (auth.currentUser?.uid == groceryList.listId) {
                    FloatingActionButton( // right button
                        onClick = { showAddCode.value = true },
                        modifier = Modifier
                            .padding(16.dp),
                    ) {
                        Icon(Icons.Filled.Add, "Invite to kitchen")
                    }
                }
            }

            if (showAddCode.value && !showKitchens.value) {
                val code =
                    try {
                        runBlocking { ApiClient.generateCode() }
                    } catch (e: Throwable) {
                        ""
                    }

                AlertDialog(
                    onDismissRequest = { showAddCode.value = false },
                    title = { Text("Please share this code with who you want to invite.") },
                    text = { Text(code) },
                    confirmButton = {
                        TextButton(onClick = { showAddCode.value = false }) {
                            Text("OK")
                        }
                    },
                )
            }

            if (!showAddCode.value && showKitchens.value) {
                GlobalState.updateKitchenLists()

                AlertDialog(
                    onDismissRequest = { showKitchens.value = false },
                    title = { Text("Click on kitchen to View") },
                    text = {
                        Column {
                            kitchenLists.forEach {
                                TextButton(
                                    onClick = {
                                        GlobalState.updateKitchenId(it.listId)
                                        GlobalState.updateList(force = true)
                                        showKitchens.value = false
                                    },
                                ) {
                                    Text(it.listName)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Column {
                            TextField(
                                value = kitchenCode,
                                onValueChange = { kitchenCode = it },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    runBlocking { ApiClient.joinList(kitchenCode) }
                                    GlobalState.updateKitchenLists()
                                    showKitchens.value = false
                                    kitchenCode = ""
                                },
                            ) {
                                Text("Join Kitchen")
                            }
                        }
                    },
                )
            }
        }
    }
}

// private fun onConsume(consumedItem: GroceryItem) : GroceryList {
//    ApiClient.consumeItem(consumedItem)
//    return ApiClient.getGroceryList()
// }
//
// private fun onDelete(deletedItem: GroceryItem) : GroceryList {
//    ApiClient.removeItem(deletedItem)
//    return ApiClient.getGroceryList()
// }
