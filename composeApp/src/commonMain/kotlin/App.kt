import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import composedemo.composeapp.generated.resources.Res
import composedemo.composeapp.generated.resources.compose_multiplatform
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.round
import kotlin.random.Random

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        var heap by remember { mutableStateOf(MinHeap<Double>()) }
        val showDialog = remember { mutableStateOf(false) }
        var minString by remember { mutableStateOf("") }
        var newestValue by remember { mutableStateOf("") }

        if (showDialog.value) {
            Alert("Heap is Empty", showDialog.value, onDismiss = {showDialog.value = false})
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                val r = Random.nextDouble()
                heap.insert(r, r)
                minString = heap.peek()?.toString() ?: ""
                newestValue = r.toString()
            }) {
                Text("Insert Random Value!")
            }
            Button(onClick = {
                if (heap.isEmpty()) {
                    showDialog.value = true
                } else {
                    heap.getMin()
                    minString = heap.peek()?.toString() ?: ""
                }
            }) {
                Text("Remove min")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Most recently inserted value", modifier=Modifier.padding(12.dp))
                Text(newestValue)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Minimum value in the heap", modifier=Modifier.padding(12.dp))
                Text(minString)
            }
            val textMeasure = rememberTextMeasurer()

            Canvas(modifier = Modifier.fillMaxSize()) {
                val rowSpacing = 20F
                val rectSize = size / 20F
                val vertices = heap.getVertices()
                val style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black,
                    background = Color.Red.copy(alpha = 0.5f)
                )
                vertices.forEachIndexed { index, vert ->
                    // TODO: can't get string formatting to work properly, so defaulting to this
                    // less than optimal approach
                    val roundedValue = round(vert.first*100)/100
                    val textLayoutResult = textMeasure.measure(
                        AnnotatedString(roundedValue.toString()),
                        style = style
                    )
                    val level = log2(index.toDouble()+1).toInt()
                    val position = (index - 2.0.pow(level) + 1.0).toInt()
                    val offset = Offset(position*(rectSize.width + rowSpacing), (rowSpacing+rectSize.height)*level)
                    drawText(textLayoutResult, topLeft = offset)
                }
            }
        }
    }
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
