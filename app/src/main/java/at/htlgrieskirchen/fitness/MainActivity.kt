package at.htlgrieskirchen.fitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Sonstiges") }
    var duration by remember { mutableStateOf("") }
    var editId by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val registration = repo.listen { list ->
            items.clear()
            items.addAll(list)
        }
        onDispose { registration.remove() }
    }

    val typeOptions = listOf("Laufen", "Radfahren", "Schwimmen", "Sonstiges")
    var typeMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Meine Aktivitäten (Cloud)") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; showError = false },
                label = { Text(if (editId == null) "Name der Aktivität" else "Aktivität ändern") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = typeMenuExpanded,
                onExpandedChange = { typeMenuExpanded = !typeMenuExpanded }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Typ") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = typeMenuExpanded,
                    onDismissRequest = { typeMenuExpanded = false }
                ) {
                    typeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                type = option
                                typeMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it; showError = false },
                label = { Text("Dauer in Minuten") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if (showError) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Bitte Name eingeben und Dauer größer als 0.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val dur = duration.toIntOrNull() ?: 0
                    if (name.isBlank() || dur <= 0) {
                        showError = true
                        return@Button
                    }
                    val id = editId
                    if (id == null) {
                        repo.add(name.trim(), type, dur)
                    } else {
                        repo.update(id, name.trim(), type, dur)
                        editId = null
                    }
                    name = ""
                    duration = ""
                    type = "Sonstiges"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (editId == null) "Add" else "Speichern")
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, fontWeight = FontWeight.Bold)
                            Text(
                                "${item.type} • ${item.durationMinutes} Min",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        TextButton(onClick = {
                            editId = item.id
                            name = item.name
                            type = item.type
                            duration = item.durationMinutes.toString()
                        }) { Text("Ändern") }
                        TextButton(onClick = {
                            repo.delete(item.id)
                            if (editId == item.id) {
                                editId = null
                                name = ""
                                duration = ""
                                type = "Sonstiges"
                            }
                        }) { Text("Löschen") }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}