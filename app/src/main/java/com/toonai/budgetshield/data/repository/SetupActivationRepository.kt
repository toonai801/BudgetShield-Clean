package com.toonai.budgetshield.data.repository

import androidx.room.withTransaction
import com.toonai.budgetshield.data.calculator.IncomeRecurrencePolicy
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.BudgetCategory
import com.toonai.budgetshield.data.model.BudgetCategoryType
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.IncomeFrequency
import com.toonai.budgetshield.data.model.UserSettings
import com.toonai.budgetshield.data.model.XpActivityTypes
import com.toonai.budgetshield.data.model.XpEntry
import com.toonai.budgetshield.util.DateParser

class SetupActivationRepository(
    private val database: BudgetShieldDatabase
) {
    data class ActivationBillDraft(
        val name: String,
        val icon: String,
        val amountCents: Long,
        val dueDate: String,
        val isProtected: Boolean
    )

    data class ActivationRequest(
        val cashOnHandCents: Long,
        val savingsBalanceCents: Long,
        val incomeName: String,
        val incomeAmountCents: Long,
        val nextPaydayDate: String,
        val frequency: String,
        val paydayAnchorDayOne: Int?,
        val paydayAnchorDayTwo: Int?,
        val isIncomeConfirmed: Boolean,
        val foodBudgetCents: Long,
        val wantsBudgetCents: Long,
        val bills: List<ActivationBillDraft>,
        val selectedMonth: String = DateParser.currentMonthKey()
    )

    data class ActivationResult(
        val activated: Boolean,
        val alreadyComplete: Boolean,
        val xpEarned: Int
    )

    suspend fun activate(request: ActivationRequest): ActivationResult {
        validate(request)

        return database.withTransaction {
            val settingsDao = database.userSettingsDao()
            val existingSettings = settingsDao.getSettingsSync()
            if (existingSettings?.isFirstRunComplete == true) {
                return@withTransaction ActivationResult(
                    activated = false,
                    alreadyComplete = true,
                    xpEarned = 0
                )
            }

            val nextPayday = request.incomeName.takeIf { it.isNotBlank() }?.let {
                DateParser.parseToIsoDate(request.nextPaydayDate).getOrThrow()
            }
            if (nextPayday != null && request.incomeAmountCents > 0) {
                database.incomeScheduleDao().insertSchedule(
                    IncomeSchedule(
                        name = request.incomeName.trim(),
                        amountCents = request.incomeAmountCents,
                        nextPayday = nextPayday,
                        nextPaydayDate = nextPayday,
                        frequency = request.frequency,
                        paydayAnchorDayOne = request.paydayAnchorDayOne,
                        paydayAnchorDayTwo = request.paydayAnchorDayTwo,
                        isConfirmed = request.isIncomeConfirmed
                    )
                )
            }

            upsertBudget("Food", BudgetCategoryType.FOOD, request.selectedMonth, request.foodBudgetCents, "🍽️")
            upsertBudget("Wants", BudgetCategoryType.WANTS, request.selectedMonth, request.wantsBudgetCents, "🎁")

            request.bills.forEach { draftBill ->
                if (draftBill.name.isNotBlank() && draftBill.amountCents > 0) {
                    database.billDao().insertBill(
                        Bill(
                            name = draftBill.name.trim(),
                            icon = draftBill.icon.ifBlank { "📄" },
                            amountCents = draftBill.amountCents,
                            dueDate = DateParser.parseToIsoDate(draftBill.dueDate).getOrThrow(),
                            isProtected = draftBill.isProtected
                        )
                    )
                }
            }

            settingsDao.insertSettings(
                UserSettings(
                    id = 1L,
                    isFirstRunComplete = true,
                    cashOnHandCents = request.cashOnHandCents,
                    savingsBalanceCents = request.savingsBalanceCents,
                    setupChapter = 7,
                    selectedMonth = request.selectedMonth
                )
            )

            database.setupDraftDao().clearDraft()

            val xpAmount = XpActivityTypes.baseXp(XpActivityTypes.COMPLETE_SETUP)
            database.xpEntryDao().insertXpEntry(
                XpEntry(
                    amount = xpAmount,
                    activityType = XpActivityTypes.COMPLETE_SETUP,
                    description = "Completed setup quest",
                    relatedId = 1L,
                    entryDate = DateParser.today()
                )
            )

            ActivationResult(
                activated = true,
                alreadyComplete = false,
                xpEarned = xpAmount
            )
        }
    }

    private suspend fun upsertBudget(
        name: String,
        type: String,
        monthKey: String,
        plannedAmountCents: Long,
        icon: String
    ) {
        val dao = database.budgetCategoryDao()
        val existing = dao.getBudgetForCategorySync(name, monthKey)
        if (existing == null) {
            dao.insertBudget(
                BudgetCategory(
                    name = name,
                    monthKey = monthKey,
                    plannedAmountCents = plannedAmountCents,
                    categoryType = type,
                    icon = icon
                )
            )
        } else {
            dao.updateBudget(
                existing.copy(
                    plannedAmountCents = plannedAmountCents,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    private fun validate(request: ActivationRequest) {
        require(request.cashOnHandCents >= 0L) { "Cash on hand cannot be negative" }
        require(request.savingsBalanceCents >= 0L) { "Savings balance cannot be negative" }
        require(request.foodBudgetCents >= 0L) { "Food budget cannot be negative" }
        require(request.wantsBudgetCents >= 0L) { "Wants budget cannot be negative" }
        if (request.incomeName.isNotBlank() || request.incomeAmountCents > 0L) {
            require(request.incomeName.isNotBlank()) { "Income name is required" }
            require(request.incomeAmountCents > 0L) { "Income amount must be positive" }
            DateParser.parseToIsoDate(request.nextPaydayDate).getOrThrow()
            require(request.isIncomeConfirmed) { "Income must be confirmed" }
            IncomeRecurrencePolicy.validateAnchors(
                request.frequency.ifBlank { IncomeFrequency.BIWEEKLY },
                request.paydayAnchorDayOne,
                request.paydayAnchorDayTwo
            )
        }
        request.bills.forEach { bill ->
            if (bill.name.isNotBlank() || bill.amountCents > 0L || bill.dueDate.isNotBlank()) {
                require(bill.name.isNotBlank()) { "Bill name is required" }
                require(bill.amountCents > 0L) { "Bill amount must be positive" }
                DateParser.parseToIsoDate(bill.dueDate).getOrThrow()
            }
        }
    }
}
