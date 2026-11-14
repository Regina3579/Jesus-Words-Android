package com.iregina.jesuswords.reminders

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.iregina.jesuswords.MainActivity
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.ContentRepository
import com.iregina.jesuswords.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Shows one day's reminder: title, the reference as summary, and the full verse. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val epochDay = intent.getLongExtra(ReminderScheduler.EXTRA_EPOCH_DAY, 0L)
        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                show(context.applicationContext, if (epochDay > 0) LocalDate.ofEpochDay(epochDay) else LocalDate.now())
            } finally {
                pending.finish()
            }
        }
    }

    private suspend fun show(context: Context, date: LocalDate) {
        val settings = SettingsRepository(context).current()
        if (!settings.reminderEnabled || !canNotify(context)) return
        val word = ContentRepository.get(context).wordFor(date) ?: return
        val language = settings.language

        ReminderScheduler.ensureChannel(context, language.pick("Daily verse", "தினசரி வசனம்"))
        val open = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val quote = word.quote(language)
        val reference = word.reference(language)
        val notification = NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_cross)
            .setContentTitle(language.pick("✝️ Jesus Words", "✝️ இயேசுவின் வார்த்தைகள்"))
            .setSubText(reference)
            .setContentText(quote)
            .setStyle(NotificationCompat.BigTextStyle().bigText(quote).setSummaryText(reference))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(date.toEpochDay().toInt(), notification)
    }

    private fun canNotify(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return false
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}

/** Re-creates the reminders after a reboot, app update, or clock / time-zone change. */
class RescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                ReminderScheduler.rescheduleFromSettings(context.applicationContext)
            } finally {
                pending.finish()
            }
        }
    }
}
