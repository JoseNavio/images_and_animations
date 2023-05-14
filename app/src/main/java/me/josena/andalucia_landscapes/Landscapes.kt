package me.josena.andalucia_landscapes

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import me.josena.andalucia_landscapes.data.Landscape
import me.josena.andalucia_landscapes.databinding.ActivityLandscapesBinding
import me.josena.andalucia_landscapes.databinding.ImageRatingLayoutBinding

class Landscapes : AppCompatActivity() {

    private lateinit var binding: ActivityLandscapesBinding
    private var initialX: Float = 0.toFloat()

    private val landscapes = listOf(
        Landscape("La Aquisgrana","https://www.josena.me/files/images/aquisgrana.png"),
        Landscape("La Cimbarra","https://www.josena.me/files/images/cimbarra.jpg"),
        Landscape("Collado de la aviación (Despeñaperros)","https://www.josena.me/files/images/collado_aviacion.jpg"),
        Landscape("El Centenillo","https://www.josena.me/files/images/el_centenillo.jpg")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandscapesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startTitleAnimation()
        startFlipper()
        setTouchSwipe()
    }

    private fun startTitleAnimation() {

        binding.labelTitle.alpha = 0f
        binding.flipperImages.alpha = 0f

        binding.labelTitle.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(2000)
            .withEndAction {
                binding.labelTitle.alpha = 0f
                binding.flipperImages.animate()
                    .alpha(1f).duration = 1000
            }
            .start()
    }

    private fun setTouchSwipe() {

        binding.flipperImages.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = event.x
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val finalX = event.x
                        if (initialX < finalX) {
                            // Swipe from left to right (previous screen)
                            binding.flipperImages.showPrevious()
                            playSwipeSound()

                        } else if (initialX > finalX) {
                            // Swipe from right to left (next screen)
                            binding.flipperImages.showNext()
                            playSwipeSound()

                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun startFlipper() {

        for (landscape in landscapes) {

            val ratingBinding = ImageRatingLayoutBinding.inflate(layoutInflater)
            //Set image
            Glide.with(this)
                .load(landscape.url)
                .into(ratingBinding.imageLandscape)
            //Set name
            ratingBinding.labelLandscapeName.text = landscape.name

            binding.flipperImages.addView(ratingBinding.root)
        }
//        binding.flipperImages.startFlipping()
    }

    private fun playSwipeSound() {
        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.swipe_sound)
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release() // Release the MediaPlayer resources when sound playback is complete
        }

        mediaPlayer.start()
    }
}