package com.taitsmith.schoolio.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.taitsmith.schoolio.data.SchoolResponseModel
import com.taitsmith.schoolio.databinding.ListItemSchoolBinding
import com.taitsmith.schoolio.utils.SchoolAdapter.SchoolResponseModelViewHolder

class SchoolAdapter(
    private val onItemClicked: (SchoolResponseModel) -> Unit,
    private val onItemLongClick: (SchoolResponseModel) -> Unit
) : ListAdapter<SchoolResponseModel, SchoolResponseModelViewHolder>(DiffCallback) {
   companion object {
       private val DiffCallback = object: DiffUtil.ItemCallback<SchoolResponseModel>() {
           override fun areItemsTheSame(
               oldItem: SchoolResponseModel,
               newItem: SchoolResponseModel
           ): Boolean {
               return oldItem.dbn == newItem.dbn
           }

           @SuppressLint("DiffUtilEquals") //can solve by creating an equals() in the school object
           override fun areContentsTheSame(
               oldItem: SchoolResponseModel,
               newItem: SchoolResponseModel
           ): Boolean {
               return oldItem == newItem
           }
       }
   }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SchoolResponseModelViewHolder {
        val viewholder = SchoolResponseModelViewHolder(
            ListItemSchoolBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        viewholder.itemView.setOnClickListener {
            val position = viewholder.bindingAdapterPosition
            onItemClicked(getItem(position))
        }

        viewholder.itemView.setOnLongClickListener {
            val position = viewholder.bindingAdapterPosition
            onItemLongClick(getItem(position))
            true
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: SchoolResponseModelViewHolder, position: Int) {
        val school = getItem(position)
        holder.bind(school)
    }

    inner class SchoolResponseModelViewHolder(private val binding: ListItemSchoolBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ){
        fun bind(school: SchoolResponseModel?) {
            binding.schoolListItem = school
        }
    }
}