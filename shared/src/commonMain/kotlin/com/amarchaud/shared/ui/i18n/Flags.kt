package com.amarchaud.shared.ui.i18n

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.amarchaud.shared.data.Language

private val FranceBlue = Color(0xFF000091)
private val FranceRed = Color(0xFFE1000F)
private val UnionBlue = Color(0xFF012169)
private val UnionRed = Color(0xFFC8102E)

/**
 * Flag of the [language], drawn rather than written as an emoji: emoji flags are not rendered
 * on Windows nor by the Compose web renderer.
 */
@Composable
fun LanguageFlag(language: Language, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        when (language) {
            Language.French -> drawFrenchFlag()
            Language.English -> drawUnionJack()
        }
    }
}

private fun DrawScope.drawFrenchFlag() {
    val stripe = size.width / 3
    drawRect(FranceBlue, size = Size(stripe, size.height))
    drawRect(Color.White, topLeft = Offset(stripe, 0f), size = Size(stripe, size.height))
    drawRect(FranceRed, topLeft = Offset(stripe * 2, 0f), size = Size(size.width - stripe * 2, size.height))
}

/** Union Jack, simplified for small sizes (centered red saltire). */
private fun DrawScope.drawUnionJack() {
    val unit = size.height / 30f
    drawRect(UnionBlue)
    listOf(Color.White to 6f, UnionRed to 2f).forEach { (color, width) ->
        drawLine(color, Offset.Zero, Offset(size.width, size.height), strokeWidth = width * unit)
        drawLine(color, Offset(0f, size.height), Offset(size.width, 0f), strokeWidth = width * unit)
    }
    listOf(Color.White to 10f, UnionRed to 6f).forEach { (color, width) ->
        val half = width * unit / 2
        drawRect(color, topLeft = Offset(size.width / 2 - half, 0f), size = Size(half * 2, size.height))
        drawRect(color, topLeft = Offset(0f, size.height / 2 - half), size = Size(size.width, half * 2))
    }
}
