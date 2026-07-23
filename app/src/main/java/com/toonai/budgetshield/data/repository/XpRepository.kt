package com.toonai.budgetshield.data.repository

import com.toonai.budgetshield.data.database.AchievementDao
import com.toonai.budgetshield.data.database.XpEntryDao
import com.toonai.budgetshield.data.model.*
import com.toonai.budgetshield.util.DateParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

/**
 * Repository for XP and progression operations.
 * Manages XP entries, levels, and achievements.
 */
class XpRepository(
    private val xpEntryDao: XpEntryDao,
    private val achievementDao: AchievementDao
) {

    /** All XP entries as a reactive stream */
    val allXpEntries: Flow<List<XpEntry>> = xpEntryDao.getAllXpEntries()

    /** Total XP as a reactive stream */
    val totalXp: Flow<Int> = xpEntryDao.getTotalXp().map { it ?: 0 }

    /** Current shield level based on total XP */
    val currentLevel: Flow<ShieldLevels.Level> = totalXp.map { xp ->
        ShieldLevels.getLevelForXp(xp)
    }

    /** XP to next level */
    val xpToNextLevel: Flow<Int> = totalXp.map { xp ->
        ShieldLevels.xpToNextLevel(xp)
    }

    /** Progress percentage to next level (0-100) */
    val levelProgressPercent: Flow<Int> = combine(totalXp, currentLevel) { xp, level ->
        val nextLevel = ShieldLevels.getNextLevel(xp)
        if (nextLevel != null) {
            val xpInLevel = xp - level.xpRequired
            val xpNeededForLevel = nextLevel.xpRequired - level.xpRequired
            ((xpInLevel * 100) / xpNeededForLevel).coerceIn(0, 100)
        } else 100
    }

    /** All achievements as a reactive stream */
    val allAchievements: Flow<List<Achievement>> = achievementDao.getAllAchievements()

    /** Unlocked achievements */
    val unlockedAchievements: Flow<List<Achievement>> = achievementDao.getUnlockedAchievements()

    /** Number of unlocked achievements */
    val unlockedCount: Flow<Int> = achievementDao.getUnlockedCount().map { it ?: 0 }

    /** XP history - recent entries in reverse chronological order */
    val xpHistory: Flow<List<XpEntry>> = xpEntryDao.getAllXpEntries().map { entries ->
        entries.sortedByDescending { it.createdAt }.take(20)
    }

    /** Recent XP entries (last N) */
    suspend fun getRecentXpEntries(limit: Int = 5): List<XpEntry> {
        return xpEntryDao.getRecentXpEntries(limit)
    }

    /** Get XP for today */
    suspend fun getXpForToday(): Int {
        return xpEntryDao.getXpForDate(DateParser.today()) ?: 0
    }

    /** Get XP for current month */
    fun getXpForMonth(monthKey: String = DateParser.currentMonthKey()): Flow<Int> {
        return xpEntryDao.getXpForMonth(monthKey).map { it ?: 0 }
    }

    /**
     * Award XP for an activity.
     * Applies level boost if applicable.
     */
    suspend fun awardXp(
        activityType: String,
        description: String,
        relatedId: Long? = null
    ): XpEntry {
        val baseAmount = XpActivityTypes.baseXp(activityType)
        val currentLevelValue = currentLevel.first()
        val boostedAmount = baseAmount + (baseAmount * currentLevelValue.xpBoostPercent / 100)

        val entry = XpEntry(
            amount = boostedAmount,
            activityType = activityType,
            description = description,
            relatedId = relatedId,
            entryDate = DateParser.today()
        )
        xpEntryDao.insertXpEntry(entry)
        return entry
    }

    /**
     * Award XP for protecting a bill.
     */
    suspend fun awardBillProtectionXp(billId: Long, billName: String): XpEntry {
        return awardXp(XpActivityTypes.PROTECT_BILL, "Protected $billName", billId)
    }

    /**
     * Award XP for paying a bill.
     */
    suspend fun awardBillPaymentXp(billId: Long, billName: String): XpEntry {
        return awardXp(XpActivityTypes.PAY_BILL, "Paid $billName", billId)
    }

    /**
     * Award XP for adding income.
     */
    suspend fun awardIncomeXp(incomeId: Long, incomeName: String): XpEntry {
        return awardXp(XpActivityTypes.ADD_INCOME, "Added income: $incomeName", incomeId)
    }

    /**
     * Award XP for adding savings.
     */
    suspend fun awardSavingsXp(amountCents: Long): XpEntry {
        val formatted = "${amountCents / 100}.${amountCents % 100}"
        return awardXp(XpActivityTypes.ADD_SAVINGS, "Saved $$formatted")
    }

    /**
     * Award daily streak XP.
     */
    suspend fun awardStreakXp(streakDays: Int): XpEntry {
        return awardXp(XpActivityTypes.DAILY_STREAK, "$streakDays day streak")
    }

    /**
     * Initialize default achievements if none exist.
     */
    suspend fun initializeAchievementsIfNeeded() {
        val count = achievementDao.getTotalAchievementCount()
        if (count == 0) {
            achievementDao.insertAll(AchievementsList.ALL)
        }
    }

    /**
     * Update achievement progress.
     */
    suspend fun updateAchievementProgress(achievementId: String, progress: Int) {
        achievementDao.updateProgress(achievementId, progress.coerceIn(0, 100))
    }

    /**
     * Unlock an achievement.
     * Returns the XP reward if newly unlocked.
     */
    suspend fun unlockAchievement(achievementId: String): Int {
        val achievement = achievementDao.getAchievementById(achievementId)
        if (achievement != null && !achievement.isUnlocked) {
            achievementDao.unlockAchievement(achievementId)
            return achievement.xpReward
        }
        return 0
    }

    /**
     * Check and update achievement progress based on stats.
     */
    suspend fun checkAchievements(
        protectedBillCount: Int,
        paidBillCount: Int,
        streakDays: Int,
        totalSavingsCents: Long,
        totalXp: Int
    ): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()

        // First bill protected
        if (protectedBillCount >= 1) {
            val xp = unlockAchievement("first_bill")
            if (xp > 0) {
                awardXp(XpActivityTypes.COMPLETE_SETUP, "Achievement: Bill Protector")
            }
        }

        // 5 bills protected
        if (protectedBillCount >= 5) {
            updateAchievementProgress("protect_5", (protectedBillCount * 100) / 5)
            if (protectedBillCount >= 5) {
                unlockAchievement("protect_5")
            }
        }

        // 10 bills protected
        if (protectedBillCount >= 10) {
            updateAchievementProgress("protect_10", (protectedBillCount * 100) / 10)
            if (protectedBillCount >= 10) {
                unlockAchievement("protect_10")
            }
        }

        // Streak achievements
        when {
            streakDays >= 30 -> unlockAchievement("streak_30")
            streakDays >= 7 -> unlockAchievement("streak_7")
            streakDays >= 3 -> unlockAchievement("streak_3")
        }
        updateAchievementProgress("streak_3", (streakDays * 100) / 3)
        updateAchievementProgress("streak_7", (streakDays * 100) / 7)
        updateAchievementProgress("streak_30", (streakDays * 100) / 30)

        // Savings achievements
        val savingsDollars = totalSavingsCents / 100
        when {
            savingsDollars >= 1000 -> unlockAchievement("save_1000")
            savingsDollars >= 100 -> unlockAchievement("save_100")
        }
        updateAchievementProgress("save_100", (savingsDollars.toInt() * 100) / 100)
        updateAchievementProgress("save_1000", (savingsDollars.toInt() * 100) / 1000)

        // Level achievements
        val level = ShieldLevels.getLevelForXp(totalXp)
        if (level.level >= 2) unlockAchievement("shield_level_2")
        if (level.level >= 3) unlockAchievement("shield_level_3")

        return newlyUnlocked
    }
}
