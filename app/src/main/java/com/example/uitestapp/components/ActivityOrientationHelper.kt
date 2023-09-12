package com.example.uitestapp.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

fun Activity.setLandscape(){
    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Activity.setPortrait(){
    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}


fun Activity.changeOrientation(){
    if (this.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    {
        this.setPortrait()

    }else{

        this.setLandscape()
    }
}
