package me.josena.andalucia_landscapes

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.josena.andalucia_landscapes.databinding.ActivityImagesBinding
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Images : AppCompatActivity() {

    private lateinit var binding: ActivityImagesBinding
    private var errorFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Download file of images
        val destinationFile = File(applicationContext.filesDir, "photos.txt")

        //Create error file
        errorFile = File(applicationContext.filesDir, "errors.txt")
        errorFile?.createNewFile()
        //Deletes file content
        errorFile?.writeText("")

        //Download file in coroutine
        lifecycleScope.launch(Dispatchers.IO) {
            downloadFile("https://www.josena.me/files/photos.txt", destinationFile)
        }

        //Read file
        val file = File(applicationContext.filesDir, "photos.txt")
        val urls = mutableListOf<String>()

        try {
            file.bufferedReader().useLines { lines ->
                urls.addAll(lines)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val imageContainer = binding.imageContainer

        //Play all images
        lifecycleScope.launch {
            for (url in urls) {
                withContext(Dispatchers.Main) {
                    Glide.with(this@Images)
                        .load(url)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.mipmap.error_image_x) // Replace with your error image resource
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Toast.makeText(this@Images, "Failed to load image: $url", Toast.LENGTH_SHORT).show()
                                errorFile?.appendText("Fallo al mostrar la imagen: $url. --> ${getCurrentDateTime()}\n")
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                val imageView = ImageView(this@Images)
                                imageView.setImageDrawable(resource)
                                imageContainer.addView(imageView)

                                // Apply slide animation
                                imageView.translationX = -imageView.width.toFloat()
                                imageView.animate()
                                    .translationX(0f)
                                    .setDuration(500)
                                    .start()
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                // Handle when the image load is cleared
                            }
                        })
                }
                delay(3000) // Delay for 3 seconds before loading the next image
            }
        }
    }

    //Download a file which contains images urls
    private fun downloadFile(url: String, destinationFile: File) {
        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
            .url(url)
            .build()

        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val inputStream: ResponseBody? = response.body
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(destinationFile)
                inputStream?.byteStream()?.use { input ->
                    val buffer = ByteArray(4096)
                    var bytesRead: Int

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }

                    outputStream.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                outputStream?.close()
                inputStream?.close()
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "File could not be downloaded.", Toast.LENGTH_SHORT).show()
            }
            val errorMessage = "El fichero no se pudo descargar. --> ${getCurrentDateTime()}\n"
            errorFile?.appendText(errorMessage)
        }
    }
    // Function to get the current date and time
    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
 }