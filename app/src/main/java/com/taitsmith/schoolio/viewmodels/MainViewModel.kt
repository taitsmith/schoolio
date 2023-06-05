package com.taitsmith.schoolio.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taitsmith.schoolio.api.ApiRepository
import com.taitsmith.schoolio.data.SatResponseModel
import com.taitsmith.schoolio.data.SchoolResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InterruptedIOException
import javax.inject.Inject

/**
 * our viewmodel for updating livedata with results from our api repository. done with kotlin so
 * we can use coroutines and runCatching to deal with errors that might come up.
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val apiRepository: ApiRepository): ViewModel() {
    private val _schools = MutableLiveData<List<SchoolResponseModel>>()
    var schools: LiveData<List<SchoolResponseModel>> = _schools

    private val _satData = MutableLiveData<SatResponseModel?>()
    var satData: MutableLiveData<SatResponseModel?> = _satData

    private val _errorMessage = MutableLiveData<String>()
    var errorMessage: LiveData<String> = _errorMessage

    //we'll set this when users click on a specific school so we can send it to
    //the school detail fragment along with the sat scores if they exist
    lateinit var selectedSchool: SchoolResponseModel

    fun getSchoolList() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                _schools.postValue(apiRepository.fetchSchools())
            }.onFailure {
                val s = when (it) {
                    //if you've got a slow network connection, we don't want to wait forever
                    is InterruptedIOException -> "TIMEOUT"
                    else -> "UNKNOWN"
                }
                _errorMessage.postValue(s)
                Log.d("CALL FAILURE: ", it.toString())
            }
        }
    }

    fun getSatScores(selectedSchool: SchoolResponseModel) {
        //we'll take the school picked by the user so we can pass that to the info fragment
        this.selectedSchool = selectedSchool
        viewModelScope.launch {
            kotlin.runCatching {
                //the sat endpoint returns an array of one, apparently.
                _satData.postValue(apiRepository.fetchSatScoresForSchool(selectedSchool.dbn)[0])
            }.onFailure {
                _errorMessage.value = when (it) {
                    //sometimes the api returns a 200/ok response, but an empty array
                    is IndexOutOfBoundsException -> "OOB"
                    else -> "UNKNOWN"
                }
                _satData.value = null
            }
        }
    }

    fun resetErrorMessage() {
        _errorMessage.value = ""
    }
}