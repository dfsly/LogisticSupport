package com.dfsly.android.logisticsupport.utils

import android.app.Service
import android.content.Context
import android.os.Vibrator

/**
 * 让手机振动milliseconds毫秒
 */
fun vibrate(context: Context, milliseconds: Long) {
    val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
    if (vib.hasVibrator()) { //判断手机硬件是否有振动器
        vib.vibrate(milliseconds)
    }
}

/**
 * 让手机以我们自己设定的pattern[]模式振动
 * long pattern[] = {1000, 20000, 10000, 10000, 30000};
 */
fun vibrate(context: Context, pattern: LongArray?, repeat: Int) {
    val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
    if (vib.hasVibrator()) {
        vib.vibrate(pattern, repeat)
    }
}

/**
 * 取消震动
 */
fun vibrateCancel(context: Context) { //关闭震动
    val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
    vib.cancel()
}