package com.taitsmith.schoolio.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.taitsmith.schoolio.R;
import com.taitsmith.schoolio.data.SatResponseModel;
import com.taitsmith.schoolio.data.SchoolResponseModel;
import com.taitsmith.schoolio.databinding.FragmentSchoolInfoBinding;

public class SchoolInfoFragment extends Fragment {
    private SchoolResponseModel school;
    private SatResponseModel satScores;
    private FragmentSchoolInfoBinding binding;

    public SchoolInfoFragment(SchoolResponseModel school, @Nullable SatResponseModel satScores) {
        //some schools on the list don't have sat scores available
        if (satScores != null) this.satScores = satScores;
        this.school = school;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSchoolInfoBinding.inflate(inflater, container, false);
        binding.setSchool(school);
        //some schools don't have sat score data
        if (satScores != null) {
            binding.setSatScore(satScores);
            setupTextViews();
        }
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * we'll set the sat-related textview text programmatically to avoid doing it all in xml,
     * because a lot of people have a lot of very strong opinions about doing that since it
     * involves calling String.format()
     */
    void setupTextViews() {
        binding.detailsNumberOfTakers.setVisibility(View.VISIBLE);
        binding.detailsMathAvg.setVisibility(View.VISIBLE);
        binding.detailsReadingAvg.setVisibility(View.VISIBLE);
        binding.detailsWritingAvg.setVisibility(View.VISIBLE);

        binding.detailsNumberOfTakers.setText(String.format(
                getString(R.string.details_number_of_test_takers), satScores.getNumOfSatTestTakers()));
        binding.detailsWritingAvg.setText(String.format(
                getString(R.string.details_writing_avg), satScores.getSatWritingAvgScore()));
        binding.detailsReadingAvg.setText(String.format(
                getString(R.string.details_reading_avg), satScores.getSatCriticalReadingAvgScore()));
        binding.detailsMathAvg.setText(String.format(
                getString(R.string.details_math_avg), satScores.getSatMathAvgScore()));

    }
}
