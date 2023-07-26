package com.example.camera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Button
import android.widget.ToggleButton
import android.widget.EditText
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.Exception
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.delayEach

class LatestImageFragment : Fragment() {
    private val client = OkHttpClient()
    private lateinit var imageView: ImageView
    private lateinit var intervalEditText: EditText
    private var job: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_latest_image, container, false)

        imageView = view.findViewById<ImageView>(R.id.imageViewLatest)
        intervalEditText = view.findViewById<EditText>(R.id.intervalEditText)
        val loadLatestButton = view.findViewById<Button>(R.id.loadLatestButton)
        val autoLoadToggle = view.findViewById<ToggleButton>(R.id.autoLoadToggle)

        loadLatestButton.setOnClickListener {
            downloadAndDisplayImage("1")
        }

        autoLoadToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val interval = intervalEditText.text.toString().toLongOrNull() ?: 5L // default to 5 seconds if not valid
                startAutoLoad(interval)
            } else {
                stopAutoLoad()
            }
        }

        return view
    }

    private fun startAutoLoad(interval: Long) {
        job = CoroutineScope(Dispatchers.IO).launch {
            flow {
                while (isActive) {
                    emit(Unit)
                    delay(interval * 1000)
                }
            }.collect {
                downloadAndDisplayImage("1")
            }
        }
    }

    private fun stopAutoLoad() {
        job?.cancel()
        job = null
    }

    private fun downloadAndDisplayImage(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = "http://192.168.0.1:5005/download/$id"
            val request = Request.Builder().url(url).build()
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = response.body
                    if (responseBody != null) {
                        val inputStream = responseBody.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)

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
