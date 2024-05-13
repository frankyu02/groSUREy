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
import fjdk.grocery.model.GroceryItem
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
private fun GroceryCard(item: GroceryItem, onPurchase: (GroceryItem) -> Unit, onCancel: (GroceryItem) -> Unit) {
    val majorFont = 20.sp
    val minorFont = 16.sp
    val spacing = 12.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray)
            .padding(8.dp),
    ) {
        Text(item.name, fontSize = majorFont)

        Spacer(modifier = Modifier.width(spacing))

        Text(item.quantity.toString(), fontSize = majorFont)
        Spacer(modifier = Modifier.width(4.dp))
        Text(item.unit, fontSize = minorFont)

        Spacer(modifier = Modifier.width(spacing))

        if (item.category != null && item.category!!.isNotEmpty()) {
            Text("Category: ${item.category}", fontSize = minorFont)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onPurchase(item) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff0288d1)),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Purchase", tint = Color.White)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = { onCancel(item) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        ) {
            Icon(Icons.Filled.Clear, contentDescription = "Cancel", tint = Color.White)
        }
    }
}

@Composable
fun GroceryListItems() {
    val groceryList by remember { GlobalState::groceryList }

    LazyColumn(
        state = rememberLazyListState(),
        contentPadding = PaddingValues(10.dp),
        modifier = Modifier.fillMaxSize(),
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
                            if (offsetX.absoluteValue >= dismissThreshold) {
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
                GroceryCard(
                    item = item,
                    onPurchase = { purchasedItem ->
                        GlobalState.purchaseItem(purchasedItem)
                    },
                    onCancel = { canceledItem ->
                        GlobalState.consumeItem(canceledItem)
                    },
                )
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun GroceryList() {
    LaunchedEffect(Unit) {
        GlobalState.updateList(force = true)
        while (true) {
            GlobalState.updateList()
            delay(100L)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Grocery List", fontSize = 24.sp)
        Divider(color = Color.Blue, thickness = 2.dp)
        GroceryListItems()
    }
}
