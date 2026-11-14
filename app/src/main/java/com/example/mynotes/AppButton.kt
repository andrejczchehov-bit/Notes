package com.example.mynotes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Переиспользуемый компонент AppButton с поддержкой стилей и размеров (Лабораторная работа №8).
 */
enum class ButtonStyle {
    PRIMARY, SECONDARY, OUTLINED
}

enum class ButtonSize {
    SMALL, MEDIUM, LARGE
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    size: ButtonSize = ButtonSize.MEDIUM,
    enabled: Boolean = true
) {
    val contentPadding = when (size) {
        ButtonSize.SMALL -> PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ButtonSize.MEDIUM -> PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        ButtonSize.LARGE -> PaddingValues(horizontal = 24.dp, vertical = 14.dp)
    }

    val textSize = when (size) {
        ButtonSize.SMALL -> 12.sp
        ButtonSize.MEDIUM -> 14.sp
        ButtonSize.LARGE -> 16.sp
    }

    when (style) {
        ButtonStyle.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                contentPadding = contentPadding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = text, fontSize = textSize)
            }
        }
        ButtonStyle.SECONDARY -> {
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                contentPadding = contentPadding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(text = text, fontSize = textSize)
            }
        }
        ButtonStyle.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                contentPadding = contentPadding,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = text, fontSize = textSize)
            }
        }
    }
}
