package com.task.recordertaskapp

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.task.recordertaskapp.models.AudioMetadata
import java.io.File

class RecordingScreenActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var timeIndicator: TextView
    private lateinit var buttonCross: Button
    private lateinit var buttonStart: Button
    private lateinit var buttonTick: Button
    private lateinit var colorBackgroundView: LinearLayout
    private var isRecording = false
    private var handler: Handler? = null
    private var seconds = 0
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            seconds++
            updateTimer()
            handler?.postDelayed(this, 1000)
        }
    }
    private val sharedPreferences by lazy {
        getSharedPreferences("audio_prefs", MODE_PRIVATE)
    }
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted
        } else {
            // Permission is denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording_screen)

        imageView = findViewById(R.id.image_view)
        timeIndicator = findViewById(R.id.time_indicator)
        buttonCross = findViewById(R.id.button_cross)
        buttonStart = findViewById(R.id.button_start)
        buttonTick = findViewById(R.id.button_tick)
        colorBackgroundView = findViewById(R.id.color_background_view)

        buttonStart.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        buttonCross.setOnClickListener {
            // Handle the cross button click (e.g., cancel recording)
            finish()
        }

        buttonTick.setOnClickListener {
            if (seconds > 0) {
                showSaveDialog()
            } else {
                // Handle case where there's no recording to save
            }
        }

        // Check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startRecording() {
        // Set up MediaRecorder
        audioFile = File(getExternalFilesDir(null), "recording_${System.currentTimeMillis()}.mp3")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile?.absolutePath)
            prepare()
            start()
        }
        isRecording = true
        handler = Handler()
        handler?.post(runnable)
        buttonStart.text = "Stop"
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false
        handler?.removeCallbacks(runnable)
        seconds = 0
        updateTimer()
        buttonStart.text = "Start"
    }

    private fun updateTimer() {
        val minutes = seconds / 60
        val hours = minutes / 60
        timeIndicator.text = String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)

        when {
            seconds > 3600 -> timeIndicator.setTextColor(getColor(R.color.red)) // 1 hour
            seconds > 60 -> timeIndicator.setTextColor(getColor(R.color.orange)) // 1 minute
            seconds > 0 -> timeIndicator.setTextColor(getColor(R.color.green)) // > 0 seconds
            else -> timeIndicator.setTextColor(getColor(R.color.black)) // Default
        }
    }

    private fun showSaveDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_save_recording, null)
        val nameEditText = view.findViewById<EditText>(R.id.name_edit_text)
        val colorPicker = view.findViewById<LinearLayout>(R.id.color_picker)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Save Recording")
            .setView(view)
            .setPositiveButton("OK") { dialog, _ ->
                val name = nameEditText.text.toString()
                val backgroundColor = getSelectedColor(colorPicker)
                saveAudioMetadata(name, backgroundColor)
                dialog.dismiss()
                finish() // Return to previous activity
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun getSelectedColor(colorPicker: LinearLayout): Int {
        // Get the selected color from the color picker
        // This is a placeholder. You should implement actual color selection.
        return R.color.white // Default color
    }

    private fun saveAudioMetadata(name: String, backgroundColor: Int) {
        val audioMetadata = AudioMetadata(name, backgroundColor, audioFile?.absolutePath ?: "")
        val currentList = getStoredAudioMetadata().toMutableList()
        currentList.add(audioMetadata)
        saveAudioMetadata(currentList)
    }

    private fun getStoredAudioMetadata(): List<AudioMetadata> {
        val gson = Gson()
        val json = sharedPreferences.getString("audio_metadata", "[]")
        val type = object : TypeToken<List<AudioMetadata>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveAudioMetadata(metadataList: List<AudioMetadata>) {
        val gson = Gson()
        val json = gson.toJson(metadataList)
        sharedPreferences.edit().putString("audio_metadata", json).apply()
    }
}
