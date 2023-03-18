package com.taitsmith.schoolio.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taitsmith.schoolio.data.SchoolResponseModel
import com.taitsmith.schoolio.databinding.FragmentMainBinding
import com.taitsmith.schoolio.utils.SchoolAdapter
import com.taitsmith.schoolio.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var schoolAdapter: SchoolAdapter

    private var schoolListView: RecyclerView? = null
    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        schoolListView = binding.schoolListView

        //show the progress bar while we're loading
        binding.mainProgressBar.visibility = View.VISIBLE
        setObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        schoolListView!!.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        schoolAdapter = SchoolAdapter({
            viewModel.getSatScores(it)
        },{
            //doesn't actually do anything at the moment.
            Log.d("main fragment", "long click")
        })
        schoolListView!!.adapter = schoolAdapter
        viewModel.getSchoolList()
        setObservers()
    }

    private fun setObservers() {
        //our fragment doesn't need to know about the goings-on behind the scenes so
        //we'll just wait for some data to show up and then display it
        viewModel.schools.observe(viewLifecycleOwner) { schoolResponseModels: List<SchoolResponseModel?> ->
            schoolAdapter.submitList(schoolResponseModels)
            binding.mainProgressBar.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        //cleanup to avoid leaks
        super.onDestroyView()
        binding.root.removeAllViews()
        schoolAdapter.submitList(null)
        schoolListView!!.removeAllViews()
        schoolListView!!.layoutManager = null
        schoolListView!!.adapter = null
        viewModel.schools.removeObservers(viewLifecycleOwner)
    }
}