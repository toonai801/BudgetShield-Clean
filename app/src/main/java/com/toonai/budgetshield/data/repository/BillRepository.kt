package com.toonai.budgetshield.data.repository

import androidx.room.withTransaction
import com.toonai.budgetshield.data.database.BillDao
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.Transaction
import com.toonai.budgetshield.data.model.TransactionCategories
import com.toonai.budgetshield.data.model.XpActivityTypes
import com.toonai.budgetshield.data.model.XpEntry
import com.toonai.budgetshield.util.DateParser
import kotlinx.coroutines.flow.Flow

/**
 * Repository for bill operations.
 * Single source of truth for bill data, abstracts DAO operations.
 */
class BillRepository(
    private val database: BudgetShieldDatabase
) {
    private val billDao: BillDao = database.billDao()
    
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

        return database.withTransaction {
            val bill = billDao.getBillById(billId) ?: return@withTransaction false
            val remaining = bill.remainingDueCents

            if (paymentCents > remaining) return@withTransaction false

            val newPaidAmount = bill.paidAmountCents + paymentCents
            val isNowFullyPaid = newPaidAmount >= bill.amountCents

            val updatedBill = bill.copy(
                paidAmountCents = newPaidAmount,
                isPaid = isNowFullyPaid
            )

            billDao.updateBill(updatedBill)

            val today = DateParser.today()
            val transactionId = database.transactionDao().insertTransaction(
                Transaction(
                    type = Transaction.TYPE_BILL_PAYMENT,
                    title = "Paid ${bill.name}",
                    description = "Payment toward ${bill.name}",
                    amountCents = -paymentCents,
                    category = TransactionCategories.BILLS,
                    icon = bill.icon,
                    relatedBillId = bill.id,
                    earnsXp = true,
                    xpEarned = XpActivityTypes.baseXp(XpActivityTypes.PAY_BILL),
                    isProtected = bill.isProtected,
                    transactionDate = today
                )
            )

            database.xpEntryDao().insertXpEntry(
                XpEntry(
                    amount = XpActivityTypes.baseXp(XpActivityTypes.PAY_BILL),
                    activityType = XpActivityTypes.PAY_BILL,
                    description = "Paid ${bill.name}",
                    relatedId = transactionId,
                    entryDate = today
                )
            )

            true
        }
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
