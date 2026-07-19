package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.repository.BillRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Bill Entry screen.
 * Handles creating new bills with validation.
 */
class BillEntryViewModel(private val repository: BillRepository) : ViewModel() {

    /**
     * Create a new bill with validation.
     *
     * @param name The bill name
     * @param icon Icon emoji for the bill
     * @param amountCents Amount in cents
     * @param dueDate Due date as YYYY-MM-DD string
     * @param isProtected Whether to protect this bill
     * @return true if bill was created successfully
     */
    suspend fun createBill(
        name: String,
        icon: String,
        amountCents: Long,
        dueDate: String,
        isProtected: Boolean
    ): Result<Long> {
        return try {
            // Validate inputs
            if (name.isBlank()) {
                return Result.failure(IllegalArgumentException("Bill name is required"))
            }
            if (amountCents <= 0) {
                return Result.failure(IllegalArgumentException("Amount must be greater than $0.00"))
            }
            if (dueDate.isBlank()) {
                return Result.failure(IllegalArgumentException("Due date is required"))
            }

            val billId = repository.createBill(
                name = name.trim(),
                icon = icon,
                amountCents = amountCents,
                dueDate = dueDate,
                isProtected = isProtected
            )

            Result.success(billId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Factory for creating ViewModel with repository dependency.
     */
    class Factory(private val repository: BillRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BillEntryViewModel::class.java)) {
                return BillEntryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
