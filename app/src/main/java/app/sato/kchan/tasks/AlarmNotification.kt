package app.sato.kchan.tasks

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.sato.kchan.tasks.fanction.Note
import app.sato.kchan.tasks.fanction.NoteManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


class AlarmNotification : BroadcastReceiver() {
    // データを受信した
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("id", 0)
        val deleteTime = intent.getLongExtra("deleteTime", 0)

        val homeIntent = Intent(context, HomeActivity::class.java).apply(){
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, homeIntent,
            0
        )
        val channelId = "notice"
        // app name
        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("content")

        // Notification　Channel 設定
        val channel = NotificationChannel(
            channelId, "通知",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setSound(null, null)
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)


        if (deleteTime == 0.toLong()) {
            builder
                .setAutoCancel(true) // アプリ起動したら消えるか
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        } else {
            builder
                .setAutoCancel(false) // アプリ起動したら消えるか
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setTimeoutAfter(deleteTime)
        }

        val notification = builder.build()
        notification.flags = Notification.FLAG_NO_CLEAR

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        // 通知
        notificationManagerCompat.notify(notificationId, notification)
    }
}
