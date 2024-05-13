package fjdk.grocery.application

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerItem(label: String, image: ImageVector? = null, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() },
    ) {
        if (image != null) {
            Icon(image, contentDescription = label, tint = Color.Black)
        }
        ClickableText(
            text = buildAnnotatedString {
                withStyle(
                    style = androidx.compose.ui.text.SpanStyle(
                        fontSize = 18.sp,
                        textDecoration = TextDecoration.Underline,
                    ),
                ) {
                    append(label)
                }
            },
            onClick = { offset ->
                onClick()
            },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically),
        )
    }
}
