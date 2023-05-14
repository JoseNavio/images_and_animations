package me.josena.andalucia_landscapes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.josena.andalucia_landscapes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLandscapes.setOnClickListener{
            val switchLandscapesIntent = Intent(applicationContext, Landscapes::class.java)
            startActivity(switchLandscapesIntent)
        }

        binding.buttonImages.setOnClickListener{
            val switchImagesIntent = Intent(applicationContext, Images::class.java)
            startActivity(switchImagesIntent)
        }
    }
}