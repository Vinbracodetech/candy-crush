package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundSynth(private val isSoundEnabled: () -> Boolean = { true }) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 44100

    private fun playToneSequence(tones: List<Pair<Double, Int>>) {
        if (!isSoundEnabled()) return
        scope.launch {
            try {
                var totalSamples = 0
                for ((_, durMs) in tones) {
                    totalSamples += (sampleRate * durMs / 1000)
                }
                val buffer = ShortArray(totalSamples)
                var offset = 0
                for ((freq, durMs) in tones) {
                    val count = sampleRate * durMs / 1000
                    for (i in 0 until count) {
                        val envelope = if (i < count * 0.1) {
                            (i / (count * 0.1)).toFloat()
                        } else {
                            (1f - (i - count * 0.1f) / (count * 0.9f)).coerceAtLeast(0f)
                        }
                        val sample = (sin(2.0 * PI * i * freq / sampleRate) * 0.5 * envelope * Short.MAX_VALUE).toInt()
                        if (offset + i < buffer.size) {
                            buffer[offset + i] = sample.toShort()
                        }
                    }
                    offset += count
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                // Auto release after sound finishes
                scope.launch {
                    kotlinx.coroutines.delay((totalSamples * 1000L / sampleRate) + 200)
                    audioTrack.release()
                }
            } catch (_: Exception) {
                // Ignore audio hardware errors
            }
        }
    }

    fun playSwap() {
        playToneSequence(listOf(523.25 to 50, 659.25 to 60))
    }

    fun playInvalidSwap() {
        playToneSequence(listOf(220.0 to 90, 180.0 to 120))
    }

    fun playMatch(combo: Int = 1) {
        val baseFreq = when (combo.coerceIn(1, 7)) {
            1 -> 523.25 // C5
            2 -> 587.33 // D5
            3 -> 659.25 // E5
            4 -> 698.46 // F5
            5 -> 783.99 // G5
            6 -> 880.00 // A5
            else -> 1046.50 // C6
        }
        playToneSequence(listOf(baseFreq to 80, (baseFreq * 1.25) to 120))
    }

    fun playLineBlast() {
        playToneSequence(listOf(400.0 to 40, 800.0 to 40, 1200.0 to 60, 1600.0 to 90))
    }

    fun playBombExplosion() {
        playToneSequence(listOf(180.0 to 60, 120.0 to 80, 80.0 to 140))
    }

    fun playColorBomb() {
        playToneSequence(
            listOf(
                523.25 to 50,
                659.25 to 50,
                783.99 to 50,
                1046.50 to 80,
                1318.51 to 120
            )
        )
    }

    fun playWinFanfare() {
        playToneSequence(
            listOf(
                523.25 to 100,
                659.25 to 100,
                783.99 to 100,
                1046.50 to 200,
                880.00 to 120,
                1046.50 to 350
            )
        )
    }

    fun playGameOver() {
        playToneSequence(
            listOf(
                440.0 to 150,
                392.0 to 150,
                349.23 to 180,
                293.66 to 350
            )
        )
    }

    fun playButtonClick() {
        playToneSequence(listOf(880.0 to 30))
    }

    fun playBoosterActivate() {
        playToneSequence(listOf(600.0 to 60, 900.0 to 60, 1200.0 to 100))
    }
}
