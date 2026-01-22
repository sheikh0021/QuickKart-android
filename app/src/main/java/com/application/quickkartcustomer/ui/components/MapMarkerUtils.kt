package com.application.quickkartcustomer.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


object MapMarkerUtils {

    fun createDeliveryBikeIcon(
        context: Context,
        bearing: Float = 0f
    ): BitmapDescriptor {
        val size = 120
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Green circle background (motorcycle base) - matches image
        paint.color = 0xFF4CAF50.toInt() // Green
        paint.style = Paint.Style.FILL
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 8f, paint)

        // White border
        paint.color = 0xFFFFFFFF.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 8f, paint)

        canvas.save()
        canvas.rotate(bearing, size / 2f, size / 2f)

        // Yellow person icon (matches image - person on motorcycle)
        paint.style = Paint.Style.FILL
        paint.color = 0xFFFFEB3B.toInt() // Yellow
        
        // Head (circle)
        canvas.drawCircle(size / 2f, size / 2f - 20f, 12f, paint)
        
        // Body (rounded rectangle)
        val bodyPath = Path()
        val rect = android.graphics.RectF(
            size / 2f - 10f, size / 2f - 8f,
            size / 2f + 10f, size / 2f + 15f
        )
        bodyPath.addRoundRect(rect, 8f, 8f, Path.Direction.CW)
        canvas.drawPath(bodyPath, paint)

        // Motorcycle handlebars (white lines)
        paint.color = 0xFFFFFFFF.toInt()
        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE
        canvas.drawLine(size / 2f - 15f, size / 2f - 5f, size / 2f + 15f, size / 2f - 5f, paint)

        canvas.restore()

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    fun createCustomerHomeIcon(context: Context): BitmapDescriptor {
        val size = 100
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Blue pin shape (inverted teardrop) - matches image
        paint.color = 0xFF2196F3.toInt() // Blue
        paint.style = Paint.Style.FILL
        
        val pinPath = Path()
        // Top circle
        pinPath.addCircle(size / 2f, size / 3f, size / 4f, Path.Direction.CW)
        // Bottom point
        pinPath.moveTo(size / 2f, size / 3f + size / 4f)
        pinPath.lineTo(size / 2f - size / 6f, size - 5f)
        pinPath.lineTo(size / 2f, size - 10f)
        pinPath.lineTo(size / 2f + size / 6f, size - 5f)
        pinPath.close()
        canvas.drawPath(pinPath, paint)

        // White border
        paint.color = 0xFFFFFFFF.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawPath(pinPath, paint)

        // Yellow bullseye target in center (matches image)
        paint.style = Paint.Style.FILL
        paint.color = 0xFFFFEB3B.toInt() // Yellow
        
        // Outer circle
        canvas.drawCircle(size / 2f, size / 3f, 12f, paint)
        
        // Inner circle (white)
        paint.color = 0xFFFFFFFF.toInt()
        canvas.drawCircle(size / 2f, size / 3f, 8f, paint)
        
        // Center dot
        paint.color = 0xFFFFEB3B.toInt()
        canvas.drawCircle(size / 2f, size / 3f, 4f, paint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    fun createStoreIcon(context: Context): BitmapDescriptor {
        val size = 100
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Black circle background - matches image
        paint.color = 0xFF000000.toInt() // Black
        paint.style = Paint.Style.FILL
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 5f, paint)

        // White border
        paint.color = 0xFFFFFFFF.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 5f, paint)

        // Green building/storefront icon - matches image
        paint.style = Paint.Style.FILL
        paint.color = 0xFF4CAF50.toInt() // Green
        
        // Building shape (rectangle with triangle roof)
        val buildingPath = Path()
        // Base rectangle
        val rect = android.graphics.RectF(
            size / 2f - 20f, size / 2f - 5f,
            size / 2f + 20f, size / 2f + 15f
        )
        buildingPath.addRect(rect, Path.Direction.CW)
        // Triangle roof
        buildingPath.moveTo(size / 2f - 20f, size / 2f - 5f)
        buildingPath.lineTo(size / 2f, size / 2f - 15f)
        buildingPath.lineTo(size / 2f + 20f, size / 2f - 5f)
        buildingPath.close()
        canvas.drawPath(buildingPath, paint)

        // Door (white rectangle)
        paint.color = 0xFFFFFFFF.toInt()
        canvas.drawRect(
            size / 2f - 6f, size / 2f + 5f,
            size / 2f + 6f, size / 2f + 15f,
            paint
        )

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}