package com.application.quickkartdeliverypartner.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.application.quickkartdeliverypartner.MainActivity
import com.application.quickkartdeliverypartner.R
import com.application.quickkartdeliverypartner.core.network.RetrofitClient
import com.application.quickkartdeliverypartner.core.util.PreferencesManager
import com.application.quickkartdeliverypartner.data.remote.api.AuthApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class FirebaseMessageService : FirebaseMessagingService() {
    override fun onMessageReceived(remotemessage: RemoteMessage) {
        super.onMessageReceived(remotemessage)

        Log.d("FCM", "Delivery Partner received FCM message")
        Log.d("FCM", "Notification: ${remotemessage.notification}")
        Log.d("FCM", "Data: ${remotemessage.data}")

        val data = remotemessage.data
        val title = remotemessage.notification?.title ?: "New Notification"
        val body = remotemessage.notification?.body ?: ""

        showNotification(title, body, data)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendTokenToBackend(token)
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val channelId = "delivery_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelId,
                "Delivery Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            data["order_id"]?.let { putExtra("order_id", it) }
            data["type"]?.let { putExtra("notification_type", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

    }
    private fun sendTokenToBackend(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val preferencesManager = PreferencesManager(applicationContext)
                val authToken = preferencesManager.getToken()

                if (!authToken.isNullOrEmpty()) {
                    val retrofit = RetrofitClient.getAuthenticatedClient()
                    val authApi = retrofit.create(AuthApi::class.java)

                    val request = mapOf("fcm_token" to token)
                    val response: Response<Map<String, String>> = authApi.updateFcmToken(request)

                    if (response.isSuccessful) {
                        Log.d("FCM", "Token sent to backend successfully")
                    } else {
                        Log.e("FCM", "Failed to send token to backend: ${response.message()}")
                    }
                } else {
                    Log.w("FCM", "No auth token available, skipping FCM token update")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error sending token to backend: ${e.message}")
            }
        }
    }
}
