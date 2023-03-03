package com.taitsmith.schoolio.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.taitsmith.schoolio.data.SchoolResponseModel;
import com.taitsmith.schoolio.databinding.ListItemSchoolBinding;

import java.util.Objects;

public class SchoolAdapter extends ListAdapter<SchoolResponseModel,
        SchoolAdapter.SchoolResponseModelViewHolder> {

    private final LayoutInflater layoutInflater;
    public OnItemClickListener onItemClickListener;

    //we want to be able to click on a school from the list, so we'll make an interface and then
    //implement it in the fragment
    public interface OnItemClickListener {
        void onItemClick(SchoolResponseModel school);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }
    public SchoolAdapter(Context context, OnItemClickListener listener) {
        super(new DiffUtil.ItemCallback<SchoolResponseModel>() {
            @Override
            public boolean areItemsTheSame(SchoolResponseModel oldItem, SchoolResponseModel newItem) {
                return Objects.equals(oldItem.getDbn(), newItem.getDbn());
            }

            @SuppressLint("DiffUtilEquals") //can solve by creating an equals() in the school object
            @Override
            public boolean areContentsTheSame(SchoolResponseModel oldItem, SchoolResponseModel newItem) {
                return oldItem.equals(newItem);
            }
        });
        onItemClickListener = listener;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SchoolResponseModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemSchoolBinding binding = ListItemSchoolBinding.inflate(layoutInflater, parent, false);
        return new SchoolResponseModelViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolResponseModelViewHolder holder, int position) {
        SchoolResponseModel school = getItem(position);
        holder.bind(school);
    }

    public class SchoolResponseModelViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        private final ListItemSchoolBinding binding;

        public SchoolResponseModelViewHolder(ListItemSchoolBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        public void bind(SchoolResponseModel school) {
            binding.setSchoolListItem(school);
            binding.executePendingBindings();
            itemView.setTag(school);
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            onItemClickListener.onItemClick(getItem(position));
        }
    }
}
