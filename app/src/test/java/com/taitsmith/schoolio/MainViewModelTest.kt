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
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations.initMocks

@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatchRule = MainDispatchRule()

    @Mock
    private lateinit var apiRepository: ApiRepository

    private lateinit var viewModel: MainViewModel

    private val testDispatcher = TestCoroutineDispatcher()
    private val school1 =  SchoolResponseModel()
    private val scores = SatResponseModel()
    private val mockedSchools = mutableListOf<SchoolResponseModel>()
    private val mockedScores = mutableListOf<SatResponseModel>()

    @Before
    fun setup() {
        initMocks(this)

        school1.dbn = "testSchool"
        school1.schoolName = "cool school"
        school1.borough = "queens"
        school1.overviewParagraph = "stay in school"
        mockedSchools.add(0, school1)

        scores.dbn = "testSchool"
        scores.satWritingAvgScore = "500"
        scores.satCriticalReadingAvgScore = "500"
        scores.satMathAvgScore = "500"
        scores.numOfSatTestTakers = "100"
        mockedScores.add(0, scores)

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
        val returnedSchools = apiRepository.fetchSchools()
        assertEquals(returnedSchools, viewModel.schools.getOrAwaitValue())
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test empty sat scores set error message in ViewModel`() = testDispatcher.runBlockingTest {
        `when`(apiRepository.fetchSatScoresForSchool(school1.dbn)).thenThrow(IndexOutOfBoundsException())
        viewModel.getSatScores(school1)
        assertEquals("OOB", viewModel.errorMessage.getOrAwaitValue())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test sat scores in ViewModel`() = testDispatcher.runBlockingTest {
        `when`(apiRepository.fetchSatScoresForSchool(school1.dbn)).thenReturn(mockedScores)
        viewModel.getSatScores(school1)
        assertEquals(mockedScores[0], viewModel.satData.getOrAwaitValue())
    }
}