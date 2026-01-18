package com.application.quickkartadmin.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.application.quickkartadmin.MainActivity
import com.application.quickkartadmin.R
import com.application.quickkartadmin.core.network.RetrofitClient
import com.application.quickkartadmin.core.util.PreferencesManager
import com.application.quickkartadmin.data.remote.api.AdminApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.create

class FirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FirebaseService", "Received message: ${remoteMessage.data}")
        Log.d("FirebaseService", "Notification title: ${remoteMessage.notification?.title}")
        Log.d("FirebaseService", "Notification body: ${remoteMessage.notification?.body}")

        val data = remoteMessage.data
        val title = remoteMessage.notification?.title ?: "Admin Alert"
        val body = remoteMessage.notification?.body ?: ""

        showNotification(title, body, data)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send FCM token to backend
        sendTokenToBackend(token)
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        Log.d("FirebaseService", "Showing notification: $title - $body")

        val channelId = "admin_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Admin Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            Log.d("FirebaseService", "Notification channel created")
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

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
        Log.d("FirebaseService", "Notification displayed with ID: $notificationId")
    }

    private fun sendTokenToBackend(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val preferencesManager = PreferencesManager(applicationContext)
                val authToken = preferencesManager.getToken()

                if (!authToken.isNullOrEmpty()) {
                    val retrofit = RetrofitClient.getAuthenticatedClient(preferencesManager)
                    val adminApi = retrofit.create(AdminApi::class.java)

                    val request = mapOf("fcm_token" to token)
                    val response: Response<Map<String, String>> = adminApi.updateFcmToken(request)

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