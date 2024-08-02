package com.task.recordertaskapp.fragments

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.task.recordertaskapp.R
import com.task.recordertaskapp.RecordingScreenActivity
import com.task.recordertaskapp.adapter.AudioMetadataAdapter
import com.task.recordertaskapp.models.AudioMetadata

class RecordingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var audioMetadataAdapter: AudioMetadataAdapter
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recordings, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_recordings)

        val fab = view.findViewById<ImageView>(R.id.fab_record)
        fab.setOnClickListener {
            Intent(requireActivity(), RecordingScreenActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        setupRecyclerView()
        loadAudioMetadata()
    }

    private fun setupRecyclerView() {
        audioMetadataAdapter = AudioMetadataAdapter(
            audioList = emptyList(),
            onPlayClick = { audioMetadata ->
                playAudio(audioMetadata)
            },
            onDeleteClick = { audioMetadata ->
                deleteRecording(audioMetadata)
            }
        )
        recyclerView.adapter = audioMetadataAdapter
    }

    private fun loadAudioMetadata() {
        val sharedPreferences = requireActivity().getSharedPreferences("audio_prefs", AppCompatActivity.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("audio_metadata", "[]")
        val type = object : TypeToken<List<AudioMetadata>>() {}.type
        val audioMetadataList: List<AudioMetadata> = gson.fromJson(json, type)
        audioMetadataAdapter.updateData(audioMetadataList)
    }

    private fun deleteRecording(audioMetadata: AudioMetadata) {
        val sharedPreferences = requireActivity().getSharedPreferences("audio_prefs", AppCompatActivity.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("audio_metadata", "[]")
        val type = object : TypeToken<List<AudioMetadata>>() {}.type
        val audioMetadataList: MutableList<AudioMetadata> = gson.fromJson(json, type)
        audioMetadataList.remove(audioMetadata)
        val updatedJson = gson.toJson(audioMetadataList)
        sharedPreferences.edit().putString("audio_metadata", updatedJson).apply()
        audioMetadataAdapter.updateData(audioMetadataList)
    }

    private fun playAudio(audioMetadata: AudioMetadata) {
        mediaPlayer?.release() // Release any previously playing media
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioMetadata.filePath) // Set the data source to the audio file
            prepare()
            start()
            setOnCompletionListener {
                it.release() // Release the MediaPlayer resources when playback is complete
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release() // Ensure MediaPlayer is released when the view is destroyed
        mediaPlayer = null
    }
}
