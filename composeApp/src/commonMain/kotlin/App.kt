
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import kotlin.String
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.round
import kotlin.random.Random

@Composable
@Preview
fun App() {
    MaterialTheme {
        val heap by remember { mutableStateOf(MinHeap<Double>()) }
        val showDialog = remember { mutableStateOf(false) }
        val dialogText = remember { mutableStateOf("") }
        var newestValue by remember { mutableStateOf(0) }
        val textStyle = TextStyle(
            fontSize = 18.sp,
            color = Color.Black
        )
        if (showDialog.value) {
            Alert(dialogText.value, showDialog.value, onDismiss = {showDialog.value = false})
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                if (heap.size() >= 31) {
                    dialogText.value = "Heap is full"
                    showDialog.value = true
                } else {
                    val r = Random.nextDouble()
                    val p = floor(Random.nextDouble()*100)
                    heap.insert(r, p)
                    newestValue = p.toInt()
                }
            }) {
                Text("Insert Random Value!")
            }
            Button(onClick = {
                if (heap.isEmpty()) {
                    dialogText.value = "Heap is empty"
                    showDialog.value = true
                } else {
                    heap.getMin()
                }
            }) {
                Text("Remove min")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Most recently inserted value",
                    style=textStyle,
                    modifier=Modifier.padding(12.dp))
                Text(newestValue.toString(), style=textStyle)
            }
            val textMeasure = rememberTextMeasurer()

            Canvas(modifier = Modifier.fillMaxSize()) {
                val vertices = heap.getVertices()

                vertices.forEachIndexed { index, vert ->
                    val textLayout = textMeasure.measure(
                        AnnotatedString(vert.second.toInt().toString()),
                        style = textStyle
                    )
                    val center = centerCanvasCoordinates(size, index)
                    val offset = Offset(center.x - textLayout.size.width/2,
                                        center.y - textLayout.size.height/2)
                    if (index != 0) { // the root has no parent
                        // standard math to get parent index
                        val parentIndex = (index - 1) / 2
                        val parentCenter = centerCanvasCoordinates(size, parentIndex)
                        drawLine(
                            color = Color.Black,
                            start = center - Offset(0f, textLayout.size.height.toFloat() / 2),
                            end = parentCenter + Offset(
                                0f,
                                textLayout.size.height.toFloat() / 2
                            )
                        )
                    }
                    drawText(textLayout, topLeft = offset)
                }
            }
        }
    }
}

fun centerCanvasCoordinates(canvasSize: Size, index: Int): Offset {
    val vertSpacing = canvasSize.height/5
    val level = log2(index.toDouble()+1).toInt()
    val leadSpacing = canvasSize.width.toFloat() / 2.0.pow(level+1)
    val gap = canvasSize.width.toFloat() / 2.0.pow(level)
    val position = (index - 2.0.pow(level) + 1.0).toInt()
    return Offset((leadSpacing + position*gap).toFloat(), vertSpacing*level+vertSpacing/3)
}

@Composable
fun Alert(name: String,
          showDialog: Boolean,
          onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            title = {
                Text("Title")
            },
            text = {
                Text(text = name)
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss ) {
                    Text("OK")
                }
            },
            dismissButton = {}
        )
    }
}
