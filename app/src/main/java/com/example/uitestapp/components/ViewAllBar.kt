package com.example.uitestapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ViewAllBar(modifier: Modifier=Modifier,title:String,action:()->Unit)
{
    Box(modifier = modifier.fillMaxWidth().padding(20.dp)) {

        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp,modifier = Modifier.align(
            Alignment.CenterStart), textAlign = TextAlign.Center)
        Row(modifier = Modifier.wrapContentWidth().align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "View all")
            Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "View all",modifier=Modifier.clickable { action() })
        }
        

    }
    
    
}


@Preview(showSystemUi = true, showBackground = true, device = Devices.NEXUS_5)
@Composable
fun PreviewViewAll()
{
    ViewAllBar(title = "Continue Learning") {

    }


}