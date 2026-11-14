package com.iregina.jesuswords.reminders

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.iregina.jesuswords.data.AppSettings
import com.iregina.jesuswords.data.SettingsRepository
import java.time.LocalDate
import java.time.ZoneId

/**
 * Daily reminder notifications (NotificationManager.swift): the next 7 days, one per day,
 * each carrying that day's verse. Everything is rescheduled (cancel all, schedule 7) when
 * the app opens, the language or time changes, and after a reboot.
 *
 * Uses inexact `setAndAllowWhileIdle` alarms: they fire even while the phone sleeps, need
 * no exact-alarm permission, and Android delivers them within an hour of the set time
 * (usually within minutes).
 */
object ReminderScheduler {
    const val DAYS = 7
    const val CHANNEL_ID = "daily_verse"
    const val EXTRA_EPOCH_DAY = "epochDay"
    private const val ACTION_SHOW = "com.iregina.jesuswords.SHOW_DAILY_VERSE"

    suspend fun rescheduleFromSettings(context: Context) {
        reschedule(context, SettingsRepository(context).current())
    }

    fun reschedule(context: Context, settings: AppSettings) {
        cancelAll(context)
        if (!settings.reminderEnabled) return
        val alarms = context.getSystemService(AlarmManager::class.java) ?: return
        val zone = ZoneId.systemDefault()
        val now = System.currentTimeMillis()
        val today = LocalDate.now(zone)
        for (slot in 0 until DAYS) {
            val date = today.plusDays(slot.toLong())
            val triggerAt = date.atTime(settings.reminderHour, settings.reminderMinute).atZone(zone).toInstant().toEpochMilli()
            if (triggerAt <= now) continue
            alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent(context, slot, date))
        }
    }

    fun cancelAll(context: Context) {
        val alarms = context.getSystemService(AlarmManager::class.java) ?: return
        for (slot in 0 until DAYS) alarms.cancel(pendingIntent(context, slot, null))
    }

    /** Removes delivered reminders from the shade (done whenever the app is opened). */
    fun clearDelivered(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }

    fun ensureChannel(context: Context, name: String) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT),
        )
    }

    // Slot = request code, so cancelling slots 0..6 removes every scheduled reminder.
    private fun pendingIntent(context: Context, slot: Int, date: LocalDate?): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
            .setAction(ACTION_SHOW)
            .putExtra(EXTRA_EPOCH_DAY, date?.toEpochDay() ?: 0L)
        return PendingIntent.getBroadcast(
            context, slot, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
