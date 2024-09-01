package com.scanners.docsscanners.screens.common

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scanners.docsscanners.R


@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp, 0.dp),
        contentAlignment = Alignment.Center,
    ){
        Column (
           horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = message, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.errorDescription), fontSize = 16.sp, textAlign = TextAlign.Center)
        }
    }
}