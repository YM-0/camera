// DateSelectedImageFragment.kt

package com.example.camera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.Button
import android.widget.Toast
import android.app.DatePickerDialog
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*
import android.view.ViewGroup.LayoutParams

class DateSelectedImageFragment : Fragment() {
    private val client = OkHttpClient()
    private lateinit var imageLinearLayout: LinearLayout
    private var selectedDate = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_date_selected_image, container, false)

        imageLinearLayout = view.findViewById<LinearLayout>(R.id.imageLinearLayoutDateSelected)
        val selectDateButton = view.findViewById<Button>(R.id.selectDateButton)
        val loadButton = view.findViewById<Button>(R.id.loadButton)

        selectDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = context?.let { it1 ->
                DatePickerDialog(it1, { _, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    selectedDate = "" + year + String.format("%02d", monthOfYear + 1) + String.format("%02d", dayOfMonth)
                }, year, month, day)
            }

            dpd?.show()
        }

        loadButton.setOnClickListener {
            if (selectedDate.isNotEmpty()) {
                downloadAndDisplayImage(selectedDate)
            } else {
                Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
            }
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
