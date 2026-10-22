package com.example.compusconnect

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CampusLogo(size: Dp = 48.dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_mark),
        contentDescription = "CampusConnect logo",
        modifier = modifier.size(size)
    )
}
