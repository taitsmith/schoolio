package com.taitsmith.schoolio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.taitsmith.schoolio.R
import com.taitsmith.schoolio.data.SatResponseModel
import com.taitsmith.schoolio.data.SchoolResponseModel
import com.taitsmith.schoolio.databinding.FragmentSchoolInfoBinding

class SchoolInfoFragment(school: SchoolResponseModel, satScores: SatResponseModel?) : Fragment() {
    private val school: SchoolResponseModel

    private var satScores: SatResponseModel? = null

    private var _binding: FragmentSchoolInfoBinding? = null
    private val binding get() = _binding!!

    init {
        //some schools on the list don't have sat scores available
        if (satScores != null) this.satScores = satScores
        this.school = school
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSchoolInfoBinding.inflate(inflater, container, false)
        binding.school = school
        //some schools don't have sat score data
        if (satScores != null) {
            binding.satScore = satScores
            setupTextViews()
        }
        return binding.root
    }

    /**
     * we'll set the sat-related textview text programmatically to avoid doing it all in xml,
     * because a lot of people have a lot of very strong opinions about doing that since it
     * involves calling String.format()
     */
    private fun setupTextViews() {
        binding.detailsNumberOfTakers.visibility = View.VISIBLE
        binding.detailsMathAvg.visibility = View.VISIBLE
        binding.detailsReadingAvg.visibility = View.VISIBLE
        binding.detailsWritingAvg.visibility = View.VISIBLE
        binding.detailsNumberOfTakers.text = String.format(
            getString(R.string.details_number_of_test_takers), satScores!!.numOfSatTestTakers
        )
        binding.detailsWritingAvg.text = String.format(
            getString(R.string.details_writing_avg), satScores?.satWritingAvgScore
        )
        binding.detailsReadingAvg.text = String.format(
            getString(R.string.details_reading_avg), satScores?.satCriticalReadingAvgScore
        )
        binding.detailsMathAvg.text = String.format(
            getString(R.string.details_math_avg), satScores?.satMathAvgScore
        )
    }
}