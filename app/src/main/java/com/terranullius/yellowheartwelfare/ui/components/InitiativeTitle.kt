package com.terranullius.yellowheartwelfare.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun InitiativeTitle(modifier: Modifier = Modifier, text: String, size: TextStyle = MaterialTheme.typography.h4) {
    Text(
        text = text,
        style = size.copy(
            color = Color(0xFFF8E4E6),
            background = Color(0xFFA22B3E),
            fontWeight = FontWeight.Bold
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    InitiativeTitle(text = "Enlightening the youth")
}