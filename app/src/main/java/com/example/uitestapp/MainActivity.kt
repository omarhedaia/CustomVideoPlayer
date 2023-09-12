package com.example.uitestapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.uitestapp.components.SliderWithLabel
import com.example.uitestapp.components.SliderWithThumbnail
import com.example.uitestapp.components.Video
import com.example.uitestapp.components.VideoPlayer
import com.example.uitestapp.ui.theme.UiTestAppTheme
import dagger.hilt.android.AndroidEntryPoint

val samepleVideo = Video(1,"https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",10000,false)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {

            UiTestAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(Modifier.fillMaxSize()) {


//                        item {
                            VideoPlayer(currentVideo = samepleVideo) { latestTime, videoId ->

                                //add Latest time to video Model in the backend
                                if (samepleVideo.completedTime < latestTime) {

                                }
                            }
//
//                            }
//                        SliderWithThumbnailExample()
//                        var sliderValue by remember {
//                            mutableStateOf(0f)
//                        }
//                            SliderWithLabel(
//                                value = sliderValue,
//                                valueRange = 0f..100f,
//                                finiteEnd = true,
//                                onRadiusChange = {
//                                    sliderValue = it
//                                }
//                            )
//                        }
                        
//                        items(20){
//                            Text(text = "Hello $it")
//                        }
                    }

                }
            }
        }
    }


}
//
//@Composable
//fun SliderWithThumbnailExample() {
//    var sliderValue by remember { mutableStateOf(1f) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        SliderWithThumbnail(
//            value = sliderValue,
//            onValueChange = {
//                sliderValue = it
//            },
//            colors = SliderDefaults.colors(thumbColor = Color(R.color.Purple200)),
//            valueRange = 0f..100f,
//            currentFrame = Uri.EMPTY
//        )
//    }
//}


