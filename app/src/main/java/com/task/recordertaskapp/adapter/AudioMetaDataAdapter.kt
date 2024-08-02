package com.task.recordertaskapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.task.recordertaskapp.R
import com.task.recordertaskapp.models.AudioMetadata

class AudioMetadataAdapter(
    private var audioList: List<AudioMetadata>,
    private val onPlayClick: (AudioMetadata) -> Unit,
    private val onDeleteClick: (AudioMetadata) -> Unit
) : RecyclerView.Adapter<AudioMetadataAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.rvItemTitle)
        val playButton: ImageView = itemView.findViewById(R.id.play)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteRecording)
        val itemLayout: ConstraintLayout = itemView.findViewById(R.id.recording_item_layout)

        init {
            playButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPlayClick(audioList[position])
                }
            }
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(audioList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recording_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val audioMetadata = audioList[position]
        holder.titleTextView.text = audioMetadata.name
        holder.itemLayout.setBackgroundColor(holder.itemView.context.getColor(audioMetadata.backgroundColor))
    }

    override fun getItemCount(): Int {
        return audioList.size
    }

    fun updateData(newAudioList: List<AudioMetadata>) {
        audioList = newAudioList
        notifyDataSetChanged()
    }
}
