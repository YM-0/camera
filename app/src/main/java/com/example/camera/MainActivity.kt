package com.example.camera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val idEditText = findViewById<EditText>(R.id.idEditText)
        val loadButton = findViewById<Button>(R.id.loadButton)

        loadButton.setOnClickListener {
            val id = idEditText.text.toString()

            // Launch a coroutine to perform the network request off the main thread
            CoroutineScope(Dispatchers.IO).launch {
                val url = "http://192.168.0.1:5005/download/$id"
                val request = Request.Builder()
                    .url(url)
                    .build()

                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        val responseBody = response.body
                        if (responseBody != null) {
                            val inputStream = responseBody.byteStream()
                            val bitmap = BitmapFactory.decodeStream(inputStream)

                            // Switch back to the main thread to update the UI
                            withContext(Dispatchers.Main) {
                                imageView.setImageBitmap(bitmap)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
