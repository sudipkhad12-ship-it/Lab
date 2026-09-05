package com.example.data

import android.content.Context
import android.content.SharedPreferences

class LabPreferences(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("power_lab_state", Context.MODE_PRIVATE)

  var hasCompletedOnboarding: Boolean
    get() = prefs.getBoolean("onboarding_completed", false)
    set(value) = prefs.edit().putBoolean("onboarding_completed", value).apply()

  var hasAcceptedPrivacy: Boolean
    get() = prefs.getBoolean("privacy_accepted", false)
    set(value) = prefs.edit().putBoolean("privacy_accepted", value).apply()

  var themeMode: String // "SYSTEM", "DARK", "LIGHT"
    get() = prefs.getString("theme_mode", "DARK") ?: "DARK"
    set(value) = prefs.edit().putString("theme_mode", value).apply()

  var hapticsEnabled: Boolean
    get() = prefs.getBoolean("haptics_enabled", true)
    set(value) = prefs.edit().putBoolean("haptics_enabled", value).apply()

  var xp: Int
    get() = prefs.getInt("user_xp", 120)
    set(value) = prefs.edit().putInt("user_xp", value).apply()

  var coins: Int
    get() = prefs.getInt("user_coins", 250)
    set(value) = prefs.edit().putInt("user_coins", value).apply()

  val level: Int
    get() = 1 + (xp / 250)

  val rankTitle: String
    get() = when (level) {
      1 -> "Laboratory Trainee"
      2 -> "Junior Physicist"
      3 -> "Assistant Chemist"
      4 -> "Research Specialist"
      5 -> "Senior Scientist"
      6 -> "Principal Investigator"
      7 -> "Department Head"
      else -> "Chief Lab Director"
    }

  fun getCompletedExperiments(): Set<String> {
    return prefs.getStringSet("completed_experiments", emptySet()) ?: emptySet()
  }

  fun markExperimentCompleted(id: String, xpGain: Int, coinGain: Int) {
    val current = getCompletedExperiments().toMutableSet()
    if (!current.contains(id)) {
      current.add(id)
      prefs.edit().putStringSet("completed_experiments", current).apply()
      xp += xpGain
      coins += coinGain
    }
  }

  fun getCompletedChallenges(): Set<String> {
    return prefs.getStringSet("completed_challenges", emptySet()) ?: emptySet()
  }

  fun markChallengeCompleted(id: String, xpGain: Int, coinGain: Int) {
    val current = getCompletedChallenges().toMutableSet()
    if (!current.contains(id)) {
      current.add(id)
      prefs.edit().putStringSet("completed_challenges", current).apply()
      xp += xpGain
      coins += coinGain
    }
  }

  fun getUnlockedEquipment(): Set<String> {
    return prefs.getStringSet("unlocked_equipment", setOf("dmm_01", "dc_pwr_01", "burette_01", "galv_01", "lens_conv_01")) ?: emptySet()
  }

  fun unlockEquipment(id: String, cost: Int): Boolean {
    if (coins >= cost) {
      val current = getUnlockedEquipment().toMutableSet()
      current.add(id)
      prefs.edit().putStringSet("unlocked_equipment", current).apply()
      coins -= cost
      return true
    }
    return false
  }

  fun getEquipmentLevel(id: String): Int {
    return prefs.getInt("equip_level_$id", 1)
  }

  fun upgradeEquipment(id: String, cost: Int): Boolean {
    if (coins >= cost) {
      val currentLvl = getEquipmentLevel(id)
      prefs.edit().putInt("equip_level_$id", currentLvl + 1).apply()
      coins -= cost
      return true
    }
    return false
  }

  fun getUnlockedAchievements(): Set<String> {
    return prefs.getStringSet("unlocked_achievements", setOf("ach_first_spark")) ?: emptySet()
  }

  fun unlockAchievement(id: String, reward: Int) {
    val current = getUnlockedAchievements().toMutableSet()
    if (!current.contains(id)) {
      current.add(id)
      prefs.edit().putStringSet("unlocked_achievements", current).apply()
      coins += reward
    }
  }

  fun resetAllProgress() {
    prefs.edit().clear().apply()
  }
}
