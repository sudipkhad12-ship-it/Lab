package com.example.data

enum class LabDomain {
  PHYSICS,
  CHEMISTRY,
  ORGANIC
}

enum class PhysicsSubdomain {
  CIRCUITS,
  MECHANICS,
  OPTICS,
  MODERN_PHYSICS
}

enum class ChemistrySubdomain {
  TITRATION,
  ELECTROCHEMISTRY,
  REACTIONS,
  THERMAL
}

data class EquipmentItem(
  val id: String,
  val name: String,
  val category: String,
  val domain: LabDomain,
  val description: String,
  val specSummary: String,
  val level: Int = 1,
  val maxLevel: Int = 3,
  val upgradeCost: Int = 150,
  val isUnlocked: Boolean = true,
  val unlockCost: Int = 0
)

data class ExperimentModel(
  val id: String,
  val title: String,
  val domain: LabDomain,
  val topic: String,
  val summary: String,
  val objective: String,
  val theoryAndFormula: String,
  val observationNotes: String,
  val xpReward: Int,
  val coinsReward: Int,
  val isCompleted: Boolean = false
)

data class ChallengeModel(
  val id: String,
  val title: String,
  val domain: LabDomain,
  val description: String,
  val targetMetric: String,
  val xpReward: Int,
  val coinsReward: Int,
  val isCompleted: Boolean = false
)

data class AchievementModel(
  val id: String,
  val title: String,
  val description: String,
  val iconName: String,
  val isUnlocked: Boolean = false,
  val currentProgress: Int = 0,
  val maxProgress: Int = 1,
  val coinReward: Int = 100
)

data class AtomNode(
  val id: Int,
  val element: String, // C, H, O, N, S, F, Cl, Br, I
  val x: Float,
  val y: Float
)

data class BondEdge(
  val atom1Id: Int,
  val atom2Id: Int,
  val order: Int = 1 // 1 = single, 2 = double, 3 = triple
)

data class OrganicReaction(
  val id: String,
  val title: String,
  val reactantIupac: String,
  val reactantFormula: String,
  val reagent: String,
  val condition: String,
  val productIupac: String,
  val productFormula: String,
  val equation: String,
  val mechanismNotes: String
)

data class MoleculePreset(
  val name: String,
  val iupac: String,
  val formula: String,
  val molecularWeight: Double,
  val compoundClass: String,
  val description: String,
  val atoms: List<AtomNode>,
  val bonds: List<BondEdge>
)
