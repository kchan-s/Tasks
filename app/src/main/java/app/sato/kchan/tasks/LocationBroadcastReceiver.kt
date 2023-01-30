package app.sato.kchan.tasks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocationBroadcastReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val targetIntent = Intent(context, ForegroundNotificationService::class.java)
        context.stopService(targetIntent)
    }
}