package com.vako.abook.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import com.vako.abook.R

@Composable
fun debugPlaceholder(@DrawableRes debugPreview: Int = R.drawable.ic_launcher_background) =
    if (LocalInspectionMode.current) {
        painterResource(id = debugPreview)
    } else null