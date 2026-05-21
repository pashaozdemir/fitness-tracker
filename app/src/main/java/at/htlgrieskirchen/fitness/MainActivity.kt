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

/**
 * Einfache CRUD-App zum Ausprobieren:
 *   - anzeigen (Read)
 *   - hinzufügen (Create)
 *   - ändern (Update)
 *   - löschen (Delete)
 *
 * Daten liegen nur im Speicher -> läuft sofort, kein Backend nötig.
 *
 * Einbauen: Inhalt in eure MainActivity.kt kopieren.
 * Wichtig: Beim Projekt-Anlegen als Package-Name  at.htlgrieskirchen.fitness  verwenden,
 * dann passt die package-Zeile oben automatisch.
 */
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

/** Ein Eintrag in der UI. */
data class UiItem(val id: Int, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen() {
    val items = remember { mutableStateListOf(UiItem(1, "Laufen"), UiItem(2, "Schwimmen")) }
    var nextId by remember { mutableStateOf(3) }
    var text by remember { mutableStateOf("") }
    // Wenn != null, sind wir im "Ändern"-Modus für dieses Item.
    var editId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Meine Aktivitäten") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Eingabefeld + Button (Add ODER Ändern)
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
                            // CREATE
                            items.add(UiItem(nextId, text.trim()))
                            nextId++
                        } else {
                            // UPDATE
                            val index = items.indexOfFirst { it.id == id }
                            if (index != -1) items[index] = items[index].copy(name = text.trim())
                            editId = null
                        }
                        text = ""
                    }
                ) {
                    Text(if (editId == null) "Add" else "Speichern")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Liste: pro Zeile "Ändern" und "Löschen"
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
                            // in den Ändern-Modus gehen: Text ins Feld laden
                            editId = item.id
                            text = item.name
                        }) { Text("Ändern") }
                        TextButton(onClick = {
                            // DELETE
                            items.removeAll { it.id == item.id }
                            if (editId == item.id) { editId = null; text = "" }
                        }) { Text("Löschen") }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
