package com.example.uitestapp.components

import android.R.attr.value
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.R
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    currentVideo: Video,
    onVideoStop: (Long, Int) -> Unit
) {

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val activity = context.findActivity()
    val URLString = currentVideo.videoUrl
    val completedTime = currentVideo.completedTime

    var fullScreen by rememberSaveable {
        mutableStateOf(false)

    }

    var isPlayingSaveable by rememberSaveable {
        mutableStateOf(false)
    }

    val mediaItem = MediaItem.Builder()
        .apply {
            setUri(
                URLString
            )
            setMediaMetadata(
                MediaMetadata.Builder()
//                                    .setDisplayTitle("My Video")
                    .build()
            )
        }
        .build()



    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .apply {
                setSeekBackIncrementMs(PLAYER_SEEK_BACK_INCREMENT)
                setSeekForwardIncrementMs(PLAYER_SEEK_FORWARD_INCREMENT)
            }
            .build()
            .apply {
                setMediaItem(
                    mediaItem
                )
                prepare()
                playWhenReady = isPlayingSaveable
            }
    }
    var shouldShowControlFlag by rememberSaveable {
        mutableStateOf(false)

    }

    val shouldShowControls by produceState(initialValue = true, key1 = shouldShowControlFlag) {
        if (shouldShowControlFlag) {
            value = true
            delay(4000)
            shouldShowControlFlag = false
        } else {
            delay(1000)
            value = false
        }
    }

    var totalDuration by rememberSaveable { mutableStateOf(0L) }

    var currentTimeSaveable by rememberSaveable {
        mutableStateOf(completedTime)
    }
    var currentTime by remember { mutableStateOf(currentTimeSaveable) }
    val currentTimeCoroutine = rememberCoroutineScope()

    var bufferedPercentage by rememberSaveable { mutableStateOf(0) }

    var playbackState by rememberSaveable { mutableStateOf(exoPlayer.playbackState) }
    val currentTimeCoroutineScope = rememberCoroutineScope()

    var isVideoCompleted by rememberSaveable() {
        mutableStateOf(currentVideo.isCompleted)

    }
    var currentCompletedTime by rememberSaveable {
        mutableStateOf(currentTimeSaveable)
    }

    val retriever = remember {
        MediaMetadataRetriever()
    }
    var framesIntervals by remember(totalDuration) {
        mutableStateOf( totalDuration/1000 / 20)
    }
    val thumbnailFramesList = remember {
        mutableListOf<Bitmap>()
    }
    val thumbnailIntervals = remember {
        mutableListOf<Long>()
    }





    BackHandler(enabled = fullScreen) {
            activity.changeOrientation()
    }

    Box(
        modifier = modifier.then(
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            }
        ),
    ) {

        DisposableEffect(key1 = URLString) {
            val listener =
                object : Player.Listener {
                    override fun onEvents(
                        player: Player,
                        events: Player.Events
                    ) {
                        super.onEvents(player, events)
                        totalDuration = player.duration.coerceAtLeast(0L)
                        bufferedPercentage = player.bufferedPercentage.coerceAtLeast(0)
                        isPlayingSaveable = player.isPlaying
                        playbackState = player.playbackState
                        currentTimeCoroutineScope.launch {
                            while(true)
                            {
                                currentTime = player.currentPosition.coerceAtLeast(0L)
                                currentTimeSaveable = currentTime
                                if(currentTime >= currentCompletedTime) {currentCompletedTime = currentTime}
                                delay(1000)

                            }
                        }
                        framesIntervals = totalDuration / 1000 /20
                    }
                }



            exoPlayer.addListener(listener)
            exoPlayer.seekTo(currentTimeSaveable)
            retriever.setDataSource(context,Uri.Builder().path(URLString).build())


            onDispose {
                exoPlayer.removeListener(listener)
                exoPlayer.release()
                currentTimeCoroutine.cancel()
                retriever.release()
                onVideoStop(currentCompletedTime, currentVideo.id)

            }
        }

        LaunchedEffect(key1 = framesIntervals ){
            if (framesIntervals != 0L)
            {
                for (i in 0 until 20){
                    val time = i * framesIntervals
                    thumbnailIntervals.add(time)
                    Log.d("TAG", "VideoPlayer: thumbnail intervals = ${thumbnailIntervals}")
                    retriever.getFrameAtTime(time*1000000,MediaMetadataRetriever.OPTION_CLOSEST)?.let { thumbnailFramesList.add(it) }
                    Log.d("TAG", "VideoPlayer: thumbnail list = ${thumbnailFramesList}")
                }
            }
        }

        AndroidView(
            modifier =
            Modifier
                .fillMaxWidth()
                .matchParentSize()
                .clickable(interactionSource = remember {
                    MutableInteractionSource()
                }, indication = null) {
                    shouldShowControlFlag = shouldShowControlFlag.not()
                },
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                }
            }
        )

        var controlsWidth by remember {
            mutableStateOf(0)
        }

        PlayerControls(
            changeOrientation = {
                activity.changeOrientation()
                fullScreen = !fullScreen
            },
            modifier = Modifier
                .matchParentSize()
                .onGloballyPositioned {
                    controlsWidth = it.size.width

                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { tapOffset ->

                            if (isVideoCompleted) {
                                if (tapOffset.x >= (controlsWidth / 2)) {
                                    exoPlayer.seekForward()
                                    shouldShowControlFlag = true
                                } else {
                                    exoPlayer.seekBack()
                                }
                            } else {
                                if (tapOffset.x < (controlsWidth / 2)) {
                                    exoPlayer.seekBack()
                                    shouldShowControlFlag = true
                                }
                            }
                        },
                        onTap = { shouldShowControlFlag = !shouldShowControlFlag }
                    )

                },
            isVisible = { shouldShowControls },
            isPlaying = { isPlayingSaveable },
            title = { exoPlayer.mediaMetadata.displayTitle.toString() },
            playbackState = { playbackState },
            onReplayClick = { exoPlayer.seekBack()
                            shouldShowControlFlag = true},
            onForwardClick = {
                if((exoPlayer.seekForwardIncrement + currentTime) > currentCompletedTime )
                {
                    exoPlayer.seekTo(currentCompletedTime)
                }else{

                    exoPlayer.seekForward()
                }
                shouldShowControlFlag = true
                             },
            onPauseToggle = {
                when {
                    isPlayingSaveable -> {
                        // pause the video
                        exoPlayer.pause()
                    }

                    isPlayingSaveable.not() &&
                            playbackState == STATE_ENDED -> {
                        exoPlayer.seekTo(0)
                        exoPlayer.playWhenReady = true
                    }

                    else -> {
                        // play the video
                        // it's already paused
                        exoPlayer.play()
                    }
                }
//                isPlaying = isPlaying.not()
                isPlayingSaveable = !isPlayingSaveable
                shouldShowControlFlag = true
            },
            totalDuration = { totalDuration },
            currentTime = { currentTime },
            bufferedPercentage = { bufferedPercentage },
            onSeekChanged = { timeMs: Float ->
                if (isVideoCompleted) {
                    exoPlayer.seekTo(timeMs.toLong())
                }else if(timeMs < currentCompletedTime)
                {
                    exoPlayer.seekTo(timeMs.toLong())
                }else{
                    exoPlayer.seekTo(currentCompletedTime)
                }
                shouldShowControlFlag = true
            },
            isCompleted = {isVideoCompleted},
            currentCompletedTime = currentCompletedTime,
            thumbnailFrameList = thumbnailFramesList,
            thumbnailIntervals = thumbnailIntervals
        )
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlayerControls(
    changeOrientation: () -> Unit,
    modifier: Modifier,
    isVisible: () -> Boolean,
    isPlaying: () -> Boolean,
    title: () -> String,
    onReplayClick: () -> Unit,
    onForwardClick: () -> Unit,
    onPauseToggle: () -> Unit,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    playbackState: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    isCompleted:() -> Boolean,
    currentCompletedTime: Long,
    thumbnailFrameList : List<Bitmap>,
    thumbnailIntervals:List<Long>
) {

    val visible = remember(isVisible()) { isVisible() }




    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.6f))) {
//            TopControl(
//                modifier = Modifier
//                    .align(Alignment.TopStart)
//                    .fillMaxWidth(),
//                title = title
//            )

            CenterControls(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                isPlaying = isPlaying,
                onReplayClick = onReplayClick,
                onForwardClick = onForwardClick,
                onPauseToggle = onPauseToggle,
                playbackState = playbackState,
                isCompleted = isCompleted,
                currentTime = currentTime,
                currentCompletedTime = currentCompletedTime
            )

            BottomControls(
                changeOrientation = changeOrientation,
                modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .animateEnterExit(
                        enter =
                        slideInVertically(
                            initialOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        ),
                        exit =
                        slideOutVertically(
                            targetOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        )
                    ),
                totalDuration = totalDuration,
                currentTime = currentTime,
                bufferedPercentage = bufferedPercentage,
                onSeekChanged = onSeekChanged,
                thumbnailFrameList = thumbnailFrameList,
                thumbnailInterval = thumbnailIntervals
            )
        }
    }
}

@Composable
private fun TopControl(modifier: Modifier = Modifier, title: () -> String) {
    val videoTitle = remember(title()) { title() }

//    Text(
//        modifier = modifier.padding(16.dp),
//        text = videoTitle,
//        style = MaterialTheme.typography.bodyMedium,
//        color = Color.Magenta
//    )
}

@Composable
private fun CenterControls(
    modifier: Modifier = Modifier,
    isPlaying: () -> Boolean,
    playbackState: () -> Int,
    onReplayClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onForwardClick: () -> Unit,
    isCompleted: () -> Boolean,
    currentTime: () -> Long,
    currentCompletedTime : Long
) {
    val isVideoPlaying = rememberSaveable(isPlaying()) { isPlaying() }

    val playerState = rememberSaveable(playbackState()) { playbackState() }

    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        IconButton(modifier = Modifier.size(40.dp), onClick = onReplayClick) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.exo_ic_rewind),
                contentDescription = "Replay 5 seconds"
            )
        }

        IconButton(modifier = Modifier.size(40.dp), onClick = onPauseToggle) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter =
                when {
                    isVideoPlaying -> {
                        painterResource(id = R.drawable.exo_icon_pause)
                    }

                    isVideoPlaying.not() && playerState == STATE_ENDED -> {
                        painterResource(id = R.drawable.exo_controls_repeat_one)
                    }

                    else -> {
                        painterResource(id = R.drawable.exo_icon_play)
                    }
                },
                contentDescription = "Play/Pause"
            )
        }

        if (isCompleted() || currentTime() < currentCompletedTime) {

            IconButton(modifier = Modifier.size(40.dp), onClick = onForwardClick) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    painter = painterResource(id = R.drawable.exo_ic_forward),
                    contentDescription = "Forward 10 seconds"
                )
            }
        } else {
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun BottomControls(
    changeOrientation: () -> Unit,
    modifier: Modifier = Modifier,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    thumbnailFrameList: List<Bitmap>,
    thumbnailInterval:List<Long>

) {


    val duration = rememberSaveable(totalDuration()) { totalDuration() }

    val videoTime = rememberSaveable(currentTime()) { currentTime() }

    val buffer = rememberSaveable(bufferedPercentage()) { bufferedPercentage() }

    Log.d("TAG", "bottom Controller : thumbnail List = ${thumbnailFrameList}")


    Column(modifier = modifier.padding(bottom = 32.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = buffer.toFloat(),
                enabled = false,
                onValueChange = { /*do nothing*/ },
                valueRange = 0f..100f,
                colors =
                SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledActiveTrackColor = Color.Gray
                )
            )

            //----------------- slider with a thumbnail
            Column(modifier = Modifier.fillMaxWidth()) {


                SliderWithThumbnail(
                    modifier = Modifier.fillMaxWidth(),
                    value = videoTime.toFloat(),
                    onValueChange = onSeekChanged,
                    thumbnailFrameList = thumbnailFrameList,
                    valueRange = 0f..duration.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTickColor = Color.White
                    ),
                    thumbnailIntervals = thumbnailInterval
                )
            }
//            Slider(
//                modifier = Modifier.fillMaxWidth(),
//                value = videoTime.toFloat(),
//                onValueChange = onSeekChanged,
//
//                valueRange = 0f..duration.toFloat(),
//                colors =
//                SliderDefaults.colors(
//                    thumbColor = Color.White,
//                    activeTickColor = Color.White
//                )
//            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "${videoTime.formatMinSec()} / ${duration.formatMinSec()}",
                color = Color.White
            )

            val configuration = LocalConfiguration.current
            IconButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {
                    changeOrientation()
                }
            ) {
                Image(
                    contentScale = ContentScale.Crop,
                    painter = when (configuration.orientation) {
                        android.content.res.Configuration.ORIENTATION_PORTRAIT -> painterResource(id = R.drawable.exo_icon_fullscreen_enter)
                        android.content.res.Configuration.ORIENTATION_LANDSCAPE -> painterResource(
                            id = R.drawable.exo_icon_fullscreen_exit
                        )

                        else -> painterResource(id = R.drawable.exo_icon_fullscreen_enter)
                    },
                    contentDescription = "Enter/Exit fullscreen"
                )
            }
        }
    }
}

fun Long.formatMinSec(): String {
    return if (this == 0L) {
        "..."
    } else {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    }
}

@Composable
fun SliderWithThumbnail(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange:ClosedFloatingPointRange<Float>,
    colors: SliderColors,
    thumbnailFrameList: List<Bitmap>,
    thumbnailIntervals:List<Long>
) {
//    var isDragging by rememberSaveable { mutableStateOf(false) }
//    Log.d("TAG", "SliderWithThumbnail: isDragging = ${isDragging}")

    var thumbOffset by remember { mutableStateOf(0.dp) }


    val interactionSource = remember {
        MutableInteractionSource()
    }
    val draggingState by interactionSource.collectIsDraggedAsState()
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {

        thumbOffset = getSliderOffset(value=value, valueRange = valueRange, maxWidth, labelWidth = 30.dp)

        Slider(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            valueRange = valueRange,
            colors = colors,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
        )

        if (draggingState) {

            val thumbX = thumbOffset
            Log.d("TAG", "SliderWithThumbnail: thumbx = ${thumbX} ")
            val currentFrame = thumbnailFrameList.getOrNull(thumbnailIntervals.indexOfFirst { (value/1000) < it })
            Log.d("TAG", "SliderWithThumbnail: thumbnail List = ${thumbnailFrameList} ")
            Log.d("TAG", "SliderWithThumbnail: current frame = ${currentFrame} ")
            if (currentFrame!=null)
            Thumb(currentFrame = currentFrame, offset = thumbX)
        }
    }
}

@Composable
fun Thumb(modifier: Modifier = Modifier,currentFrame:Bitmap,offset: Dp) {

    Box(
        modifier = modifier
            .size(50.dp)
            .offset(x = (offset), y = -40.dp)
            .background(Color.Blue)
            .defaultMinSize(24.dp)
    ) {


//        Text(
//            text = value.roundToInt().toString(),
//            color = Color.White,
//            modifier = Modifier
//                .fillMaxSize(),
//            textAlign = TextAlign.Center
//        )
//        val context = LocalContext.current

//        val currentFrame = getVideoFrame(context,uri,(currentTime * 1000).roundToLong())


//        Log.d("current time in thumb", "Thumb: ${currentTime.roundToLong()}")


//
//            currentFrame =
//                getVideoFrame(context = context, uri = uri, time = currentTime.roundToLong())


//        Log.d("currentFrame", "Thumb: ${currentFrame.toString()}")
//

        Image(
            modifier = Modifier.align(Alignment.Center),
            bitmap = currentFrame.asImageBitmap(),
            contentDescription = "current frame"
        )

//        Text(text = "${currentTime}")

    }

}
//
//@Composable
//fun SliderWithThumbnailExample() {
//    var sliderValue by remember { mutableStateOf(0.5f) }
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
//            }
//        )
//    }
//}


private fun getSliderOffset(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    boxWidth: Dp,
    labelWidth: Dp
): Dp {

    val coerced = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val positionFraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)

    return (boxWidth - labelWidth) * positionFraction
}


// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)

fun getVideoFrame(context: Context?,retriever: MediaMetadataRetriever, time: Long): Bitmap? {
    var bitmap: Bitmap? = null

    try {
        bitmap = retriever.getFrameAtTime(time,MediaMetadataRetriever.OPTION_CLOSEST)
    } catch (ex: RuntimeException) {
        ex.printStackTrace()
    } finally {
        try {
            retriever.release()
        } catch (ex: RuntimeException) {
            ex.printStackTrace()
        }
    }
    return bitmap
}

private const val PLAYER_SEEK_BACK_INCREMENT = 5 * 1000L // 5 seconds
private const val PLAYER_SEEK_FORWARD_INCREMENT = 10 * 1000L // 10 seconds