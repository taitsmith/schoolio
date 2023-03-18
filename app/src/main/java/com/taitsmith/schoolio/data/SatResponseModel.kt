package com.taitsmith.schoolio.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SatResponseModel (
    @SerializedName("dbn")
    @Expose
    var dbn: String? = null,

    @SerializedName("school_name")
    @Expose
    var schoolName: String? = null,

    @SerializedName("num_of_sat_test_takers")
    @Expose
    var numOfSatTestTakers: String? = null,

    @SerializedName("sat_critical_reading_avg_score")
    @Expose
    var satCriticalReadingAvgScore: String? = null,

    @SerializedName("sat_math_avg_score")
    @Expose
    var satMathAvgScore: String? = null,

    @SerializedName("sat_writing_avg_score")
    @Expose
    var satWritingAvgScore: String? = null
)