package com.sakusisswa.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryInfo(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

object CategoryHelper {
    val categories = listOf(
        CategoryInfo("Makan & Minum", Icons.Default.Fastfood, Color(0xFFFF7043)),
        CategoryInfo("Uang Jajan", Icons.Default.Payments, Color(0xFF42A5F5)),
        CategoryInfo("Transportasi", Icons.Default.DirectionsBus, Color(0xFFFFA726)),
        CategoryInfo("Tabungan", Icons.Default.Savings, Color(0xFF66BB6A)),
        CategoryInfo("Belanja", Icons.Default.ShoppingCart, Color(0xFFAB47BC)),
        CategoryInfo("Hiburan", Icons.Default.Movie, Color(0xFFEC407A)),
        CategoryInfo("Gaji / Upah", Icons.Default.MonetizationOn, Color(0xFF26A69A)),
        CategoryInfo("Hadiah", Icons.Default.CardGiftcard, Color(0xFFFFCA28)),
        CategoryInfo("Tagihan", Icons.Default.Receipt, Color(0xFFEF5350)),
        CategoryInfo("Lainnya", Icons.Default.Category, Color(0xFF78909C))
    )

    fun getCategoryInfo(name: String): CategoryInfo {
        return categories.find { it.name.equals(name, ignoreCase = true) }
            ?: CategoryInfo(name.ifBlank { "Lainnya" }, Icons.Default.Category, Color(0xFF78909C))
    }
}
