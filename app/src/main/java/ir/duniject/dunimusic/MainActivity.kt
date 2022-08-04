package ir.duniject.dunimusic

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.slider.Slider
import ir.duniject.dunimusic.databinding.ActivityMainBinding
import java.time.Duration
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var mediaPlayer: MediaPlayer
    lateinit var timer: Timer
    var isplaying = true
    var isUserChanging = false
    var isMute = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        papreMusic()
        binding.btnplaypause.setOnClickListener {
            configureMusic()
        }
        binding.btnGoAfter.setOnClickListener {
            goAfterMusic()
        }
        binding.btnGoBefore.setOnClickListener {
            goBeforeMusic()
        }
        binding.btnvolumeonoff.setOnClickListener {
            configureVolume()
        }

        binding.sliderMain.addOnChangeListener { slider, value, fromeUser ->
            binding.txtLeft.text = converterMillisToString(value.toLong())
            isUserChanging = fromeUser


        }

        binding.sliderMain.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {

            }

            override fun onStopTrackingTouch(slider: Slider) {
                mediaPlayer.seekTo(slider.value.toInt())
            }

        })


    }

    @SuppressLint("InlinedApi")
    private fun configureVolume() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (isMute) {
            audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
            binding.btnGoAfter.setImageResource(R.drawable.ic_volume_on)
            isMute = false

        } else {
            audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
            binding.btnvolumeonoff.setImageResource(R.drawable.ic_volume_off)
            isMute = true
        }
    }

    private fun goBeforeMusic() {
        val now = mediaPlayer.currentPosition
        val nowValue = now - 15000
        mediaPlayer.seekTo(nowValue)
    }

    private fun goAfterMusic() {
        val now = mediaPlayer.currentPosition
        val nowValue = now + 15000
        mediaPlayer.seekTo(nowValue)
    }

    private fun configureMusic() {
        if (isplaying) {
            mediaPlayer.pause()
            binding.btnplaypause.setImageResource(R.drawable.ic_play)
            isplaying = false
        } else {
            mediaPlayer.start()
            binding.btnplaypause.setImageResource(R.drawable.ic_pause)
            isplaying = true

        }
    }

    private fun papreMusic() {
        //تنظیمات اولیه
        mediaPlayer = MediaPlayer.create(this, R.raw.music_file)
        binding.btnplaypause.setImageResource(R.drawable.ic_pause)
        mediaPlayer.start()
        isplaying = true

        //تنظیمات اسلایدر

        binding.sliderMain.valueTo = mediaPlayer.duration.toFloat()
        binding.txtRight.text = converterMillisToString(mediaPlayer.duration.toLong())


         timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (!isUserChanging)
                        binding.sliderMain.value = mediaPlayer.currentPosition.toFloat()
                }

            }

        }, 1000, 1000)


    }

    private fun converterMillisToString(duration: Long): String {

        val second = duration / 1000 % 60
        val minute = duration / (1000 * 60) % 60
        return java.lang.String.format(Locale.US, "%02d:%02d", minute, second)

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        mediaPlayer.release()

    }


}