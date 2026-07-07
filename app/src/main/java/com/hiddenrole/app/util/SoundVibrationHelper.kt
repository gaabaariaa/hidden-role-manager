package com.hiddenrole.app.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * برای اعلام تغییر فاز (پایان تایمر) با صدای بوق و ویبره.
 */
class SoundVibrationHelper(private val context: Context) {

    private val toneGenerator: ToneGenerator? by lazy {
        try {
            ToneGenerator(AudioManager.STREAM_ALARM, 90)
        } catch (e: Exception) {
            null
        }
    }

    fun playPhaseEndAlert(soundEnabled: Boolean = true, vibrationEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
            } catch (e: Exception) {
                // اگه صدا در دسترس نبود، فقط ویبره کافیه
            }
        }
        if (vibrationEnabled) {
            vibrate()
        }
    }

    private fun vibrate() {
        try {
            val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (e: Exception) {
            // اگه ویبره در دسترس نبود، بی‌خیال می‌شیم
        }
    }
}
