package com.iregina.jesuswords.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iregina.jesuswords.R
import kotlinx.coroutines.launch

/** Pink of a saved heart (FavoritesView.swift: rgb 1.0, 0.36, 0.48). */
val FavoritePink = Color(0xFFFF5C7A)

/**
 * The ♡ toggle (FavoriteHeartButton in FavoritesView.swift): outline white ↔ filled pink,
 * a springy bounce and a light haptic tick.
 */
@Composable
fun FavoriteHeartButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val bounce = remember { Animatable(1f) }
    val bouncy = spring<Float>(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow)
    val icon = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_outline
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size + 22.dp)
            .semantics {
                role = Role.Button
                contentDescription = if (isFavorite) "Remove from favourites" else "Add to favourites"
            }
            .plainClickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onToggle()
                scope.launch {
                    bounce.animateTo(1.3f, bouncy)
                    bounce.animateTo(1f, bouncy)
                }
            },
    ) {
        Box(Modifier.graphicsLayer { scaleX = bounce.value; scaleY = bounce.value }) {
            // Soft dark halo (shadow black 40%, r2) so the white outline reads on light images.
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(size).offset(y = 0.5.dp).alpha(0.4f).blur(2.dp),
            )
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = if (isFavorite) FavoritePink else Color.White,
                modifier = Modifier.size(size),
            )
        }
    }
}
