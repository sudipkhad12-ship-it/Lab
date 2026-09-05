package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.components.MoleculeCanvasView

@Composable
fun MoleculeMakerScreen(
  onRewardEarned: (Int, Int) -> Unit = { _, _ -> }
) {
  Box(modifier = Modifier.fillMaxSize()) {
    MoleculeCanvasView(
      onMoleculeBuilt = { moleculeName, isSatisfied ->
        if (isSatisfied) {
          onRewardEarned(20, 25)
        }
      }
    )
  }
}
