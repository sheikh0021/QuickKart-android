package com.application.quickkartcustomer.ui.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.quickkartcustomer.domain.model.AssignmentStatus

@Composable
fun DeliveryTimeline(
    status: AssignmentStatus,
    etaMinutes: Int?,
    deliveryPartnerName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = when {
                            status.delivered -> "Order Delivered!"
                            status.outForDelivery -> "On the way"
                            status.pickedUp -> "Picked up from store"
                            else -> "Preparing your order"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    if (!status.delivered) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (etaMinutes != null && etaMinutes <= 5) {
                                "Arriving in $etaMinutes minutes"
                            } else if (etaMinutes != null) {
                                "Estimated $etaMinutes minutes"
                            } else {
                                "Preparing..."
                            },
                            fontSize = 14.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (status.outForDelivery) {
                    Icon(
                        Icons.Default.DirectionsBike,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Timeline Steps
            TimelineRow(
                steps = listOf(
                    TimelineStep(
                        icon = Icons.Default.ShoppingCart,
                        label = "Placed",
                        isCompleted = true
                    ),
                    TimelineStep(
                        icon = Icons.Default.CheckCircle,
                        label = "Packed",
                        isCompleted = status.pickedUp || status.outForDelivery || status.delivered
                    ),
                    TimelineStep(
                        icon = Icons.Default.DirectionsBike,
                        label = "On the way",
                        isCompleted = status.outForDelivery || status.delivered,
                        isActive = status.outForDelivery && !status.delivered
                    ),
                    TimelineStep(
                        icon = Icons.Default.CheckCircle,
                        label = "Delivered",
                        isCompleted = status.delivered
                    )
                )
            )

            // Partner info (only show during delivery)
            if (status.outForDelivery && !status.delivered) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Delivery Partner",
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = deliveryPartnerName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF212121)
                        )
                    }

                    // Call button (optional)
                    IconButton(onClick = { /* Call delivery partner */ }) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "Call",
                            tint = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }
    }
}

data class TimelineStep(
    val icon: ImageVector,
    val label: String,
    val isCompleted: Boolean,
    val isActive: Boolean = false
)

@Composable
private fun TimelineRow(steps: List<TimelineStep>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            // Step icon and label
            TimelineStepItem(
                step = step,
                modifier = Modifier.weight(1f)
            )

            if (index < steps.size - 1) {
                TimelineConnector(
                    isCompleted = step.isCompleted,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TimelineStepItem(
    step: TimelineStep,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon circle
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background circle
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = when {
                        step.isCompleted -> Color(0xFF4CAF50)
                        step.isActive -> Color(0xFF2196F3)
                        else -> Color(0xFFE0E0E0)
                    }
                )
            }

            Icon(
                step.icon,
                contentDescription = step.label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Label
        Text(
            text = step.label,
            fontSize = 11.sp,
            color = if (step.isCompleted || step.isActive) {
                Color(0xFF212121)
            } else {
                Color(0xFF9E9E9E)
            },
            fontWeight = if (step.isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun TimelineConnector(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .height(2.dp)
            .padding(horizontal = 4.dp)
    ) {
        drawLine(
            color = if (isCompleted) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }
}