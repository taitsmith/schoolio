package com.taitsmith.schoolio.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taitsmith.schoolio.data.SchoolResponseModel;
import com.taitsmith.schoolio.databinding.FragmentMainBinding;
import com.taitsmith.schoolio.utils.SchoolAdapter;
import com.taitsmith.schoolio.viewmodels.MainViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends Fragment implements SchoolAdapter.OnItemClickListener{
    MainViewModel viewModel;
    private SchoolAdapter schoolAdapter;
    private RecyclerView schoolListView;
    private FragmentMainBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        schoolAdapter = new SchoolAdapter(getContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        schoolListView = binding.schoolListView;

        //show the progress bar while we're loading
        binding.mainProgressBar.setVisibility(View.VISIBLE);

        setObservers();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        schoolListView.setLayoutManager(new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false));
        schoolListView.setAdapter(schoolAdapter);
        viewModel.getSchoolList();
        setObservers();
    }

    private void setObservers() {
        //our fragment doesn't need to know about the goings-on behind the scenes so
        //we'll just wait for some data to show up and then display it
        viewModel.getSchools().observe(getViewLifecycleOwner(), schoolResponseModels
                -> {
            schoolAdapter.submitList(schoolResponseModels);
            binding.mainProgressBar.setVisibility(View.INVISIBLE);
        });
    }

    @Override
    public void onItemClick(SchoolResponseModel school) {
        viewModel.getSatScores(school);
    }

    @Override
    public void onDestroyView() {
        //cleanup to avoid leaks
        super.onDestroyView();
        binding.getRoot().removeAllViews();
        binding = null;
        schoolAdapter.submitList(null);
        schoolListView.removeAllViews();
        schoolListView.setLayoutManager(null);
        schoolListView.setAdapter(null);
        viewModel.getSchools().removeObservers(getViewLifecycleOwner());
    }
}