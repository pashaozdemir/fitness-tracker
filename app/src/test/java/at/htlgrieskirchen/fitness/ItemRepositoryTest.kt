package at.htlgrieskirchen.fitness.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit-Tests für die CRUD-Logik.
 * Prüft alle vier Operationen: schreiben, lesen, ändern, löschen.
 *
 * Ausführen:  ./gradlew testDebugUnitTest
 * (oder in Android Studio: Rechtsklick auf die Datei -> "Run")
 */
class ItemRepositoryTest {

    private lateinit var repo: ItemRepository

    @Before
    fun setup() {
        repo = ItemRepository()   // vor jedem Test ein frisches, leeres Repository
    }

    @Test
    fun `am Anfang ist die Liste leer`() {
        assertEquals(0, repo.getAll().size)
    }

    @Test
    fun `add fuegt ein Item hinzu (CREATE + READ)`() {
        repo.add("Laufen")
        assertEquals(1, repo.getAll().size)
        assertEquals("Laufen", repo.getAll().first().name)
    }

    @Test
    fun `update aendert den Namen (UPDATE)`() {
        val item = repo.add("Laufen")
        val ok = repo.update(item.id, "Joggen")
        assertTrue(ok)
        assertEquals("Joggen", repo.getAll().first().name)
    }

    @Test
    fun `update mit unbekannter ID gibt false zurueck`() {
        val ok = repo.update(999, "Egal")
        assertFalse(ok)
    }

    @Test
    fun `delete entfernt ein Item (DELETE)`() {
        val item = repo.add("Schwimmen")
        val ok = repo.delete(item.id)
        assertTrue(ok)
        assertEquals(0, repo.getAll().size)
    }

    @Test
    fun `delete mit unbekannter ID gibt false zurueck`() {
        assertFalse(repo.delete(999))
    }
}
