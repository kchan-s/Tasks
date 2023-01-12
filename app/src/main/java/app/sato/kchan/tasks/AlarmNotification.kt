package app.sato.kchan.tasks

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*


class AlarmNotification : BroadcastReceiver() {
    // データを受信した
    override fun onReceive(context: Context, intent: Intent) {
        val requestCode = intent.getIntExtra("RequestCode", 0)
        val pendingIntent = PendingIntent.getActivity(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = "default"
        // app name
        val title: String = context.getString(R.string.app_name)
        val currentTime = System.currentTimeMillis()
        val dataFormat = SimpleDateFormat("HH:mm:ss", Locale.JAPAN)
        val cTime = dataFormat.format(currentTime)

        // メッセージ　+ 11:22:331
        val message = "時間になりました。 $cTime"


        // Notification　Channel 設定
        val channel = NotificationChannel(
            channelId, title,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = message
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        val a = Calendar.getInstance()
        a.set(2023, 0, 12, 16, 15, 0)
        val i = Calendar.getInstance()
        i.set(2023, 1, 12, 16, 15, 10)
        val time = a.timeInMillis - i.timeInMillis
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("My notification")
            .setContentText(message)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false) // アプリ起動したら消えるか
            .setTimeoutAfter(time)

        val notificationManagerCompat = NotificationManagerCompat.from(context)

        val notification = builder.build()
        notification.flags = Notification.FLAG_NO_CLEAR

        // 通知
        notificationManagerCompat.notify(R.string.app_name, notification)
    }
}