package com.example.data

object LabRepository {

  val equipmentCatalog = listOf(
    EquipmentItem(
      id = "dmm_01",
      name = "Digital Precision Multimeter",
      category = "Meters",
      domain = LabDomain.PHYSICS,
      description = "True RMS digital multimeter measuring DC/AC voltage, series current, and 4-wire resistance with audible continuity.",
      specSummary = "0.001V - 1000V | 0.1mA - 20A | ±0.05% Accuracy",
      level = 1,
      upgradeCost = 120,
      isUnlocked = true
    ),
    EquipmentItem(
      id = "dc_pwr_01",
      name = "Dual-Channel Regulated DC Power Supply",
      category = "Power Sources",
      domain = LabDomain.PHYSICS,
      description = "Linear variable power supply with constant voltage (CV) and constant current (CC) limit modes and ripple rejection.",
      specSummary = "0 - 30.0V DC | 0 - 5.0A | Current Limiting",
      level = 1,
      upgradeCost = 150,
      isUnlocked = true
    ),
    EquipmentItem(
      id = "osc_01",
      name = "Dual-Trace Digital Storage Oscilloscope",
      category = "Meters",
      domain = LabDomain.PHYSICS,
      description = "Real-time waveform analyzer for AC signals, phase shift measurement, and frequency domain peak detection.",
      specSummary = "100 MHz Bandwidth | 1 GSa/s Sample Rate",
      level = 1,
      upgradeCost = 250,
      isUnlocked = false,
      unlockCost = 200
    ),
    EquipmentItem(
      id = "pot_01",
      name = "10-Wire Precision Potentiometer",
      category = "Circuits",
      domain = LabDomain.PHYSICS,
      description = "Null-deflection potentiometer wire for accurate EMF comparison and internal resistance determination.",
      specSummary = "1000 cm Constantan Wire | Zero-draw measurement",
      level = 1,
      upgradeCost = 180,
      isUnlocked = true
    ),
    EquipmentItem(
      id = "burette_01",
      name = "50mL Class-A Glass Burette",
      category = "Volumetric",
      domain = LabDomain.CHEMISTRY,
      description = "PTFE straight stopcock burette for high-precision acid-base titrations and dropwise dispensing.",
      specSummary = "50.00 mL | Subdivisions 0.05 mL | Class A Borosilicate",
      level = 1,
      upgradeCost = 100,
      isUnlocked = true
    ),
    EquipmentItem(
      id = "ph_meter_01",
      name = "Microprocessor pH / Ion Meter",
      category = "Analytical",
      domain = LabDomain.CHEMISTRY,
      description = "Electrochemical glass electrode probe with automatic temperature compensation (ATC) and mV readout.",
      specSummary = "Range: -2.00 to 16.00 pH | ±0.01 pH Resolution",
      level = 1,
      upgradeCost = 160,
      isUnlocked = false,
      unlockCost = 150
    ),
    EquipmentItem(
      id = "galv_01",
      name = "Dead-Beat Moving Coil Galvanometer",
      category = "Meters",
      domain = LabDomain.PHYSICS,
      description = "High-sensitivity center-zero galvanometer with damping coil for detecting microcurrents in bridge circuits.",
      specSummary = "Sensitivity: 20 µA/div | Resistance: 50 Ω",
      level = 1,
      upgradeCost = 110,
      isUnlocked = true
    ),
    EquipmentItem(
      id = "cell_daniell_01",
      name = "Standard Daniell Galvanic Cell",
      category = "Electrochemistry",
      domain = LabDomain.CHEMISTRY,
      description = "Dual-compartment electrochemical cell with zinc/copper electrodes and porous ceramic salt bridge.",
      specSummary = "Standard EMF: 1.100 V at 298 K | Nernst Compliant",
      level = 1,
      upgradeCost = 140,
      isUnlocked = true
    ),
    EquipmentItem(
      id = "hoffman_01",
      name = "Hoffman Voltameter Electrolyzer",
      category = "Electrolysis",
      domain = LabDomain.CHEMISTRY,
      description = "Graduated three-tube electrolysis apparatus with platinum electrodes for water dissociation into H2 and O2.",
      specSummary = "Stoichiometric 2:1 Gas Collection | Pt Electrodes",
      level = 1,
      upgradeCost = 220,
      isUnlocked = false,
      unlockCost = 220
    ),
    EquipmentItem(
      id = "lens_conv_01",
      name = "Achromatic Doublet Convex Lens",
      category = "Optics",
      domain = LabDomain.PHYSICS,
      description = "Precision ground converging optical lens with anti-reflective coating for focal ray tracing.",
      specSummary = "Focal length f = +15.0 cm | Diameter: 50 mm",
      level = 1,
      upgradeCost = 90,
      isUnlocked = true
    ),
    EquipmentItem(
      id = "prism_01",
      name = "Equilateral Flint Glass Prism",
      category = "Optics",
      domain = LabDomain.PHYSICS,
      description = "Optical dispersion prism (refractive index n=1.66) for spectrum generation and minimum deviation measurement.",
      specSummary = "60° Apex Angle | High-Dispersion Heavy Flint",
      level = 1,
      upgradeCost = 130,
      isUnlocked = false,
      unlockCost = 180
    ),
    EquipmentItem(
      id = "rad_decay_01",
      name = "Geiger-Müller Radiation Counter",
      category = "Nuclear",
      domain = LabDomain.PHYSICS,
      description = "End-window Geiger tube with pulse integrator for nuclear decay counting and material shielding verification.",
      specSummary = "Dead time: 90 µs | Alpha, Beta, Gamma Detection",
      level = 1,
      upgradeCost = 280,
      isUnlocked = false,
      unlockCost = 250
    )
  )

  val experiments = listOf(
    ExperimentModel(
      id = "exp_ohm_law",
      title = "Verification of Ohm's Law & Resistor Color Code",
      domain = LabDomain.PHYSICS,
      topic = "Electricity & Circuits",
      summary = "Measure current as a function of applied potential difference to verify linear V = IR proportionality.",
      objective = "Determine resistance of an unknown resistor, verify 4-band color code, and plot the linear I-V characteristic graph.",
      theoryAndFormula = "Ohm's Law states: V = I · R. Power dissipated is P = I² · R. Color bands: 1st/2nd digit + multiplier + tolerance (Gold 5%).",
      observationNotes = "As voltage is stepped from 2V to 12V across a 100Ω resistor, current scales strictly linearly from 20mA to 120mA.",
      xpReward = 80,
      coinsReward = 100
    ),
    ExperimentModel(
      id = "exp_potentiometer",
      title = "Potentiometer: Internal Resistance of a Cell",
      domain = LabDomain.PHYSICS,
      topic = "Circuits & Electromagnetism",
      summary = "Find the balance length with open circuit (l1) and with shunt resistance R (l2) using a null-deflection galvanometer.",
      objective = "Measure EMF E and compute cell internal resistance r = R · ((l1 / l2) - 1).",
      theoryAndFormula = "At null deflection: E = k·l1, Terminal V = k·l2. Thus r = R · ((l1 - l2) / l2).",
      observationNotes = "Balancing at 750cm without shunt, and 450cm with 10Ω shunt gives internal resistance r = 6.67 Ω.",
      xpReward = 90,
      coinsReward = 110
    ),
    ExperimentModel(
      id = "exp_ac_transformer",
      title = "AC Transformer Turns Ratio & Mutual Induction",
      domain = LabDomain.PHYSICS,
      topic = "Electromagnetism & AC",
      summary = "Analyze magnetic flux linkage between primary and secondary coils in step-up and step-down configurations.",
      objective = "Confirm turns ratio equation: Vs / Vp = Ns / Np = Ip / Is and measure power transfer efficiency.",
      theoryAndFormula = "Faraday's Law of Induction: EMF e = -N · (dΦ/dt). For ideal transformer, P_in = P_out.",
      observationNotes = "Primary 220V with Np=1000 turns and Ns=100 turns yields secondary voltage of 22.0V AC.",
      xpReward = 85,
      coinsReward = 105
    ),
    ExperimentModel(
      id = "exp_optics_lens",
      title = "Optics: Convex Lens Formula & Magnification",
      domain = LabDomain.PHYSICS,
      topic = "Ray Optics",
      summary = "Trace real and virtual images produced by an optical convex lens at various object distances u.",
      objective = "Verify Gaussian lens equation: 1/f = 1/v - 1/u and compute linear magnification m = -v/u.",
      theoryAndFormula = "Thin Lens Equation: 1/v - 1/u = 1/f. When u = -2f, image distance v = +2f and magnification m = -1 (real, inverted).",
      observationNotes = "For focal length f = +15 cm and object distance u = -30 cm, sharp image forms at v = +30 cm.",
      xpReward = 75,
      coinsReward = 95
    ),
    ExperimentModel(
      id = "exp_prism_dispersion",
      title = "Prism Dispersion & Minimum Deviation Angle",
      domain = LabDomain.PHYSICS,
      topic = "Optics & Waves",
      summary = "Pass incident light beam through triangular prism to measure refractive index n using Cauchy's dispersion.",
      objective = "Determine angle of minimum deviation D_m and calculate n = sin((A + D_m)/2) / sin(A/2).",
      theoryAndFormula = "Refractive index n varies inversely with wavelength λ (Cauchy relation), causing violet light (shorter λ) to bend more than red light.",
      observationNotes = "Equilateral prism (A=60°) exhibits minimum deviation D_m = 38.5° yielding refractive index n = 1.52.",
      xpReward = 90,
      coinsReward = 120
    ),
    ExperimentModel(
      id = "exp_pendulum_mechanics",
      title = "Simple Harmonic Motion: Pendulum & Gravity g",
      domain = LabDomain.PHYSICS,
      topic = "Mechanics & Oscillations",
      summary = "Investigate the oscillation period T of a simple pendulum across different celestial gravity environments.",
      objective = "Verify T = 2π√(L/g) and determine gravitational acceleration g from T² vs L graph.",
      theoryAndFormula = "Restoring torque τ = -m·g·L·sinθ ≈ -m·g·L·θ for small angles. Angular frequency ω = √(g/L).",
      observationNotes = "On Earth (g=9.8 m/s²), a 1.0m pendulum has period T = 2.006s. On Moon (g=1.62 m/s²), period extends to 4.94s.",
      xpReward = 70,
      coinsReward = 85
    ),
    ExperimentModel(
      id = "exp_photoelectric",
      title = "Photoelectric Effect & Einstein's Quantum Formula",
      domain = LabDomain.PHYSICS,
      topic = "Modern & Nuclear Physics",
      summary = "Irradiate target metal cathode with photons of varying frequency to measure stopping potential Vs.",
      objective = "Verify maximum kinetic energy K_max = h·ν - Φ = e·Vs and calculate Planck's constant h.",
      theoryAndFormula = "Threshold frequency ν0 = Φ/h. Light below ν0 ejects zero electrons regardless of beam intensity.",
      observationNotes = "For Sodium (Φ=2.3 eV), green light (ν=5.5×10¹⁴ Hz) yields K_max = 0.00 eV; UV light (ν=8.0×10¹⁴ Hz) produces 1.01 eV electrons.",
      xpReward = 100,
      coinsReward = 130
    ),
    ExperimentModel(
      id = "exp_titration_hcl_naoh",
      title = "Acid-Base Titration: 0.1M HCl vs 0.1M NaOH",
      domain = LabDomain.CHEMISTRY,
      topic = "Volumetric Analysis",
      summary = "Titrate strong acid (HCl) with strong base (NaOH) using phenolphthalein indicator to trace pH S-curve.",
      objective = "Find exact equivalence point volume, observe abrupt pH inflection from 3 to 11, and calculate molarity.",
      theoryAndFormula = "Neutralization: H⁺(aq) + OH⁻(aq) → H2O(l). Equivalence when n(acid) = n(base): M1·V1 = M2·V2.",
      observationNotes = "At 25.0 mL NaOH added, solution instantly shifts from transparent to pale permanent pink (pH 8.2).",
      xpReward = 85,
      coinsReward = 110
    ),
    ExperimentModel(
      id = "exp_daniell_cell",
      title = "Daniell Galvanic Cell & Nernst Equation",
      domain = LabDomain.CHEMISTRY,
      topic = "Electrochemistry",
      summary = "Construct electrochemical cell: Zn(s) | Zn²⁺(aq) || Cu²⁺(aq) | Cu(s) and measure cell potential E_cell.",
      objective = "Calculate standard cell EMF E° = E°(cathode) - E°(anode) = 0.34V - (-0.76V) = 1.10V and observe Nernst shifts.",
      theoryAndFormula = "Nernst Equation at 298K: E_cell = E° - (0.0592 / n) · log([Zn²⁺] / [Cu²⁺]).",
      observationNotes = "Equal 1.0M concentrations yield 1.10V. Diluting [Zn²⁺] to 0.01M increases potential to 1.16V.",
      xpReward = 95,
      coinsReward = 120
    ),
    ExperimentModel(
      id = "exp_water_electrolysis",
      title = "Electrolysis of Water: Faraday's Laws & Gas Volumes",
      domain = LabDomain.CHEMISTRY,
      topic = "Electrolysis",
      summary = "Decompose acidified water into Hydrogen and Oxygen gases via external DC electric current.",
      objective = "Confirm stoichiometric 2:1 volume ratio of H2 (cathode) to O2 (anode) and verify m = Z·I·t.",
      theoryAndFormula = "Cathode (-): 2H⁺ + 2e⁻ → H2(g). Anode (+): 2H2O → O2(g) + 4H⁺ + 4e⁻. Total: 2H2O → 2H2 + O2.",
      observationNotes = "Bubbles form twice as rapidly at the negative electrode, collecting 20.0 mL H2 and 10.0 mL O2.",
      xpReward = 90,
      coinsReward = 115
    )
  )

  val challenges = listOf(
    ChallengeModel(
      id = "ch_ohm_target",
      title = "Ohm's Law Circuit Tuning",
      domain = LabDomain.PHYSICS,
      description = "Tune the DC voltage and circuit rheostat to produce an exact current of 50.0 mA (±1 mA) across the load.",
      targetMetric = "Current = 50.0 mA",
      xpReward = 100,
      coinsReward = 120
    ),
    ChallengeModel(
      id = "ch_titration_neutral",
      title = "Precision Titration Neutralization",
      domain = LabDomain.CHEMISTRY,
      description = "Add titrant dropwise until the solution reaches exact neutral equivalence at pH 7.00 ± 0.15.",
      targetMetric = "pH = 7.00 ± 0.15",
      xpReward = 120,
      coinsReward = 150
    ),
    ChallengeModel(
      id = "ch_ethanol_synth",
      title = "Synthesize Pure Ethanol",
      domain = LabDomain.ORGANIC,
      description = "In the Molecule Maker, construct Ethanol (C2H5OH) with satisfied valencies for all 9 atoms.",
      targetMetric = "Valid Ethanol molecule built",
      xpReward = 110,
      coinsReward = 130
    ),
    ChallengeModel(
      id = "ch_benzene_ring",
      title = "Aromatic Benzene Ring Construction",
      domain = LabDomain.ORGANIC,
      description = "Build a 6-carbon hexagonal ring with alternating single and double bonds and complete hydrogen valency.",
      targetMetric = "IUPAC = Benzene",
      xpReward = 140,
      coinsReward = 180
    ),
    ChallengeModel(
      id = "ch_lens_focus",
      title = "Magnification Equilibrium",
      domain = LabDomain.PHYSICS,
      description = "Position the object at 2f distance on the optical bench to form an inverted image with magnification m = -1.0.",
      targetMetric = "Magnification = -1.0",
      xpReward = 90,
      coinsReward = 110
    ),
    ChallengeModel(
      id = "ch_daniell_nernst",
      title = "Boost Daniell Cell to 1.15V",
      domain = LabDomain.CHEMISTRY,
      description = "Adjust the concentration of Zn²⁺ and Cu²⁺ ions in the galvanic cell to achieve an EMF ≥ 1.15 V.",
      targetMetric = "E_cell ≥ 1.15 V",
      xpReward = 130,
      coinsReward = 160
    )
  )

  val achievements = listOf(
    AchievementModel(
      id = "ach_first_spark",
      title = "First Spark",
      description = "Complete your first circuit experiment with current flowing safely.",
      iconName = "bolt",
      isUnlocked = true,
      currentProgress = 1,
      maxProgress = 1,
      coinReward = 50
    ),
    AchievementModel(
      id = "ach_titration_pro",
      title = "Acid Neutralizer",
      description = "Reach the equivalence point in an acid-base titration experiment.",
      iconName = "science",
      isUnlocked = false,
      currentProgress = 0,
      maxProgress = 1,
      coinReward = 100
    ),
    AchievementModel(
      id = "ach_molecule_architect",
      title = "Molecular Architect",
      description = "Construct an organic molecule with zero valency violations.",
      iconName = "hub",
      isUnlocked = false,
      currentProgress = 0,
      maxProgress = 1,
      coinReward = 120
    ),
    AchievementModel(
      id = "ach_quantum_leap",
      title = "Quantum Pioneer",
      description = "Eject photoelectrons by exceeding the metal cathode work function.",
      iconName = "flare",
      isUnlocked = false,
      currentProgress = 0,
      maxProgress = 1,
      coinReward = 150
    ),
    AchievementModel(
      id = "ach_daniell_power",
      title = "Electrochemical Battery",
      description = "Operate the Daniell galvanic cell and observe positive EMF.",
      iconName = "battery_charging_full",
      isUnlocked = false,
      currentProgress = 0,
      maxProgress = 1,
      coinReward = 100
    ),
    AchievementModel(
      id = "ach_master_synthesizer",
      title = "Master of Synthesis",
      description = "Simulate 3 different organic chemical reactions.",
      iconName = "auto_fix_high",
      isUnlocked = false,
      currentProgress = 0,
      maxProgress = 3,
      coinReward = 200
    )
  )

  val moleculePresets = listOf(
    MoleculePreset(
      name = "Methane",
      iupac = "Methane",
      formula = "CH₄",
      molecularWeight = 16.04,
      compoundClass = "Alkane",
      description = "Simplest hydrocarbon with tetrahedral sp3 geometry, primary component of natural gas.",
      atoms = listOf(
        AtomNode(1, "C", 200f, 200f),
        AtomNode(2, "H", 200f, 130f),
        AtomNode(3, "H", 200f, 270f),
        AtomNode(4, "H", 130f, 200f),
        AtomNode(5, "H", 270f, 200f)
      ),
      bonds = listOf(
        BondEdge(1, 2, 1),
        BondEdge(1, 3, 1),
        BondEdge(1, 4, 1),
        BondEdge(1, 5, 1)
      )
    ),
    MoleculePreset(
      name = "Ethanol",
      iupac = "Ethanol",
      formula = "C₂H₅OH",
      molecularWeight = 46.07,
      compoundClass = "Alcohol",
      description = "Primary alcohol with hydroxy group; versatile solvent and biofuel.",
      atoms = listOf(
        AtomNode(1, "C", 140f, 200f),
        AtomNode(2, "C", 220f, 200f),
        AtomNode(3, "O", 300f, 200f),
        AtomNode(4, "H", 360f, 200f),
        AtomNode(5, "H", 140f, 140f),
        AtomNode(6, "H", 140f, 260f),
        AtomNode(7, "H", 80f, 200f),
        AtomNode(8, "H", 220f, 140f),
        AtomNode(9, "H", 220f, 260f)
      ),
      bonds = listOf(
        BondEdge(1, 2, 1),
        BondEdge(2, 3, 1),
        BondEdge(3, 4, 1),
        BondEdge(1, 5, 1),
        BondEdge(1, 6, 1),
        BondEdge(1, 7, 1),
        BondEdge(2, 8, 1),
        BondEdge(2, 9, 1)
      )
    ),
    MoleculePreset(
      name = "Acetic Acid",
      iupac = "Ethanoic Acid",
      formula = "CH₃COOH",
      molecularWeight = 60.05,
      compoundClass = "Carboxylic Acid",
      description = "Key component of vinegar, featuring carbonyl (C=O) and hydroxyl (-OH) attached to alpha carbon.",
      atoms = listOf(
        AtomNode(1, "C", 140f, 200f),
        AtomNode(2, "C", 220f, 200f),
        AtomNode(3, "O", 220f, 130f),
        AtomNode(4, "O", 290f, 200f),
        AtomNode(5, "H", 350f, 200f),
        AtomNode(6, "H", 80f, 200f),
        AtomNode(7, "H", 140f, 140f),
        AtomNode(8, "H", 140f, 260f)
      ),
      bonds = listOf(
        BondEdge(1, 2, 1),
        BondEdge(2, 3, 2),
        BondEdge(2, 4, 1),
        BondEdge(4, 5, 1),
        BondEdge(1, 6, 1),
        BondEdge(1, 7, 1),
        BondEdge(1, 8, 1)
      )
    ),
    MoleculePreset(
      name = "Acetone",
      iupac = "Propan-2-one",
      formula = "C₃H₆O",
      molecularWeight = 58.08,
      compoundClass = "Ketone",
      description = "Simplest aliphatic ketone with central carbonyl carbon bonded to two methyl groups.",
      atoms = listOf(
        AtomNode(1, "C", 130f, 220f),
        AtomNode(2, "C", 200f, 220f),
        AtomNode(3, "C", 270f, 220f),
        AtomNode(4, "O", 200f, 150f),
        AtomNode(5, "H", 80f, 220f),
        AtomNode(6, "H", 130f, 160f),
        AtomNode(7, "H", 130f, 280f),
        AtomNode(8, "H", 320f, 220f),
        AtomNode(9, "H", 270f, 160f),
        AtomNode(10, "H", 270f, 280f)
      ),
      bonds = listOf(
        BondEdge(1, 2, 1),
        BondEdge(2, 3, 1),
        BondEdge(2, 4, 2),
        BondEdge(1, 5, 1),
        BondEdge(1, 6, 1),
        BondEdge(1, 7, 1),
        BondEdge(3, 8, 1),
        BondEdge(3, 9, 1),
        BondEdge(3, 10, 1)
      )
    ),
    MoleculePreset(
      name = "Ethene",
      iupac = "Ethene",
      formula = "C₂H₄",
      molecularWeight = 28.05,
      compoundClass = "Alkene",
      description = "Simplest alkene containing planar sp2-hybridized carbons with one sigma and one pi bond.",
      atoms = listOf(
        AtomNode(1, "C", 160f, 200f),
        AtomNode(2, "C", 240f, 200f),
        AtomNode(3, "H", 110f, 150f),
        AtomNode(4, "H", 110f, 250f),
        AtomNode(5, "H", 290f, 150f),
        AtomNode(6, "H", 290f, 250f)
      ),
      bonds = listOf(
        BondEdge(1, 2, 2),
        BondEdge(1, 3, 1),
        BondEdge(1, 4, 1),
        BondEdge(2, 5, 1),
        BondEdge(2, 6, 1)
      )
    ),
    MoleculePreset(
      name = "Ethyne",
      iupac = "Ethyne (Acetylene)",
      formula = "C₂H₂",
      molecularWeight = 26.04,
      compoundClass = "Alkyne",
      description = "Linear hydrocarbon featuring a carbon-carbon triple bond (one sigma, two pi bonds).",
      atoms = listOf(
        AtomNode(1, "C", 160f, 200f),
        AtomNode(2, "C", 240f, 200f),
        AtomNode(3, "H", 100f, 200f),
        AtomNode(4, "H", 300f, 200f)
      ),
      bonds = listOf(
        BondEdge(1, 2, 3),
        BondEdge(1, 3, 1),
        BondEdge(2, 4, 1)
      )
    ),
    MoleculePreset(
      name = "Benzene",
      iupac = "Benzene",
      formula = "C₆H₆",
      molecularWeight = 78.11,
      compoundClass = "Aromatic",
      description = "Planar aromatic ring with delocalized pi electron cloud adhering to Hückel's 4n+2 rule.",
      atoms = listOf(
        AtomNode(1, "C", 200f, 130f),
        AtomNode(2, "C", 260f, 165f),
        AtomNode(3, "C", 260f, 235f),
        AtomNode(4, "C", 200f, 270f),
        AtomNode(5, "C", 140f, 235f),
        AtomNode(6, "C", 140f, 165f),
        AtomNode(7, "H", 200f, 80f),
        AtomNode(8, "H", 310f, 145f),
        AtomNode(9, "H", 310f, 255f),
        AtomNode(10, "H", 200f, 320f),
        AtomNode(11, "H", 90f, 255f),
        AtomNode(12, "H", 90f, 145f)
      ),
      bonds = listOf(
        BondEdge(1, 2, 2),
        BondEdge(2, 3, 1),
        BondEdge(3, 4, 2),
        BondEdge(4, 5, 1),
        BondEdge(5, 6, 2),
        BondEdge(6, 1, 1),
        BondEdge(1, 7, 1),
        BondEdge(2, 8, 1),
        BondEdge(3, 9, 1),
        BondEdge(4, 10, 1),
        BondEdge(5, 11, 1),
        BondEdge(6, 12, 1)
      )
    )
  )

  val organicReactions = listOf(
    OrganicReaction(
      id = "rx_oxidation_alcohol",
      title = "Oxidation of Ethanol to Ethanoic Acid",
      reactantIupac = "Ethanol",
      reactantFormula = "C₂H₅OH",
      reagent = "Alkaline KMnO₄ or Acidified K₂Cr₂O₇",
      condition = "Reflux under heat (Δ)",
      productIupac = "Ethanoic Acid",
      productFormula = "CH₃COOH",
      equation = "CH₃CH₂OH + 2[O] ➔ CH₃COOH + H₂O",
      mechanismNotes = "Primary alcohol oxidizes first to Ethanal (aldehyde) intermediate, then rapidly oxidizes to Carboxylic Acid."
    ),
    OrganicReaction(
      id = "rx_dehydration_ethene",
      title = "Dehydration of Ethanol to Ethene",
      reactantIupac = "Ethanol",
      reactantFormula = "C₂H₅OH",
      reagent = "Concentrated H₂SO₄",
      condition = "Heated to 443 K (170°C)",
      productIupac = "Ethene",
      productFormula = "C₂H₄",
      equation = "CH₃CH₂OH ➔ CH₂=CH₂ + H₂O",
      mechanismNotes = "Acid-catalyzed E1 elimination: protonation of -OH to form oxonium ion, loss of water to carbocation, loss of proton to form alkene."
    ),
    OrganicReaction(
      id = "rx_esterification",
      title = "Fischer Esterification (Ester Synthesis)",
      reactantIupac = "Ethanoic Acid + Ethanol",
      reactantFormula = "CH₃COOH + C₂H₅OH",
      reagent = "Concentrated H₂SO₄ catalyst",
      condition = "Warm water bath (60°C)",
      productIupac = "Ethyl Ethanoate (Ester)",
      productFormula = "CH₃COOCH₂CH₃",
      equation = "CH₃COOH + C₂H₅OH ⇌ CH₃COOCH₂CH₃ + H₂O",
      mechanismNotes = "Nucleophilic acyl substitution: carbonyl oxygen protonation, nucleophilic attack by alcohol, tetrahedral intermediate dehydration."
    ),
    OrganicReaction(
      id = "rx_hydrogenation_ethene",
      title = "Catalytic Hydrogenation (Alkene to Alkane)",
      reactantIupac = "Ethene",
      reactantFormula = "C₂H₄",
      reagent = "H₂ gas / Nickel or Platinum catalyst",
      condition = "200°C (Sabatier–Senderens reaction)",
      productIupac = "Ethane",
      productFormula = "C₂H₆",
      equation = "CH₂=CH₂ + H₂ ➔ CH₃-CH₃",
      mechanismNotes = "Syn-addition of H-H bond across carbon-carbon pi bond coordinated on the metal catalyst surface."
    ),
    OrganicReaction(
      id = "rx_halogenation_benzene",
      title = "Electrophilic Bromination of Benzene",
      reactantIupac = "Benzene",
      reactantFormula = "C₆H₆",
      reagent = "Br₂ / FeBr₃ Lewis acid catalyst",
      condition = "Room temperature, dark",
      productIupac = "Bromobenzene",
      productFormula = "C₆H₅Br",
      equation = "C₆H₆ + Br₂ ➔ C₆H₅Br + HBr",
      mechanismNotes = "Electrophilic aromatic substitution (EAS): FeBr3 polarizes Br-Br generating bromonium ion, forming arenium ion (sigma complex), followed by proton loss to restore aromaticity."
    )
  )
}
