package dam_48286.finalproject

import java.time.LocalDateTime

interface AlarmScheduler {
    fun schedule(medicine: Medicine, reminderTime: LocalDateTime)
    fun cancel(medicine: Medicine)
}