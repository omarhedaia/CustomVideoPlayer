package com.example.uitestapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun MyModuleProgressBar(modifier: Modifier, title:String, numberOfSteps: Float, progress: Int){

    Card(modifier = modifier.padding(16.dp)) {

        Box(modifier = Modifier.padding(16.dp)) {

            Column() {

                Row {

                    Text(text = title, fontFamily = FontFamily.SansSerif, modifier = Modifier.weight(0.5f))
                    Text(text = "$progress / ${numberOfSteps.toInt()}", textAlign = TextAlign.End,modifier = Modifier.weight(0.5f))


                }
                Spacer(modifier = Modifier.height(10.dp))

                MyCustomStepProgressBar(numberOfSteps = numberOfSteps, modifier = Modifier.fillMaxWidth(), progress = progress)


            }

        }

    }



}