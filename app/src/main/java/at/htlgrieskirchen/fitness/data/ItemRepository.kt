package at.htlgrieskirchen.fitness.data

/**
 * Ein einzelner Eintrag (Item). Hat eine ID und einen Namen.
 */
data class Item(val id: Int, val name: String)

/**
 * Die CRUD-Logik (Create, Read, Update, Delete) für Items.
 * Reine Kotlin-Klasse ohne Android -> lässt sich super einfach testen.
 *
 * - add    = schreiben/erstellen
 * - getAll = lesen
 * - update = ändern
 * - delete = löschen
 */
class ItemRepository {
    private val items = mutableListOf<Item>()
    private var nextId = 1

    /** READ: alle Items zurückgeben. */
    fun getAll(): List<Item> = items.toList()

    /** CREATE: neues Item anlegen und zurückgeben. */
    fun add(name: String): Item {
        val item = Item(id = nextId++, name = name)
        items.add(item)
        return item
    }

    /** UPDATE: Item mit dieser ID umbenennen. Gibt true zurück, wenn es geklappt hat. */
    fun update(id: Int, newName: String): Boolean {
        val index = items.indexOfFirst { it.id == id }
        if (index == -1) return false
        items[index] = items[index].copy(name = newName)
        return true
    }

    /** DELETE: Item mit dieser ID löschen. Gibt true zurück, wenn etwas gelöscht wurde. */
    fun delete(id: Int): Boolean = items.removeAll { it.id == id }
}
