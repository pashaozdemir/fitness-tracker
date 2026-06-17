package at.htlgrieskirchen.fitness.data

import java.util.Calendar

data class StatsSummary(
    val activityCount: Int,
    val totalMinutes: Int
)

enum class Period { TODAY, THIS_WEEK, THIS_MONTH }

class StatisticsCalculator(private val now: Long = System.currentTimeMillis()) {

    fun summarize(activities: List<FsItem>, period: Period): StatsSummary {
        val filtered = activities.filter { it.timestamp >= cutoff(period) }
        return StatsSummary(
            activityCount = filtered.size,
            totalMinutes = filtered.sumOf { it.durationMinutes }
        )
    }

    private fun cutoff(period: Period): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        when (period) {
            Period.TODAY -> {}
            Period.THIS_WEEK -> cal.add(Calendar.DAY_OF_YEAR, -7)
            Period.THIS_MONTH -> cal.add(Calendar.DAY_OF_YEAR, -30)
        }
        return cal.timeInMillis
    }
}