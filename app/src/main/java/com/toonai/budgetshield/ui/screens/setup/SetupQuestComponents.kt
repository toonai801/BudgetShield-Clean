package com.toonai.budgetshield.ui.screens.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

// Premium gamified dark theme colors
private val PanelBackground = Color(0xFF0A1A2E)
private val TextMuted = Color(0xFFA6B1BF)
private val TextBright = Color(0xFFFFFFFF)
private val CyanAccent = Color(0xFF17E8F2)

/**
 * UI components for Setup Quest screens.
 */

@Composable
fun BillDraftCard(
    bill: SetupBillDraft,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PanelBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = bill.icon,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = bill.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextBright
                    )
                    Text(
                        text = "${formatCents(bill.amountCents)} • Due ${bill.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove bill",
                    tint = Color(0xFFFF6B6B)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBillDialog(
    onDismiss: () -> Unit,
    onAdd: (SetupBillDraft) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("") }
    var amountCents by remember { mutableStateOf<Long?>(null) }
    var dueDate by remember { mutableStateOf("") }
    var isProtected by remember { mutableStateOf(true) }

    val isValid = name.isNotBlank() && icon.isNotBlank() && amountCents != null && amountCents!! > 0 && dueDate.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Bill") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Bill Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = icon,
                    onValueChange = { icon = it },
                    label = { Text("Icon (emoji)") },
                    modifier = Modifier.fillMaxWidth()
                )

                SetupMoneyInputField(
                    value = amountCents,
                    onValueChanged = { amountCents = it },
                    label = "Amount",
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isProtected,
                        onCheckedChange = { isProtected = it }
                    )
                    Text("Protected (money set aside)", color = TextBright)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) {
                        onAdd(
                            SetupBillDraft(
                                name = name,
                                icon = icon,
                                amountCents = amountCents!!,
                                dueDate = dueDate,
                                isProtected = isProtected
                            )
                        )
                    }
                },
                enabled = isValid
            ) {
                Text("Add", color = CyanAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun SetupMoneyInputField(
    value: Long?,
    onValueChanged: (Long?) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var textValue by remember(value) {
        mutableStateOf(value?.let { cents ->
            val dollars = cents / 100
            val remainder = kotlin.math.abs(cents % 100)
            String.format("%d.%02d", dollars, remainder)
        } ?: "")
    }

    OutlinedTextField(
        value = textValue,
        onValueChange = { newText ->
            // Only allow numbers and decimal point
            val filtered = newText.filter { it.isDigit() || it == '.' }
            textValue = filtered

            // Parse to cents
            val parsed = try {
                if (filtered.contains(".")) {
                    val parts = filtered.split(".")
                    val dollars = parts[0].toLongOrNull() ?: 0
                    val cents = parts.getOrNull(1)?.take(2)?.padEnd(2, '0')?.toLongOrNull() ?: 0
                    dollars * 100 + cents
                } else {
                    (filtered.toLongOrNull() ?: 0) * 100
                }
            } catch (e: Exception) {
                null
            }
            onValueChanged(parsed)
        },
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
        prefix = { Text("$", color = TextMuted) }
    )
}

private fun formatCents(cents: Long): String {
    val dollars = cents / 100
    val remainder = kotlin.math.abs(cents % 100)
    return String.format("$%d.%02d", dollars, remainder)
}
