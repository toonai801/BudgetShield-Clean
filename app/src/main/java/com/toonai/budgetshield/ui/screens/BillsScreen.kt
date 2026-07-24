package com.toonai.budgetshield.ui.screens

import com.toonai.budgetshield.ui.LocalBillRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.ui.viewmodel.BillsUiState
import com.toonai.budgetshield.ui.viewmodel.BillsViewModel



import com.toonai.budgetshield.theme.BackgroundDark
import com.toonai.budgetshield.theme.PanelDark
import com.toonai.budgetshield.theme.PanelBorder
import com.toonai.budgetshield.theme.CyanAccent
import com.toonai.budgetshield.theme.CyanSoft
import com.toonai.budgetshield.theme.GreenAccent
import com.toonai.budgetshield.theme.GoldAccent
import com.toonai.budgetshield.theme.GoldAccent20
import com.toonai.budgetshield.theme.BlueAccent
import com.toonai.budgetshield.theme.PurpleAccent
import com.toonai.budgetshield.theme.TextPrimary
import com.toonai.budgetshield.theme.TextMuted
import com.toonai.budgetshield.theme.Warning

// Premium gamified dark theme - Bills & Payments Edition

@Composable
fun BillsScreen(
    viewModel: BillsViewModel = viewModel(factory = BillsViewModel.Factory(LocalBillRepository.current)),
    onNavigateToBillEntry: () -> Unit,
    onNavigateToBillPayment: (Long) -> Unit,
    onNavigateToTransactionDetails: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTreasure: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToGoals: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bills_screen")
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundDark,
                            Color(0xFF06121D),
                            Color(0xFF0A1A2E)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with back navigation
                BillsHeader(onNavigateToHome = onNavigateToHome)

                // Protected Money Vault Card
                ProtectedMoneyCard(
                    protectedAmount = uiState.formattedProtectedAmount,
                    totalUnpaid = uiState.formattedTotalUnpaid,
                    protectionPercentage = uiState.protectionPercentage
                )

                // Bills Protection Status
                ProtectionSummary(
                    protectedCount = uiState.protectedCount,
                    unprotectedCount = uiState.unprotectedCount,
                    protectedAmount = uiState.formattedProtectedAmount,
                    unprotectedAmount = uiState.formattedUnprotectedAmount
                )

                // Bills List or Empty State
                if (uiState.hasBills) {
                    BillsListSection(
                        bills = uiState.bills,
                        onPayBill = onNavigateToBillPayment,
                        onAddBill = onNavigateToBillEntry
                    )
                } else {
                    EmptyBillsState(onAddBill = onNavigateToBillEntry)
                }

                // Transaction History Link
                HistorySection(onViewHistory = onNavigateToTransactionDetails)
            }
        }


    }
}

@Composable
private fun BillsHeader(onNavigateToHome: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bills icon with glow
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CyanAccent.copy(alpha = 0.3f),
                                CyanAccent.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📜",
                    fontSize = 24.sp
                )
            }

            Column {
                Text(
                    text = "Bills & Payments",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage Your Obligations",
                    color = CyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Close/Back button
        TextButton(
            onClick = onNavigateToHome,
            contentPadding = PaddingValues(8.dp)
        ) {
            Text(
                text = "✕",
                color = TextMuted,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun ProtectedMoneyCard(
    protectedAmount: String,
    totalUnpaid: String,
    protectionPercentage: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1A3A4A),
                            Color(0xFF0D2430)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .drawBehind {
                    // Glowing border effect
                    drawRect(
                        color = CyanAccent.copy(alpha = 0.3f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
                .padding(20.dp)
        ) {
            // Decorative shield illustration
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CyanAccent.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🛡️",
                        fontSize = 48.sp
                    )
                    Text(
                        text = "SHIELD",
                        color = CyanAccent.copy(alpha = 0.6f),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Shield badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(GreenAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🛡️",
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "Protected Money",
                        color = GreenAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Amount
                Text(
                    text = protectedAmount,
                    color = TextPrimary,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                // Total bills context
                Text(
                    text = "of $totalUnpaid total bills",
                    color = TextMuted,
                    fontSize = 13.sp
                )

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(top = 8.dp)
                ) {
                    // Background track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF14364A))
                    )
                    // Progress fill
                    val progressWidth = (protectionPercentage.coerceIn(0, 100) / 100f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressWidth)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(GreenAccent, CyanAccent)
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ProtectionSummary(
    protectedCount: Int,
    unprotectedCount: Int,
    protectedAmount: String,
    unprotectedAmount: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Protected count
        StatusPill(
            modifier = Modifier.weight(1f),
            icon = "🛡️",
            label = "Protected",
            count = protectedCount,
            amount = protectedAmount,
            color = GreenAccent
        )

        // Unprotected count
        StatusPill(
            modifier = Modifier.weight(1f),
            icon = "⚠️",
            label = "Needs Shield",
            count = unprotectedCount,
            amount = unprotectedAmount,
            color = Warning
        )
    }
}

@Composable
private fun StatusPill(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    count: Int,
    amount: String,
    color: Color
) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = icon, fontSize = 14.sp)
                Text(
                    text = label,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = count.toString(),
                    color = color,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = amount,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EmptyBillsState(onAddBill: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(CyanAccent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📜",
                    fontSize = 32.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "No Bills Yet",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Add your first bill to start protecting your money",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }

            Button(
                onClick = onAddBill,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                modifier = Modifier.testTag("bills_add_bill")
            ) {
                Text(
                    text = "+ Add Your First Bill",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BillsListSection(
    bills: List<Bill>,
    onPayBill: (Long) -> Unit,
    onAddBill: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Section header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📜",
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Your Bills",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onAddBill,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent.copy(alpha = 0.15f),
                        contentColor = CyanAccent
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("bills_add_bill")
                ) {
                    Text(
                        text = "+ Add Bill",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Bill cards
            bills.forEach { bill ->
                BillCard(
                    bill = bill,
                    onPay = { onPayBill(bill.id) }
                )
            }
        }
    }
}

@Composable
private fun BillCard(
    bill: Bill,
    onPay: () -> Unit
) {
    val statusColor = if (bill.isProtected) GreenAccent else Warning
    val statusBg = if (bill.isProtected) GreenAccent.copy(alpha = 0.1f) else Warning.copy(alpha = 0.1f)
    
    // Calculate days until due
    val daysLeft = calculateDaysUntilDue(bill.dueDate)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D1B26)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bill icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bill.icon,
                        fontSize = 20.sp
                    )
                }

                Column {
                    // Name with status badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = bill.name,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        // Status badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(statusBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (bill.isProtected) "🛡️ Protected" else "⚠️ Unprotected",
                                color = statusColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Due date with urgency
                    val dateColor = when {
                        daysLeft <= 3 -> Color(0xFFFF553D)
                        daysLeft <= 7 -> Warning
                        else -> TextMuted
                    }

                    val dueText = if (daysLeft < 0) {
                        "Overdue ${kotlin.math.abs(daysLeft)} days"
                    } else if (daysLeft == 0) {
                        "Due today"
                    } else {
                        "Due ${bill.dueDate} • $daysLeft days"
                    }

                    Text(
                        text = dueText,
                        color = dateColor,
                        fontSize = 12.sp
                    )
                }
            }

            // Amount and Pay button
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = bill.formattedRemainingDue,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (!bill.isPaid) {
                    // Every unpaid bill shows Pay Bill button
                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanAccent,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Pay Bill",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Fully paid bills show paid status
                    Text(
                        text = "✓ Paid",
                        color = GreenAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun HistorySection(onViewHistory: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = onViewHistory
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BlueAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📜",
                        fontSize = 18.sp
                    )
                }

                Column {
                    Text(
                        text = "Payment History",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "View all transactions",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = "›",
                color = CyanAccent,
                fontSize = 24.sp
            )
        }
    }
}

// Helper function to calculate days until due date
private fun calculateDaysUntilDue(dueDate: String): Int {
    return try {
        // Simple date parsing - expects "YYYY-MM-DD" format
        val parts = dueDate.split("-")
        if (parts.size == 3) {
            val due = java.time.LocalDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            val today = java.time.LocalDate.now()
            java.time.temporal.ChronoUnit.DAYS.between(today, due).toInt()
        } else 30
    } catch (e: Exception) {
        30 // Default fallback
    }
}
