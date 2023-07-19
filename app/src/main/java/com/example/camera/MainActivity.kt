package com.example.camera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*
import android.app.DatePickerDialog


class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var imageLinearLayout: LinearLayout
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageLinearLayout = findViewById<LinearLayout>(R.id.imageLinearLayout)
        val selectDateButton = findViewById<Button>(R.id.selectDateButton)
        val loadButton = findViewById<Button>(R.id.loadButton)
        val loadLatestButton = findViewById<Button>(R.id.loadLatestButton)

        selectDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                selectedDate = "" + year + String.format("%02d", monthOfYear + 1) + String.format("%02d", dayOfMonth)
            }, year, month, day)

            dpd.show()
        }

        loadButton.setOnClickListener {
            if (selectedDate.isNotEmpty()) {
                downloadAndDisplayImage(selectedDate)
            } else {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            }
        }

        loadLatestButton.setOnClickListener {
            downloadAndDisplayImage("1")
        }
    }

    private fun downloadAndDisplayImage(id: String) {
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
                            val imageView = ImageView(this@MainActivity)
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
