package com.application.quickkartcustomer.core.service
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.application.quickkartcustomer.MainActivity
import com.application.quickkartcustomer.R
import com.application.quickkartcustomer.core.network.RetrofitClient
import com.application.quickkartcustomer.core.util.PreferencesManager
import com.application.quickkartcustomer.data.remote.api.ProfileApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.create


class QuickKartFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let { notification ->
            showNotification(notification.title, notification.body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //send FCM token to backend
        sendTokenToBackend(token)
    }
    private fun showNotification(title: String?, body: String?){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "quickkkart_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title ?: "QuickKart")
            .setContentText(body ?: "You have a new notification")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //creating notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "QuickKart Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun sendTokenToBackend(token: String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val preferencesManager = PreferencesManager(applicationContext)
                val authToken = preferencesManager.getToken()

                if (!authToken.isNullOrEmpty()){
                    val retrofit = RetrofitClient.getAuthenticatedClient(authToken)
                    val profileApi = retrofit.create(ProfileApi::class.java)

                    val request = mapOf("fcm_token" to token)
                    val response: Response<Map<String, String>> = profileApi.updateFcmToken(request)

                    if (response.isSuccessful){
                        Log.d("FCM", "Token sent to backend successfully")
                    } else {
                        Log.e("FCM", "Failed to send token to backend: ${response.message()}")
                    }
                } else {
                    Log.w("FCM", "No auth token available, skipping FCM token update")
                }
            } catch (e: Exception){
                Log.e("FCM", "Error sending token to backend: ${e.message}")
            }
        }
    }
}