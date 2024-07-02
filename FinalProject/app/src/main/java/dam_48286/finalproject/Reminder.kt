package dam_48286.finalproject

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class Repeat {
    CUSTOM, HOURLY
}

@Parcelize
data class Reminder(
    var repeat: Repeat? = null,
    var hour: String? = null,
    var date: String? = null,
    var daysOfWeek: List<DayOfWeek> = emptyList(),
    var enabled: Boolean? = null
) :Parcelable {
    @Exclude
    fun getHourAsLocalTime(): LocalTime? {
        return hour?.let { LocalTime.parse(it) }
    }

    @Exclude
    fun getDateAsLocalDate(): LocalDate? {
        return date?.let { LocalDate.parse(it) }
    }

    companion object {
        @Exclude
        fun fromLocalTime(localTime: LocalTime): String {
            return localTime.format(DateTimeFormatter.ISO_LOCAL_TIME)
        }

        @Exclude
        fun fromLocalDate(localDate: LocalDate): String {
            return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }
}


