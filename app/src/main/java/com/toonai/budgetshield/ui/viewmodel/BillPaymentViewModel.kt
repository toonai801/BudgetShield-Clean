package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.repository.BillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * ViewModel for Bill Payment screen.
 * Handles loading bill details and processing payments.
 */
class BillPaymentViewModel(private val repository: BillRepository) : ViewModel() {

    /**
     * Get a bill by ID as a reactive Flow.
     * Returns null if billId is null.
     */
    fun getBill(billId: Long?): Flow<Bill?> {
        return if (billId != null) {
            repository.getBillByIdFlow(billId)
        } else {
            flowOf(null)
        }
    }

    /**
     * Process a payment for a bill.
     *
     * @param billId The bill to pay
     * @param paymentCents Amount to pay in cents
     * @return true if payment succeeded, false otherwise
     */
    suspend fun payBill(billId: Long, paymentCents: Long): Boolean {
        return repository.payBill(billId, paymentCents)
    }

    /**
     * Factory for creating ViewModel with repository dependency.
     */
    class Factory(private val repository: BillRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BillPaymentViewModel::class.java)) {
                return BillPaymentViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
