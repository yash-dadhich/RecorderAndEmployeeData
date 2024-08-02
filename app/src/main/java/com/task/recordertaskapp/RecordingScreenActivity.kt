package com.task.recordertaskapp

import android.Manifest
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
import android.widget.Toast
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
    private lateinit var buttonCross: ImageView
    private lateinit var buttonStart: ImageView
    private lateinit var buttonTick: ImageView
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
            if (isRecording) {
                cancelRecording()
                buttonTick.isEnabled=false
                buttonTick.setImageResource(R.drawable.ok_inactive)
            } else {
                finish()
            }
        }

        buttonTick.setOnClickListener {
            if (isRecording) {
                stopRecording()
                showSaveDialog()
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startRecording() {
        // Prepare for recording
        audioFile = File(externalCacheDir, "${System.currentTimeMillis()}.3gp")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile?.absolutePath)
            prepare()
            start()
        }
        isRecording = true
        buttonStart.setImageResource(R.drawable.stop) // Change to stop icon
        buttonTick.isEnabled = true // Enable tick button
        buttonCross.isEnabled = true // Enable tick button
        buttonCross.setImageResource(R.drawable.cross) // Change to cancel icon
        buttonTick.setImageResource(R.drawable.ok_active)
        seconds = 0
        handler = Handler()
        handler?.postDelayed(runnable, 0)
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false
        buttonStart.setImageResource(R.drawable.play_circle) // Change to play icon
        buttonTick.isEnabled = false // Disable tick button
        buttonCross.isEnabled = false
        buttonCross.setImageResource(R.drawable.ok_inactive)
        buttonCross.setImageResource(R.drawable.cancel_inactive)
        handler?.removeCallbacks(runnable)
    }

    private fun cancelRecording() {
        stopRecording() // Stop recording
        seconds = 0
        updateTimer() // Reset timer
    }

    private fun updateTimer() {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val time = String.format("%02d:%02d", minutes, remainingSeconds)
        timeIndicator.text = time
    }

    private fun showSaveDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_save_recording, null)
        val nameEditText: EditText = dialogView.findViewById(R.id.name_edit_text)
        val colorPicker: LinearLayout = dialogView.findViewById(R.id.color_picker)
        val confirmButton: Button = dialogView.findViewById(R.id.confirm_button)
        val colorBoxes = arrayOf(
            dialogView.findViewById<ImageView>(R.id.color_box1),
            dialogView.findViewById<ImageView>(R.id.color_box2),
            dialogView.findViewById<ImageView>(R.id.color_box3),
            dialogView.findViewById<ImageView>(R.id.color_box4),
            dialogView.findViewById<ImageView>(R.id.color_box5),
            dialogView.findViewById<ImageView>(R.id.color_box6)
        )

        var selectedColorId: Int? = null

        colorBoxes.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                // Clear previously selected color box
                colorBoxes.forEach { box -> box.setImageDrawable(null) }

                // Set tick mark on selected color box
                imageView.setImageResource(R.drawable.ic_check) // Replace with your tick drawable

                // Save selected color ID
                selectedColorId = when (index) {
                    0 -> R.color.EFE8FF
                    1 -> R.color.FFDFDF
                    2 -> R.color.E2F2FF
                    3 -> R.color.D9FFDD
                    4 -> R.color.FFEDD8
                    5 -> R.color.white
                    else -> null
                }

                // Enable the confirm button
                confirmButton.isEnabled = selectedColorId != null
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Save Recording")
            .setView(dialogView)
            .setCancelable(false) // Prevent dialog from being dismissed with back button or outside click
            .create()

        dialog.setOnShowListener {
            confirmButton.setOnClickListener {
                val name = nameEditText.text.toString()
                val finalName = if (name.isBlank()) getDefaultRecordingName() else name
                val backgroundColor = selectedColorId ?: R.color.white

                if (selectedColorId != null) {
                    saveAudioMetadata(finalName, backgroundColor)
                    dialog.dismiss() // Close the dialog after saving
                    finish() // Close the activity
                } else {
                    // Show a message to select a color if none is selected
                    Toast.makeText(this, "Please select a color.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun getDefaultRecordingName(): String {
        val lastRecordingNumber = sharedPreferences.getInt("recording_number", 0)
        val newRecordingNumber = lastRecordingNumber + 1
        sharedPreferences.edit().putInt("recording_number", newRecordingNumber).apply()
        return "Recording $newRecordingNumber"
    }

    private fun saveAudioMetadata(name: String, backgroundColor: Int) {
        val audioMetadata = AudioMetadata(name, backgroundColor, audioFile?.absolutePath ?: "")
        val audioMetadataList = getStoredAudioMetadata().toMutableList()
        audioMetadataList.add(audioMetadata)
        saveAudioMetadata(audioMetadataList)
    }

    private fun getStoredAudioMetadata(): List<AudioMetadata> {
        val json = sharedPreferences.getString("audio_metadata", "[]")
        val type = object : TypeToken<List<AudioMetadata>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun saveAudioMetadata(audioMetadataList: List<AudioMetadata>) {
        val json = Gson().toJson(audioMetadataList)
        sharedPreferences.edit().putString("audio_metadata", json).apply()
    }
}
