package com.taitsmith.schoolio

import com.taitsmith.schoolio.api.ApiRepository
import com.taitsmith.schoolio.data.SchoolResponseModel
import com.taitsmith.schoolio.viewmodels.MainViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewmodelTest {


    lateinit var mainViewModel: MainViewModel

    val fakeApiResponse = mutableListOf<SchoolResponseModel>()

    lateinit var apiRepository: ApiRepository

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("ui thread")

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        val school = SchoolResponseModel()
        school.schoolName = "cool school"
        school.borough = "queens"
        school.overviewParagraph = "every day is pizza friday"

        fakeApiResponse.add(0, school)
        apiRepository = mock(ApiRepository::class.java)

        mainViewModel = MainViewModel(apiRepository)
    }

    @Test
    fun doesFetchUpdateSchoolLiveData(): Unit = runTest {
        assertTrue(fakeApiResponse[0].borough == "queens")

        coroutineScope {

                launch {
                    apiRepository = mock {
                        on {
                            launch {
                                fetchSchools()
                            }
                        } doReturn fakeApiResponse
                    }
                }


            mainViewModel.getSchoolList()
            assertTrue(mainViewModel.schools.value?.get(0) == fakeApiResponse[0])
        }

    }
}