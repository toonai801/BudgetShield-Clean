package com.toonai.budgetshield.ui.screens

import com.toonai.budgetshield.ui.LocalBillRepository
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.ui.viewmodel.BillPaymentViewModel
import kotlinx.coroutines.launch

// Premium gamified dark theme colors (matching Home)
private val BackgroundDark = Color(0xFF02070D)
private val PanelDark = Color(0xFF06121D)
private val PanelBorder = Color(0xFF14364A)
private val CyanAccent = Color(0xFF17E8F2)
private val GreenAccent = Color(0xFF2FE6A7)
private val GoldAccent = Color(0xFFFFC545)
private val BlueAccent = Color(0xFF1678B9)
private val DangerColor = Color(0xFFFF553D)
private val TextPrimary = Color(0xFFF4F7FB)
private val TextMuted = Color(0xFFA6B1BF)

@Composable
fun BillPaymentScreen(
    billId: Long?,
    viewModel: BillPaymentViewModel = viewModel(factory = BillPaymentViewModel.Factory(LocalBillRepository.current)),
    onPaymentComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val bill by viewModel.getBill(billId).collectAsState(initial = null)
    
    var amount by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Set default amount when bill loads
    if (bill != null && amount.isEmpty()) {
        amount = bill!!.formattedRemainingDue.removePrefix("$")
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
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
                // Header
                HeaderSection()

                // Error message
                if (errorMessage != null) {
                    ErrorBanner(message = errorMessage!!)
                }

                if (bill == null && billId != null) {
                    // Bill not found
                    BillNotFoundState(onCancel = onCancel)
                } else if (bill == null) {
                    // No bill selected - generic pay screen
                    GenericPaymentState(
                        amount = amount,
                        onAmountChange = { amount = it },
                        onConfirm = onPaymentComplete,
                        onCancel = onCancel
                    )
                } else {
                    // Show bill details and payment form
                    BillSummaryCard(bill = bill!!)

                    PaymentAmountSection(
                        bill = bill!!,
                        amount = amount,
                        onAmountChange = { 
                            // Only allow valid decimal input
                            if (it.matches(Regex("^\\d*\\.?\\d{0,2}$")) || it.isEmpty()) {
                                amount = it
                            }
                        },
                        onQuickAmount = { amount = it }
                    )

                    PaymentMethodSection()

                    XPRewardCard(bill = bill!!)

                    ActionButtons(
                        isEnabled = amount.isNotBlank() && amount.toDoubleOrNull() != null,
                        isProcessing = isProcessing,
                        onConfirm = {
                            scope.launch {
                                isProcessing = true
                                errorMessage = null
                                
                                val paymentCents = (amount.toDouble() * 100).toLong()
                                
                                if (paymentCents <= 0) {
                                    errorMessage = "Payment amount must be greater than $0.00"
                                    isProcessing = false
                                    return@launch
                                }
                                
                                if (paymentCents > bill!!.remainingDueCents) {
                                    errorMessage = "Payment cannot exceed remaining due amount"
                                    isProcessing = false
                                    return@launch
                                }
                                
                                val success = viewModel.payBill(bill!!.id, paymentCents)
                                
                                if (success) {
                                    onPaymentComplete()
                                } else {
                                    errorMessage = "Payment failed. Please try again."
                                }
                                
                                isProcessing = false
                            }
                        },
                        onCancel = onCancel
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DangerColor.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, DangerColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("⚠️", fontSize = 20.sp)
            Text(
                text = message,
                color = DangerColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun BillNotFoundState(onCancel: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(DangerColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text("❓", fontSize = 40.sp)
        }
        
        Text(
            text = "Bill Not Found",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "The bill you're trying to pay could not be found.",
            color = TextMuted,
            fontSize = 14.sp
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = PanelDark
            ),
            onClick = onCancel
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "← Go Back",
                    color = TextPrimary,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun GenericPaymentState(
    amount: String,
    onAmountChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Make a Payment",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Enter payment amount:",
            color = TextMuted,
            fontSize = 14.sp
        )
        
        TextField(
            value = amount,
            onValueChange = onAmountChange,
            placeholder = { Text("0.00") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0D1B26),
                unfocusedContainerColor = Color(0xFF0D1B26),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = CyanAccent,
                unfocusedIndicatorColor = PanelBorder,
                focusedPlaceholderColor = TextMuted,
                unfocusedPlaceholderColor = TextMuted
            ),
            singleLine = true,
            leadingIcon = {
                Text(
                    text = "$",
                    color = CyanAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        )
        
        ActionButtons(
            isEnabled = amount.isNotBlank() && amount.toDoubleOrNull() != null,
            isProcessing = false,
            onConfirm = onConfirm,
            onCancel = onCancel
        )
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BlueAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💳",
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = "Pay Bill",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Secure payment",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun BillSummaryCard(bill: Bill) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bill Details",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Bill info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(BlueAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = bill.icon,
                            fontSize = 24.sp
                        )
                    }

                    Column {
                        Text(
                            text = bill.name,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Due ${bill.dueDate}",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = bill.formattedRemainingDue,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (bill.isProtected) "🛡️" else "⚠️",
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (bill.isProtected) "Protected" else "Unprotected",
                            color = if (bill.isProtected) CyanAccent else GoldAccent,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Progress bar showing paid amount
            if (bill.paidAmountCents > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Paid: ${Bill.formatCents(bill.paidAmountCents)}",
                            color = GreenAccent,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Remaining: ${bill.formattedRemainingDue}",
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF14364A))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(bill.paymentProgress / 100f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(GreenAccent)
                        )
                    }
                }
            }

            // Protected notice
            if (bill.isProtected) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CyanAccent.copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🛡️",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "This bill is protected. Paying on time earns XP!",
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentAmountSection(
    bill: Bill,
    amount: String,
    onAmountChange: (String) -> Unit,
    onQuickAmount: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Payment Amount",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            TextField(
                value = amount,
                onValueChange = onAmountChange,
                placeholder = { Text(bill.formattedRemainingDue) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF0D1B26),
                    unfocusedContainerColor = Color(0xFF0D1B26),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedIndicatorColor = CyanAccent,
                    unfocusedIndicatorColor = PanelBorder,
                    focusedPlaceholderColor = TextMuted,
                    unfocusedPlaceholderColor = TextMuted
                ),
                singleLine = true,
                leadingIcon = {
                    Text(
                        text = "$",
                        color = CyanAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )

            // Quick select buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val remainingFormatted = bill.formattedRemainingDue.removePrefix("$")
                QuickAmountChip("Full: $$remainingFormatted") { onQuickAmount(remainingFormatted) }
                
                val halfAmount = (bill.remainingDueCents / 200.0)
                val halfFormatted = String.format("%.2f", halfAmount)
                QuickAmountChip("Half: $$halfFormatted") { onQuickAmount(halfFormatted) }
            }
        }
    }
}

@Composable
private fun QuickAmountChip(label: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D1B26)
        ),
        border = BorderStroke(1.dp, PanelBorder),
        onClick = onClick
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun PaymentMethodSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Payment Method",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Payment method options
            PaymentMethodOption(
                icon = "🏦",
                name = "Bank Account",
                details = "**** 4567",
                selected = true
            )

            PaymentMethodOption(
                icon = "💳",
                name = "Credit Card",
                details = "**** 8901",
                selected = false
            )
        }
    }
}

@Composable
private fun PaymentMethodOption(
    icon: String,
    name: String,
    details: String,
    selected: Boolean
) {
    val borderColor = if (selected) CyanAccent else PanelBorder
    val bgColor = if (selected) CyanAccent.copy(alpha = 0.1f) else Color(0xFF0D1B26)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor
        ),
        border = BorderStroke(1.dp, borderColor)
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
                        .clip(CircleShape)
                        .background(
                            if (selected) CyanAccent.copy(alpha = 0.2f) else PanelBorder.copy(alpha = 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 20.sp
                    )
                }

                Column {
                    Text(
                        text = name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = details,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            if (selected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(CyanAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = BackgroundDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PanelBorder)
                )
            }
        }
    }
}

@Composable
private fun XPRewardCard(bill: Bill) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (bill.isProtected) GoldAccent.copy(alpha = 0.1f) else PanelDark
        ),
        border = if (bill.isProtected) BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)) else null
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
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (bill.isProtected) GoldAccent.copy(alpha = 0.2f) else PanelBorder.copy(alpha = 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (bill.isProtected) "⭐" else "🪙",
                        fontSize = 24.sp
                    )
                }

                Column {
                    Text(
                        text = if (bill.isProtected) "XP Reward" else "Payment",
                        color = if (bill.isProtected) GoldAccent else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (bill.isProtected) "+50 XP for protected payment" else "Mark bill as paid",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            if (bill.isProtected) {
                Text(
                    text = "+50 XP",
                    color = GoldAccent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    isEnabled: Boolean,
    isProcessing: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Confirm Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isEnabled) CyanAccent else CyanAccent.copy(alpha = 0.3f)
            ),
            onClick = { if (isEnabled && !isProcessing) onConfirm() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isProcessing) "Processing..." else "🛡️ Confirm Payment",
                    color = BackgroundDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Cancel Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = PanelDark
            ),
            onClick = onCancel
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cancel",
                    color = DangerColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}
