package com.taitsmith.schoolio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.taitsmith.schoolio.api.ApiRepository
import com.taitsmith.schoolio.data.SatResponseModel
import com.taitsmith.schoolio.data.SchoolResponseModel
import com.taitsmith.schoolio.viewmodels.MainViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    private lateinit var apiRepository: ApiRepository

    private lateinit var viewModel: MainViewModel

    private val testDispatcher = TestCoroutineDispatcher()
    private val school1 =  SchoolResponseModel()

    private val mockedSchools = mutableListOf<SchoolResponseModel>()

    @Before
    fun setup() {
        apiRepository = mock()

        school1.dbn = "testSchool"
        school1.schoolName = "cool school"
        school1.borough = "queens"
        school1.overviewParagraph = "stay in school"

        mockedSchools.add(0, school1)

        viewModel = MainViewModel(apiRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test schools in ViewModel`() = testDispatcher.runBlockingTest {
        `when`(apiRepository.fetchSchools()).thenReturn(mockedSchools)

        viewModel.getSchoolList()

        verify(apiRepository, times(1)).fetchSchools()

        assertEquals(mockedSchools, viewModel.schools.value)
    }
}