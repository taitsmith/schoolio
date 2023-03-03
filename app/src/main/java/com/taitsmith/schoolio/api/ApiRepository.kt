package com.taitsmith.schoolio.api

import com.taitsmith.schoolio.data.SatResponseModel
import com.taitsmith.schoolio.data.SchoolResponseModel
import com.taitsmith.schoolio.ui.MainActivity
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Inject

/**
 * for making our api calls.
 */
@Module
@InstallIn(ViewModelComponent::class)
class ApiRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun fetchSchools() : List<SchoolResponseModel> {
        return apiInterface.fetchSchools(MainActivity.appToken)
    }

    suspend fun fetchSatScoresForSchool(dbn: String) : List<SatResponseModel> {
        return apiInterface.fetchSatData(MainActivity.appToken, dbn)
    }
}