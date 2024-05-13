package fjdk.grocery.application

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fjdk.grocery.model.GroceryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddForm(modifier: Modifier = Modifier) {
    var counter by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(), // Fill the maximum size available
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Add Grocery Items", fontSize = 24.sp)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                var itemName by remember { mutableStateOf("") }
                var itemQuantity by remember { mutableStateOf("") }
                var itemUnit by remember { mutableStateOf("") }

                TextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Item Name") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = itemQuantity, onValueChange = { itemQuantity = it }, label = { Text("Quantity") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = itemUnit, onValueChange = { itemUnit = it }, label = { Text("Unit") })
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    val quantity: Double = itemQuantity.toDoubleOrNull() ?: 0.0
                    val newItem = GroceryItem(
                        itemId = "${java.util.UUID.randomUUID()}",
                        name = itemName,
                        quantity = quantity,
                        unit = itemUnit,
                    )
                    if (itemName != "" && itemUnit != "" && quantity > 0) {
                        GlobalState.addItem(newItem)
                        itemName = ""
                        itemQuantity = ""
                        itemUnit = ""
                    } else if (quantity <= 0) {
                        Toast.makeText(
                            context,
                            "Quantity too low",
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Missing item/unit",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }) {
                    Text(text = "Add to List")
                }
            }
        }
    }
}
