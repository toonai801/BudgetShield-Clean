package com.toonai.budgetshield.data.repository

import com.toonai.budgetshield.data.database.BillDao
import com.toonai.budgetshield.data.model.Bill
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Repository for bill operations.
 * Single source of truth for bill data, abstracts DAO operations.
 */
class BillRepository(private val billDao: BillDao) {
    
    /** All bills as a reactive stream */
    val allBills: Flow<List<Bill>> = billDao.getAllBills()
    
    /** Total of all unpaid remaining amounts */
    val totalUnpaidCents: Flow<Long> = billDao.getTotalUnpaidCents()
    
    /** Total of protected unpaid amounts */
    val totalProtectedCents: Flow<Long> = billDao.getTotalProtectedCents()
    
    /** Count of protected unpaid bills */
    val protectedCount: Flow<Int> = billDao.getProtectedCount()
    
    /** Count of unprotected unpaid bills */
    val unprotectedCount: Flow<Int> = billDao.getUnprotectedCount()
    
    /** Get a specific bill by ID */
    suspend fun getBillById(billId: Long): Bill? {
        return billDao.getBillById(billId)
    }
    
    /** Get a specific bill by ID as Flow */
    fun getBillByIdFlow(billId: Long): Flow<Bill?> {
        return billDao.getBillByIdFlow(billId)
    }
    
    /** Create a new bill, returns the generated ID */
    suspend fun createBill(
        name: String,
        icon: String,
        amountCents: Long,
        dueDate: String,
        isProtected: Boolean = false
    ): Long {
        val bill = Bill(
            name = name,
            icon = icon,
            amountCents = amountCents,
            dueDate = dueDate,
            isProtected = isProtected
        )
        return billDao.insertBill(bill)
    }
    
    /**
     * Make a payment toward a bill.
     * Validates the payment amount and updates the bill.
     * 
     * @param billId The bill to pay
     * @param paymentCents Amount to pay in cents
     * @return true if payment succeeded, false if invalid
     */
    suspend fun payBill(billId: Long, paymentCents: Long): Boolean {
        if (paymentCents <= 0) return false
        
        val bill = billDao.getBillById(billId) ?: return false
        val remaining = bill.remainingDueCents
        
        if (paymentCents > remaining) return false
        
        val newPaidAmount = bill.paidAmountCents + paymentCents
        val isNowFullyPaid = newPaidAmount >= bill.amountCents
        
        val updatedBill = bill.copy(
            paidAmountCents = newPaidAmount,
            isPaid = isNowFullyPaid
        )
        
        billDao.updateBill(updatedBill)
        return true
    }
    
    /**
     * Mark a bill as protected (money set aside).
     */
    suspend fun protectBill(billId: Long): Boolean {
        val bill = billDao.getBillById(billId) ?: return false
        if (bill.isProtected) return true // Already protected
        
        val updatedBill = bill.copy(isProtected = true)
        billDao.updateBill(updatedBill)
        return true
    }
    
    /** Delete a bill */
    suspend fun deleteBill(billId: Long) {
        billDao.deleteBillById(billId)
    }
    
    /** Check if any bills exist */
    suspend fun hasBills(): Boolean {
        return billDao.getBillCount() > 0
    }
}
