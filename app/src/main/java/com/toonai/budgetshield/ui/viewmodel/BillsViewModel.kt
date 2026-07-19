package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.repository.BillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * UI state for Bills & Payments screen
 */
data class BillsUiState(
    val bills: List<Bill> = emptyList(),
    val protectedAmountCents: Long = 0L,
    val unprotectedAmountCents: Long = 0L,
    val protectedCount: Int = 0,
    val unprotectedCount: Int = 0,
    val totalUnpaidCents: Long = 0L,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val hasBills: Boolean
        get() = bills.isNotEmpty()
    
    val formattedProtectedAmount: String
        get() = Bill.formatCents(protectedAmountCents)
    
    val formattedTotalUnpaid: String
        get() = Bill.formatCents(totalUnpaidCents)
    
    val formattedUnprotectedAmount: String
        get() = Bill.formatCents(unprotectedAmountCents)
    
    /** Protection percentage (0-100+) of total bills */
    val protectionPercentage: Int
        get() = if (totalUnpaidCents > 0) {
            ((protectedAmountCents * 100) / totalUnpaidCents).toInt()
        } else 0
}

/**
 * ViewModel for Bills & Payments screen.
 * Manages bill data and protected money calculations.
 */
class BillsViewModel(private val repository: BillRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BillsUiState())
    val uiState: StateFlow<BillsUiState> = _uiState.asStateFlow()
    
    init {
        // Combine all repository flows into single UI state
        combine(
            repository.allBills,
            repository.totalProtectedCents,
            repository.totalUnpaidCents,
            repository.protectedCount,
            repository.unprotectedCount
        ) { bills, protectedCents, totalUnpaid, protectedCnt, unprotectedCnt ->
            val protectedAmount = protectedCents
            val totalUnpaidAmount = totalUnpaid
            val unprotectedAmount = maxOf(0L, totalUnpaidAmount - protectedAmount)
            
            BillsUiState(
                bills = bills,
                protectedAmountCents = protectedAmount,
                unprotectedAmountCents = unprotectedAmount,
                totalUnpaidCents = totalUnpaidAmount,
                protectedCount = protectedCnt,
                unprotectedCount = unprotectedCnt
            )
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }
    
    /**
     * Pay a bill with the specified amount.
     * 
     * @param billId The bill to pay
     * @param paymentCents Amount in cents
     * @return true if payment succeeded
     */
    fun payBill(billId: Long, paymentCents: Long, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val success = repository.payBill(billId, paymentCents)
            onResult(success)
        }
    }
    
    /**
     * Delete a bill.
     */
    fun deleteBill(billId: Long) {
        viewModelScope.launch {
            repository.deleteBill(billId)
        }
    }
    
    /**
     * Factory for creating ViewModel with repository dependency.
     */
    class Factory(private val repository: BillRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BillsViewModel::class.java)) {
                return BillsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
