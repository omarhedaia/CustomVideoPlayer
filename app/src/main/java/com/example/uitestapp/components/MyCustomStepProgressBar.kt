package com.example.uitestapp.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun MyCustomStepProgressBar(numberOfSteps:Float, modifier: Modifier, progress:Int){

    val indicator:Float =  (1/numberOfSteps)
    Row(modifier=modifier) {
        repeat(numberOfSteps.toInt()) {

            //region color animation

//             val bgColor: Color by animateColorAsState(
//                 if (progress > it) Color.Green else if (progress == it) Color.Magenta else Color.Gray,
//                 animationSpec = tween(1500, easing = EaseInOut)
//             )


//             Box(modifier = Modifier
//                 .padding(2.dp)
//                 .clip(RoundedCornerShape(25.dp))
//                 .weight(indicator)
//                 .background(bgColor)
//                 .height(5.dp)
//                 .clickable(indication = null, interactionSource = remember {
//                     MutableInteractionSource()
//                 }) {
//
//
//                 })

            //endregion

            val state = if (progress > it) 100 else if (progress == it) 50 else 0
            val color = if (progress > it) Color.Magenta else if (progress == it) Color.Magenta else Color.Gray

            ModuleAnimatedStepBox(statValue = state , statMaxValue = 100, statColor = color, animDelay = it*100, modifier = modifier
                .weight(indicator)
                .padding(4.dp))

        }


    }

}




@Composable
fun ModuleAnimatedStepBox(
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    height: Dp = 3.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0,
    modifier: Modifier,
) {

    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val curPercent = animateFloatAsState(
        targetValue = if(animationPlayed) {
            statValue / statMaxValue.toFloat()
        } else 0f,
        animationSpec = tween(
            animDuration,
            animDelay
        )
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Box(
        modifier = modifier
            .height(height)
            .clip(CircleShape)
            .background(
                if (isSystemInDarkTheme()) {
                    Color(0xFF505050)
                } else {
                    Color.LightGray
                }
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(curPercent.value)
                .background(statColor)
                .padding(horizontal = 3.dp)
        ) {

        }
    }
}