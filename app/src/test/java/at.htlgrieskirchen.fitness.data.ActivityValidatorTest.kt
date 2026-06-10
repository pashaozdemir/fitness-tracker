package at.htlgrieskirchen.fitness.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ActivityValidatorTest {

    private fun isValid(name: String, durationMinutes: Int): Boolean {
        return name.isNotBlank() && durationMinutes > 0
    }

    @Test
    fun `gueltige Eingabe ist valid`() {
        assertTrue(isValid("Laufen", 30))
    }

    @Test
    fun `leerer Name ist invalid`() {
        assertFalse(isValid("", 30))
    }

    @Test
    fun `Dauer 0 ist invalid`() {
        assertFalse(isValid("Laufen", 0))
    }

    @Test
    fun `Dauer negativ ist invalid`() {
        assertFalse(isValid("Laufen", -5))
    }
}