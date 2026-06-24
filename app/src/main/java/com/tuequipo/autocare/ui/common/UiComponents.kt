package com.tuequipo.autocare.ui.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tuequipo.autocare.ui.theme.StatusPendiente
import com.tuequipo.autocare.ui.theme.StatusRealizado
import com.tuequipo.autocare.ui.theme.StatusVencido
import com.tuequipo.autocare.ui.theme.AutoCareBorder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val HeaderGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF5B6DFF), Color(0xFF7C3AED), Color(0xFF22D3EE))
)

val AccentGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF5B6DFF), Color(0xFF7C3AED))
)

val FabGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFD946EF), Color(0xFF7C3AED))
)

fun estadoColor(estado: String): Color = when (estado) {
    "Realizado" -> StatusRealizado
    "Vencido" -> StatusVencido
    else -> StatusPendiente
}

fun estadoContainerColor(estado: String): Color = estadoColor(estado).copy(alpha = 0.18f)

@Composable
fun StatusChip(estado: String) {
    val color = estadoColor(estado)
    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.16f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(color, RoundedCornerShape(50))
                .width(6.dp)
                .padding(vertical = 3.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(estado, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
fun FilterChipAutoCare(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .height(38.dp)
            .background(
                brush = if (selected) AccentGradient else Brush.linearGradient(
                    listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface)
                ),
                shape = shape
            )
            .border(
                width = 1.dp,
                color = if (selected) Color.Transparent else AutoCareBorder,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GradientFloatingActionButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .shadow(14.dp, shape, clip = false)
            .clip(shape)
            .background(FabGradient, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("+", color = Color.White, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(8.dp))
        Text("Nuevo", color = Color.White, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun ShimmerBox(modifier: Modifier = Modifier, shape: RoundedCornerShape = RoundedCornerShape(8.dp)) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha = transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing)),
        label = "shimmerAlpha"
    )
    Box(
        modifier = modifier.background(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha.value),
            shape
        )
    )
}

fun formatFecha(fecha: String): String = runCatching {
    LocalDate.parse(fecha).format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-PE")))
}.getOrDefault(fecha)

fun iconoTipoMantenimiento(tipo: String): ImageVector = when (tipo) {
    "Correctivo" -> Icons.Default.Construction
    "Mejora" -> Icons.Default.CarRepair
    "Preventivo" -> Icons.Default.Build
    else -> Icons.Default.ErrorOutline
}

fun Modifier.pressScale(interactionSource: InteractionSource): Modifier = composed {
    val pressed = interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(if (pressed.value) 0.97f else 1f, label = "pressScale")
    scale(scale.value)
}
