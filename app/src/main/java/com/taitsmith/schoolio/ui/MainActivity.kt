package com.taitsmith.schoolio.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.taitsmith.schoolio.BuildConfig
import com.taitsmith.schoolio.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        setContent{
            ShowLoading()
        }
        setObservers()
        viewModel.getSchoolList()
    }

    private fun setObservers() {
        viewModel.schools.observe(this) {
            setContent { 
                SchoolDataList(schools = it)
            }
        }
    }

    companion object {
        const val appToken = BuildConfig.appToken
    }
}
