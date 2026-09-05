package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AtomNode
import com.example.data.BondEdge
import com.example.data.LabRepository
import com.example.data.OrganicReaction
import com.example.ui.theme.AtomCarbon
import com.example.ui.theme.AtomHalogen
import com.example.ui.theme.AtomHydrogen
import com.example.ui.theme.AtomNitrogen
import com.example.ui.theme.AtomOxygen
import com.example.ui.theme.AtomSulfur
import com.example.ui.theme.AtomicAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LaserCrimson
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PlasmaViolet
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun MoleculeCanvasView(
  onMoleculeBuilt: (String, Boolean) -> Unit = { _, _ -> }
) {
  // Molecule Canvas State
  val atoms = remember { mutableStateListOf<AtomNode>() }
  val bonds = remember { mutableStateListOf<BondEdge>() }

  var nextAtomId by remember { mutableIntStateOf(1) }
  var selectedElement by remember { mutableStateOf("C") }
  var selectedAtomId by remember { mutableStateOf<Int?>(null) }
  var bondOrderTool by remember { mutableIntStateOf(1) } // 1, 2, 3
  var selectedReaction by remember { mutableStateOf<OrganicReaction?>(null) }
  var showReactionPanel by remember { mutableStateOf(false) }

  // Initialize with Ethanol preset if empty
  if (atoms.isEmpty()) {
    val ethanol = LabRepository.moleculePresets[1]
    atoms.addAll(ethanol.atoms)
    bonds.addAll(ethanol.bonds)
    nextAtomId = (ethanol.atoms.maxOfOrNull { it.id } ?: 0) + 1
  }

  // Real-time Valency Verification & Formula Evaluation
  val atomValencies = remember(atoms.toList(), bonds.toList()) {
    computeAtomValencies(atoms, bonds)
  }

  val hasAnyValencyError = atomValencies.values.any { it.second > it.first } // current > max
  val isAllSatisfied = atoms.isNotEmpty() && atomValencies.values.all { it.second == it.first }

  val moleculeStats = remember(atoms.toList(), bonds.toList()) {
    analyzeMolecule(atoms, bonds)
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Atom Element Selection Palette (C, H, O, N, S, F, Cl, Br, I)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
      Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Atom Element Palette", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          Text("Tap canvas to place", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(
            "C" to "Carbon (4)",
            "H" to "Hydrogen (1)",
            "O" to "Oxygen (2)",
            "N" to "Nitrogen (3)",
            "S" to "Sulfur (2/6)",
            "F" to "Fluorine (1)",
            "Cl" to "Chlorine (1)",
            "Br" to "Bromine (1)",
            "I" to "Iodine (1)"
          ).forEach { (elem, desc) ->
            val isSelected = selectedElement == elem
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) ElectricCyan.copy(alpha = 0.25f) else Color.Transparent)
                .border(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) ElectricCyan else Color(0xFF475569),
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable { selectedElement = elem }
                .padding(horizontal = 10.dp, vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                  modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(getElementColor(elem))
                )
                Text(
                  text = elem,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) ElectricCyan else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }
    }

    // Interactive 2D Drag & Drop Molecule Canvas
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(290.dp)
        .testTag("molecule_editor_canvas"),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
      shape = RoundedCornerShape(16.dp)
    ) {
      Box(modifier = Modifier.fillMaxWidth()) {
        Canvas(
          modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
            .pointerInput(Unit) {
              detectDragGestures(
                onDragStart = { offset ->
                  val touchedAtom = atoms.find {
                    val dx = it.x - offset.x
                    val dy = it.y - offset.y
                    dx * dx + dy * dy < 28f * 28f
                  }
                  if (touchedAtom != null) {
                    if (selectedAtomId != null && selectedAtomId != touchedAtom.id) {
                      // Connect or toggle bond between two atoms!
                      val existingIndex = bonds.indexOfFirst {
                        (it.atom1Id == selectedAtomId && it.atom2Id == touchedAtom.id) ||
                          (it.atom2Id == selectedAtomId && it.atom1Id == touchedAtom.id)
                      }
                      if (existingIndex >= 0) {
                        val currentBond = bonds[existingIndex]
                        if (currentBond.order < 3) {
                          bonds[existingIndex] = currentBond.copy(order = currentBond.order + 1)
                        } else {
                          bonds.removeAt(existingIndex) // cycle to delete
                        }
                      } else {
                        bonds.add(BondEdge(selectedAtomId!!, touchedAtom.id, bondOrderTool))
                      }
                      selectedAtomId = null
                    } else {
                      selectedAtomId = touchedAtom.id
                    }
                  } else {
                    // Tap on empty space: Add new atom!
                    atoms.add(AtomNode(nextAtomId++, selectedElement, offset.x, offset.y))
                    selectedAtomId = null
                  }
                },
                onDrag = { change, dragAmount ->
                  change.consume()
                  selectedAtomId?.let { id ->
                    val index = atoms.indexOfFirst { it.id == id }
                    if (index >= 0) {
                      val current = atoms[index]
                      atoms[index] = current.copy(
                        x = (current.x + dragAmount.x).coerceIn(25f, size.width.toFloat() - 25f),
                        y = (current.y + dragAmount.y).coerceIn(25f, size.height.toFloat() - 25f)
                      )
                    }
                  }
                }
              )
            }
        ) {
          drawMoleculeCanvas(
            atoms = atoms,
            bonds = bonds,
            selectedAtomId = selectedAtomId,
            valencies = atomValencies
          )
        }

        // Action Overlay buttons
        Row(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(10.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Auto-Saturate with Hydrogen (+H)
          Button(
            onClick = {
              autoSaturateHydrogens(atoms, bonds, onNewId = { nextAtomId++ })
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(Icons.Default.AutoFixHigh, contentDescription = null, Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("+H Saturation", fontSize = 11.sp)
          }

          // Clear Canvas
          Button(
            onClick = {
              atoms.clear()
              bonds.clear()
              selectedAtomId = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = LaserCrimson),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = null, Modifier.size(14.dp))
            Text("Clear", fontSize = 11.sp)
          }
        }
      }
    }

    // Real-time Valency Diagnostics & IUPAC Identifier
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Compound Analysis", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (!hasAnyValencyError && isAllSatisfied) NeonEmerald else if (hasAnyValencyError) LaserCrimson else AtomicAmber)
            )
            Text(
              text = if (hasAnyValencyError) "VALENCY ERROR" else if (isAllSatisfied) "VALENCY VERIFIED" else "UNSATURATED",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = if (hasAnyValencyError) LaserCrimson else if (isAllSatisfied) NeonEmerald else AtomicAmber
            )
          }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          MeterDisplayBox(Modifier.weight(1.2f), "IUPAC / NAME", moleculeStats.iupacName, ElectricCyan)
          MeterDisplayBox(Modifier.weight(1f), "FORMULA", moleculeStats.formula, NeonEmerald)
          MeterDisplayBox(Modifier.weight(0.9f), "MOL WT", String.format("%.2f", moleculeStats.molecularWeight), AtomicAmber)
        }

        // Functional Family Classification
        Text(
          text = "Classification: ${moleculeStats.compoundClass} | Total Atoms: ${atoms.size} | Bonds: ${bonds.size}",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Quick Molecule Presets
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
      Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Quick Presets", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          LabRepository.moleculePresets.forEach { preset ->
            FilterChip(
              selected = false,
              onClick = {
                atoms.clear()
                bonds.clear()
                atoms.addAll(preset.atoms)
                bonds.addAll(preset.bonds)
                nextAtomId = (preset.atoms.maxOfOrNull { it.id } ?: 0) + 1
                selectedAtomId = null
              },
              label = { Text(preset.name, fontSize = 11.sp) }
            )
          }
        }
      }
    }

    // Interactive Organic Chemical Reaction Simulator
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Hub, null, tint = PlasmaViolet)
            Text("Organic Reaction Simulator", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
          Button(
            onClick = { showReactionPanel = !showReactionPanel },
            colors = ButtonDefaults.buttonColors(containerColor = PlasmaViolet),
            modifier = Modifier.height(32.dp)
          ) {
            Text(if (showReactionPanel) "Hide" else "Simulate", fontSize = 11.sp)
          }
        }

        if (showReactionPanel) {
          Text("Select an Organic Reaction to simulate:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LabRepository.organicReactions.forEach { rx ->
              val isSel = selectedReaction?.id == rx.id
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSel) PlasmaViolet.copy(alpha = 0.25f) else Color(0xFF1E293B))
                  .border(1.dp, if (isSel) PlasmaViolet else Color(0xFF334155), RoundedCornerShape(10.dp))
                  .clickable { selectedReaction = rx }
                  .padding(12.dp)
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(rx.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                  Text("Equation: ${rx.equation}", fontSize = 12.sp, color = ElectricCyan, fontFamily = FontFamily.Monospace)
                  Text("Reagent: ${rx.reagent} (${rx.condition})", fontSize = 11.sp, color = AtomicAmber)
                }
              }
            }
          }

          selectedReaction?.let { rx ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Reaction Mechanism & Observation", fontWeight = FontWeight.Bold, color = NeonEmerald, fontSize = 13.sp)
                Text(rx.mechanismNotes, fontSize = 12.sp, color = Color.LightGray)
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Yield Product:", fontSize = 12.sp, color = Color.Gray)
                  Text("${rx.productIupac} (${rx.productFormula})", fontWeight = FontWeight.Bold, color = ElectricCyan)
                }
              }
            }
          }
        }
      }
    }
  }
}

// Helpers for Molecule Canvas & Chemistry
data class MoleculeAnalysis(
  val iupacName: String,
  val formula: String,
  val molecularWeight: Double,
  val compoundClass: String
)

private fun computeAtomValencies(atoms: List<AtomNode>, bonds: List<BondEdge>): Map<Int, Pair<Int, Int>> {
  val map = mutableMapOf<Int, Pair<Int, Int>>() // atomId -> (maxValency, currentBonds)
  for (atom in atoms) {
    val maxV = when (atom.element) {
      "C" -> 4
      "H", "F", "Cl", "Br", "I" -> 1
      "O" -> 2
      "N" -> 3
      "S" -> 2 // can be 2, 4, 6
      else -> 4
    }
    val currentB = bonds.filter { it.atom1Id == atom.id || it.atom2Id == atom.id }.sumOf { it.order }
    map[atom.id] = Pair(maxV, currentB)
  }
  return map
}

private fun analyzeMolecule(atoms: List<AtomNode>, bonds: List<BondEdge>): MoleculeAnalysis {
  if (atoms.isEmpty()) {
    return MoleculeAnalysis("Empty Canvas", "-", 0.0, "None")
  }

  val counts = mutableMapOf<String, Int>()
  for (atom in atoms) {
    counts[atom.element] = (counts[atom.element] ?: 0) + 1
  }

  val c = counts["C"] ?: 0
  val h = counts["H"] ?: 0
  val o = counts["O"] ?: 0
  val n = counts["N"] ?: 0
  val s = counts["S"] ?: 0
  val cl = counts["Cl"] ?: 0
  val br = counts["Br"] ?: 0

  // Molecular weight
  val mw = c * 12.011 + h * 1.008 + o * 15.999 + n * 14.007 + s * 32.06 + cl * 35.45 + br * 79.904

  // Hill system formula: C, then H, then others alphabetical
  val sb = StringBuilder()
  if (c > 0) sb.append("C").append(if (c > 1) "$c" else "")
  if (h > 0) sb.append("H").append(if (h > 1) "$h" else "")
  listOf("Br", "Cl", "N", "O", "S").forEach { elem ->
    val count = counts[elem] ?: 0
    if (count > 0) sb.append(elem).append(if (count > 1) "$count" else "")
  }
  val formula = sb.toString()

  val hasTripleBond = bonds.any { it.order == 3 }
  val hasDoubleBond = bonds.any { it.order == 2 }

  // Detect IUPAC Name and Class
  val (name, compClass) = when {
    c == 1 && h == 4 && o == 0 -> "Methane" to "Alkane"
    c == 2 && h == 6 && o == 0 -> "Ethane" to "Alkane"
    c == 3 && h == 8 && o == 0 -> "Propane" to "Alkane"
    c == 4 && h == 10 && o == 0 -> "Butane" to "Alkane"
    c == 2 && h == 4 && o == 0 && hasDoubleBond -> "Ethene" to "Alkene"
    c == 3 && h == 6 && o == 0 && hasDoubleBond -> "Propene" to "Alkene"
    c == 2 && h == 2 && o == 0 && hasTripleBond -> "Ethyne (Acetylene)" to "Alkyne"
    c == 1 && h == 4 && o == 1 -> "Methanol" to "Alcohol"
    c == 2 && h == 6 && o == 1 -> "Ethanol" to "Alcohol"
    c == 2 && h == 4 && o == 2 -> "Ethanoic Acid (Acetic Acid)" to "Carboxylic Acid"
    c == 1 && h == 2 && o == 2 -> "Methanoic Acid (Formic Acid)" to "Carboxylic Acid"
    c == 3 && h == 6 && o == 1 && hasDoubleBond -> "Propan-2-one (Acetone)" to "Ketone"
    c == 2 && h == 4 && o == 1 && hasDoubleBond -> "Ethanal (Acetaldehyde)" to "Aldehyde"
    c == 6 && h == 6 && o == 0 && hasDoubleBond -> "Benzene" to "Aromatic"
    c == 6 && h == 6 && o == 1 -> "Phenol" to "Phenol"
    c == 1 && h == 3 && cl == 1 -> "Chloromethane" to "Haloalkane"
    c == 1 && h == 5 && n == 1 -> "Methylamine" to "Amine"
    c == 3 && h == 6 && o == 2 -> "Methyl Ethanoate" to "Ester"
    c == 2 && h == 6 && o == 1 && atoms.count { it.element == "O" } == 1 -> "Dimethyl Ether" to "Ether"
    else -> {
      val defaultClass = when {
        o >= 2 -> "Carboxylic / Ester"
        o == 1 -> "Alcohol / Carbonyl"
        n >= 1 -> "Amine / Nitrogenous"
        hasTripleBond -> "Alkyne"
        hasDoubleBond -> "Alkene"
        else -> "Hydrocarbon Derivative"
      }
      "Compound ($formula)" to defaultClass
    }
  }

  return MoleculeAnalysis(name, formula, mw, compClass)
}

private fun autoSaturateHydrogens(
  atoms: MutableList<AtomNode>,
  bonds: MutableList<BondEdge>,
  onNewId: () -> Int
) {
  val currentAtoms = atoms.toList()
  for (atom in currentAtoms) {
    if (atom.element == "H") continue
    val maxV = when (atom.element) {
      "C" -> 4
      "O" -> 2
      "N" -> 3
      "S" -> 2
      else -> 1
    }
    val currentB = bonds.filter { it.atom1Id == atom.id || it.atom2Id == atom.id }.sumOf { it.order }
    val needed = maxV - currentB
    if (needed > 0) {
      for (i in 0 until needed) {
        val angle = (i.toFloat() / needed) * 2f * 3.14159f + 0.5f
        val hx = (atom.x + 45f * cos(angle)).coerceIn(30f, 600f)
        val hy = (atom.y + 45f * sin(angle)).coerceIn(30f, 260f)
        val hId = onNewId()
        atoms.add(AtomNode(hId, "H", hx, hy))
        bonds.add(BondEdge(atom.id, hId, 1))
      }
    }
  }
}

private fun getElementColor(element: String): Color {
  return when (element) {
    "C" -> AtomCarbon
    "H" -> AtomHydrogen
    "O" -> AtomOxygen
    "N" -> AtomNitrogen
    "S" -> AtomSulfur
    "F", "Cl", "Br", "I" -> AtomHalogen
    else -> Color(0xFF64748B)
  }
}

private fun DrawScope.drawMoleculeCanvas(
  atoms: List<AtomNode>,
  bonds: List<BondEdge>,
  selectedAtomId: Int?,
  valencies: Map<Int, Pair<Int, Int>>
) {
  // Grid background
  val gridStep = 30f
  for (x in 0..(size.width / gridStep).toInt()) {
    drawLine(Color(0xFF1E293B).copy(alpha = 0.4f), Offset(x * gridStep, 0f), Offset(x * gridStep, size.height), strokeWidth = 1f)
  }
  for (y in 0..(size.height / gridStep).toInt()) {
    drawLine(Color(0xFF1E293B).copy(alpha = 0.4f), Offset(0f, y * gridStep), Offset(size.width, y * gridStep), strokeWidth = 1f)
  }

  // Draw Bonds
  for (bond in bonds) {
    val a1 = atoms.find { it.id == bond.atom1Id } ?: continue
    val a2 = atoms.find { it.id == bond.atom2Id } ?: continue

    val p1 = Offset(a1.x, a1.y)
    val p2 = Offset(a2.x, a2.y)

    when (bond.order) {
      1 -> {
        drawLine(Color(0xFF94A3B8), p1, p2, strokeWidth = 4f, cap = StrokeCap.Round)
      }
      2 -> {
        // Double bond (2 parallel lines)
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        val len = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
        val nx = -dy / len * 4.5f
        val ny = dx / len * 4.5f

        drawLine(Color(0xFF94A3B8), Offset(p1.x + nx, p1.y + ny), Offset(p2.x + nx, p2.y + ny), strokeWidth = 3f, cap = StrokeCap.Round)
        drawLine(Color(0xFF94A3B8), Offset(p1.x - nx, p1.y - ny), Offset(p2.x - nx, p2.y - ny), strokeWidth = 3f, cap = StrokeCap.Round)
      }
      3 -> {
        // Triple bond
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        val len = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
        val nx = -dy / len * 6f
        val ny = dx / len * 6f

        drawLine(Color(0xFF94A3B8), p1, p2, strokeWidth = 3f, cap = StrokeCap.Round)
        drawLine(Color(0xFF94A3B8), Offset(p1.x + nx, p1.y + ny), Offset(p2.x + nx, p2.y + ny), strokeWidth = 2.5f, cap = StrokeCap.Round)
        drawLine(Color(0xFF94A3B8), Offset(p1.x - nx, p1.y - ny), Offset(p2.x - nx, p2.y - ny), strokeWidth = 2.5f, cap = StrokeCap.Round)
      }
    }
  }

  // Draw Atoms
  for (atom in atoms) {
    val isSelected = atom.id == selectedAtomId
    val valPair = valencies[atom.id] ?: Pair(4, 0)
    val maxV = valPair.first
    val curV = valPair.second

    val atomRadius = if (atom.element == "H") 16f else 22f
    val center = Offset(atom.x, atom.y)

    // Selection Halo
    if (isSelected) {
      drawCircle(Color(0xFF00E5FF).copy(alpha = 0.35f), radius = atomRadius + 8f, center = center)
      drawCircle(Color(0xFF00E5FF), radius = atomRadius + 8f, center = center, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
    }

    // Atom Circle Body
    drawCircle(
      color = getElementColor(atom.element),
      radius = atomRadius,
      center = center
    )

    // Valency status ring around atom
    val ringColor = when {
      curV > maxV -> LaserCrimson
      curV == maxV -> NeonEmerald
      else -> AtomicAmber
    }
    drawCircle(
      color = ringColor,
      radius = atomRadius,
      center = center,
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
    )
  }
}
