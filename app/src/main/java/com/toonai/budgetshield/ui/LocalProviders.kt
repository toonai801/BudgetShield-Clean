package com.toonai.budgetshield.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.toonai.budgetshield.data.repository.BillRepository

/**
 * CompositionLocal for accessing BillRepository in Compose UI.
 * Must be provided at the app root level.
 */
val LocalBillRepository = staticCompositionLocalOf<BillRepository> {
    error("BillRepository not provided. Wrap your app with BillRepositoryProvider.")
}
