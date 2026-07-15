package com.toonai.budgetshield.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TransactionDetailsScreen(
    transactionId: Long? = null,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTreasure: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToGoals: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Transaction Details",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "ARCHITECTURE FOUNDATION - NOT FINAL UI",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Transaction ID: ${transactionId ?: "[not set]"}")
                    Text("Type: [placeholder]")
                    Text("Amount: $--.--")
                    Text("Date: [placeholder]")
                    Text("Description: [placeholder]")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Actions:",
                style = MaterialTheme.typography.titleMedium
            )

            TextButton(onClick = { /* Future: Edit */ }) {
                Text("Edit Transaction")
            }

            TextButton(
                onClick = { /* Future: Delete */ },
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Transaction")
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Direct exits per SCREEN_MAP.md:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column {
                Button(onClick = onNavigateToHome) {
                    Text("Home")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateToTreasure) {
                    Text("Treasure")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateToStats) {
                    Text("Stats")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateToGoals) {
                    Text("Goals")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onNavigateBack) {
                Text("← Back (Previous Screen)")
            }
        }
    }
}
