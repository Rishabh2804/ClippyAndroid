package rish.dev.android.clippy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import rish.dev.android.clippy.model.Clip
import rish.dev.android.clippy.model.ClipType
import rish.dev.android.clippy.ui.theme.ClippyAndroidTheme
import rish.dev.android.clippy.viewmodel.ClippyVM

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClippyAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    WebSocketScreen()
                }
            }
        }
    }
}

@Composable
@Preview
fun WebSocketScreen() {
    val viewModel: ClippyVM = viewModel()
    val messages by viewModel.messages.collectAsState()

    WebSocketUI(
        onConnectClick = { viewModel.connect() },
        onDisconnectClick = { viewModel.disconnect() },
        onSendMessageClick = { clipData, clipType ->
            val clip = Clip(clipData, clipType)
            viewModel.sendMessage(clip)
        },
        messages = messages
    )
}

@Composable
fun WebSocketUI(
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onSendMessageClick: (String, ClipType) -> Unit,
    messages: List<Clip>
) {
    var clipData by remember { mutableStateOf("") }
    var selectedClipType by remember { mutableStateOf(ClipType.entries[0]) }
    var isConnected by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                when (isConnected) {
                    true -> onDisconnectClick()
                    false -> onConnectClick()
                }

                isConnected = !isConnected
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (isConnected) Color.Green else Color.Red)
        ) {
            Text(if (isConnected) "Disconnect" else "Connect")
        }

        OutlinedTextField(
            value = clipData,
            onValueChange = { clipData = it },
            label = { Text("Clip Data") }
        )

        ClipTypeMenu(
            onClick = {
                selectedClipType = it
            }
        )

        Button(onClick = { onSendMessageClick(clipData, selectedClipType) }) {
            Text("Send Message")
        }

        LazyColumn {
            items(messages) { message ->
                Text("${message.clipData} - (${message.clipType}) ")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipTypeMenu(onClick: (ClipType) -> Unit = {}) {
    val clipTypes = ClipType.entries
    var expanded by remember { mutableStateOf(false) }
    var selectedCipType by remember { mutableStateOf(clipTypes[0]) }

    Box(
        modifier = Modifier
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                label = { Text("Clip Type") },
                value = "CipType :    $selectedCipType",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                clipTypes.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.name) },
                        onClick = {
                            onClick(item)

                            selectedCipType = item
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}