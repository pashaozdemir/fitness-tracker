package at.htlgrieskirchen.fitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.htlgrieskirchen.fitness.data.FirestoreItemRepository
import at.htlgrieskirchen.fitness.data.FsItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ItemScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen() {
    val repo = remember { FirestoreItemRepository() }
    val items = remember { mutableStateListOf<FsItem>() }
    var text by remember { mutableStateOf("") }
    var editId by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        val registration = repo.listen { list ->
            items.clear()
            items.addAll(list)
        }
        onDispose { registration.remove() }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Meine Aktivitäten (Cloud)") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(if (editId == null) "Neuer Eintrag" else "Eintrag ändern") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (text.isBlank()) return@Button
                        val id = editId
                        if (id == null) {
                            repo.add(text.trim())
                        } else {
                            repo.update(id, text.trim())
                            editId = null
                        }
                        text = ""
                    }
                ) {
                    Text(if (editId == null) "Add" else "Speichern")
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.name, modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            editId = item.id
                            text = item.name
                        }) { Text("Ändern") }
                        TextButton(onClick = {
                            repo.delete(item.id)
                            if (editId == item.id) { editId = null; text = "" }
                        }) { Text("Löschen") }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}