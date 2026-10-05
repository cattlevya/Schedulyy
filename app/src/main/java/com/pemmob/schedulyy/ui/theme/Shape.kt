package com.pemmob.schedulyy.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

/**
 * Shape khusus Folder-Tab: kartu dengan tab menonjol di pojok kiri atas seperti tab map arsip / folder fisik.
 */
class FolderTabShape(
    private val tabWidth: Dp = 100.dp,
    private val tabHeight: Dp = 18.dp,
    private val cornerRadius: Dp = 10.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val tabW = with(density) { tabWidth.toPx() }.coerceAtMost(size.width * 0.65f)
        val tabH = with(density) { tabHeight.toPx() }
        val radius = with(density) { cornerRadius.toPx() }

        val path = Path().apply {
            // Mulai dari kiri bawah tab
            moveTo(0f, tabH + radius)
            // Sudut kiri atas tab
            arcTo(
                rect = Rect(0f, 0f, radius * 2, radius * 2),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Garis atas tab
            lineTo(tabW - radius, 0f)
            // Sudut kanan atas tab
            arcTo(
                rect = Rect(tabW - radius * 2, 0f, tabW, radius * 2),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Turun ke pundak folder kanan
            lineTo(tabW, tabH)
            // Lengkungan transisi ke garis atas folder
            lineTo(size.width - radius, tabH)
            // Sudut kanan atas folder
            arcTo(
                rect = Rect(size.width - radius * 2, tabH, size.width, tabH + radius * 2),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Sisi kanan
            lineTo(size.width, size.height - radius)
            // Sudut kanan bawah
            arcTo(
                rect = Rect(size.width - radius * 2, size.height - radius * 2, size.width, size.height),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Sisi bawah
            lineTo(radius, size.height)
            // Sudut kiri bawah
            arcTo(
                rect = Rect(0f, size.height - radius * 2, radius * 2, size.height),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Kembali ke sisi kiri
            close()
        }
        return Outline.Generic(path)
    }
}
