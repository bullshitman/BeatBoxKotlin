package com.bullshitman.beatbox

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.SoundPool
import android.util.Log
import androidx.lifecycle.ViewModel
import java.io.IOException
import java.lang.Exception

private const val TAG = "BeatBox"
private const val SOUNDS_FOLDER = "sample_sounds"
private const val MAX_SOUNDS = 5

class BeatBox(private val assets: AssetManager) {
    val sounds: List<Sound>
    var currRate: Float = 0.0f

    private val soundPool = SoundPool.Builder().setMaxStreams(MAX_SOUNDS).build()

    init {
        sounds = loadSounds()
    }

    fun release() {
        soundPool.release()
    }

    private fun loadSounds() : List<Sound> {
        val soundNames: Array<String>
        try {
            soundNames = assets.list(SOUNDS_FOLDER)!!
        }catch (e: Exception) {
            return emptyList()
        }
        val sounds = mutableListOf<Sound>()
        soundNames.forEach { filename ->
            val assetPath = "$SOUNDS_FOLDER/$filename"
            val sound = Sound(assetPath)
            try {
                load(sound)
                sounds.add(sound)
            } catch (e: IOException) {
                Log.e(TAG, "Could not load sound $filename", e)
            }
        }
        return sounds
    }

    private fun load(sound: Sound) {
        val afd: AssetFileDescriptor = assets.openFd(sound.assetPath)
        val soundId = soundPool.load(afd, 1)
        sound.soundId = soundId
    }

    fun play(sound: Sound, currentRate: Float = 1.0f) {
        sound.soundId?.let {
            soundPool.play(it, 1.0f, 1.0f, 1, 0,  currRate)
        }
    }
    fun playWithRate(sound: Sound, currRate: Float) {
        sound.soundId?.let {
            soundPool.play(it, 1.0f, 1.0f, 1, 0, currRate )
        }
    }
}