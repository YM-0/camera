// LatestImageFragment.kt

package com.example.camera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import android.view.ViewGroup.LayoutParams

class LatestImageFragment : Fragment() {
    private val client = OkHttpClient()
    private lateinit var imageLinearLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_latest_image, container, false)

        imageLinearLayout = view.findViewById<LinearLayout>(R.id.imageLinearLayoutLatest)
        val loadLatestButton = view.findViewById<Button>(R.id.loadLatestButton)

        loadLatestButton.setOnClickListener {
            downloadAndDisplayImage("1")
        }

        return view
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
                            val imageView = ImageView(context)
                            imageView.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                            imageView.setImageBitmap(bitmap)
                            imageLinearLayout.addView(imageView)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
