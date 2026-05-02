package com.example.mindcarediary.Activities.IntroActivity.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.mindcarediary.ui.theme.LightBlue

@Composable
@Preview
fun MeuDiarioScreen (
    onStartClick: () -> Unit = {}
){
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
    ){
        var (image,btn,title) = createRefs()
        Surface(modifier = Modifier
            .fillMaxSize(),
            color = LightBlue
        ) {
        }
    }
}