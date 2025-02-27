package app.sato.kchan.tasks

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import app.sato.kchan.tasks.fanction.Note
import app.sato.kchan.tasks.fanction.NoteManager
import app.sato.kchan.tasks.fanction.NoticeManager
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.schedule


class ForegroundNotificationService : Service(), LocationListener{
    lateinit var context: Context

    override fun onStartCommand(intent: Intent?, flag: Int, startId: Int): Int {
        super.onStartCommand(intent, flag, startId)

        context = applicationContext
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val foregroundNotificationChannel =
            NotificationChannel("foregroundService", "TaSks", NotificationManager.IMPORTANCE_MIN).apply {
                setSound(null, null)
            }
        manager.createNotificationChannel(foregroundNotificationChannel)

        val sendIntent = Intent(this, LocationBroadcastReceiver::class.java).apply {
            action = ACTION_SEND
        }

        val mainIntent = Intent(context, HomeActivity::class.java).apply() {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, mainIntent, 0)
        val sendPendingIntent = PendingIntent.getBroadcast(this, 0, sendIntent, 0)

        val builder = Notification.Builder(this, "foregroundService")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("起動中…")
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "TaSksを停止する", sendPendingIntent)
        val foregroundNotification = builder.build()

        startForeground(1, foregroundNotification)

        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1F, this)
        }

        // 同期
        Timer().schedule(0,600000) {
            val noteManager = NoteManager()

            noteManager.search("")
            for (i in 0 until noteManager.getNoteNumber()) {
                val copyNoteManager = noteManager.copy()
                copyNoteManager.select(i)

                val note = copyNoteManager.getNote()!!
                val uuid: Int
                if (note.getNoticeBarId() == 0) {
                    uuid = UUID.randomUUID().hashCode()
                    note.setNoticeBarId(uuid)
                } else {
                    uuid = note.getNoticeBarId()!!
                }

                val notificationManager =
                    HomeActivity.context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.getNotificationChannel("notification")
                notificationManager.cancelAll()
                cancelAlarm(context, uuid)

                if (note.getNoticeShow() != null && note.getNoticeLocation() == null) {
                    setAlarm(context, note, uuid)
                }
            }

        }
        return START_STICKY
    }

    fun setAlarm(context: Context, note: Note, uuid: Int) {
        val intent = Intent(context, AlarmNotification::class.java)
        val start = note.getNoticeShow()!!
        var startCalendar: Calendar? = null
        val stop = note.getNoticeHide()
        val stopCalendar = Calendar.getInstance()

        if (!note.isComplete()) {
            if (stop != null) {
                if (start.isAfter(LocalDateTime.now()) || start.isEqual(LocalDateTime.now())) {
                    startCalendar = Calendar.getInstance()
                    startCalendar.set(
                        start.year,
                        start.monthValue - 1,
                        start.dayOfMonth,
                        start.hour,
                        start.minute,
                        0
                    )
                } else if (start.isBefore(LocalDateTime.now())
                    && (stop.isAfter(LocalDateTime.now()) || stop.isEqual(LocalDateTime.now()))
                ) {
                    startCalendar = Calendar.getInstance()
                    val now = LocalDateTime.now()
                    startCalendar.set(
                        now.year,
                        now.monthValue - 1,
                        now.dayOfMonth,
                        now.hour,
                        now.minute,
                        now.second
                    )
                }
                if (startCalendar != null) {
                    stopCalendar.set(
                        stop.year,
                        stop.monthValue - 1,
                        stop.dayOfMonth,
                        stop.hour,
                        stop.minute,
                        0
                    )
                    intent.putExtra(
                        "deleteTime",
                        stopCalendar.timeInMillis - startCalendar.timeInMillis
                    )
                }
            } else {
                startCalendar = Calendar.getInstance()
                startCalendar.set(
                    start.year,
                    start.monthValue - 1,
                    start.dayOfMonth,
                    start.hour,
                    start.minute,
                    0
                )
            }

            if (startCalendar != null) {
                intent.putExtra("id", uuid)
                intent.putExtra("title", note.getTitle())
                intent.putExtra("content", note.getContent())

                val pending = PendingIntent.getBroadcast(
                    context, uuid, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                // アラームをセットする
                val am =
                    context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
                am.setExact(
                    AlarmManager.RTC_WAKEUP,
                    startCalendar.getTimeInMillis(), pending
                )
            }
        }
    }

    fun cancelAlarm(context: Context, uuid: Int) {
        val intent = Intent(context, AlarmNotification::class.java)
        val pending = PendingIntent.getBroadcast(
            context, uuid, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // アラームを解除する
        val am = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        am.cancel(pending)
    }

    @Nullable
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        val notificationManager =
            HomeActivity.context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.getNotificationChannel("notification")
        notificationManager.cancelAll()

        val noticeManager = NoticeManager()
        val locationManager = app.sato.kchan.tasks.fanction.LocationManager()
        locationManager.searchByRadius(
            location.latitude.toFloat(),
            location.longitude.toFloat(),
            75
        )
        if (locationManager.isLocation()) {
            do {
                noticeManager.searchByLocation(locationManager.getLocation()!!)
                if (noticeManager.getNoticeNumber() != 0) {
                    val note = noticeManager.getNote()!!
                    val uuid: Int
                    if (note.getNoticeBarId() == 0) {
                        uuid = UUID.randomUUID().hashCode()
                        note.setNoticeBarId(uuid)
                    } else {
                        uuid = note.getNoticeBarId()!!
                    }
                    if (!note.isComplete() && note.getNoticeShow() == null) {
                        val mainIntent = Intent(context, HomeActivity::class.java).apply() {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }

                        val pendingIntent: PendingIntent =
                            PendingIntent.getActivity(context, 0, mainIntent, 0)
                        val channelId = "notification"
                        val channel = NotificationChannel(
                            channelId, "通知",
                            NotificationManager.IMPORTANCE_DEFAULT
                        ).apply {
                            setSound(null, null)
                        }

//                         Register the channel with the system
                        val notificationManager =
                            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.createNotificationChannel(channel)

                        //通知オブジェクトの作成
                        val builder = NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(note.getTitle())
                            .setContentText(note.getContent())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)

                        val notification = builder.build()
                        notification.flags = Notification.FLAG_NO_CLEAR

                        //通知の実施
                        notificationManager.notify(uuid, notification)
                    } else if (!note.isComplete()) {
                        setAlarm(context, note, uuid)
                    }
                }
            } while (locationManager.next())
        }
    }
}
