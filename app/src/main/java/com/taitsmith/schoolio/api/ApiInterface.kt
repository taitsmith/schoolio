package com.taitsmith.schoolio.api

import com.taitsmith.schoolio.data.SatResponseModel
import com.taitsmith.schoolio.data.SchoolResponseModel
import com.taitsmith.schoolio.ui.MainActivity
import retrofit2.http.GET
import retrofit2.http.Query

/**interface to hold all of our retrofit api queries. uses kotlin so we can take advantage of
 * coroutines and use suspend functions as well as kotlin's runCatching to handle errors from
 * the api response. i've honestly never seen an api endpoint where the key is in the form
 * $$api_key, so that's a first for me.
*/
interface ApiInterface {

    //get a list of all the schools returned by the api (there's a lot)
    @GET("resource/s3k6-pzi2.json")
    suspend fun fetchSchools(
        @Query("\$\$app_token") token: String = MainActivity.appToken
    ): List<SchoolResponseModel>

    //get list of schools and sat results
    @GET("resource/f9bf-2cp4.json")
    suspend fun fetchSatData(
        @Query("\$\$app_token") token: String = MainActivity.appToken,
        @Query("dbn") dbn: String
    ): List<SatResponseModel>
}