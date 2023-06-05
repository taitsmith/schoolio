package com.taitsmith.schoolio.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taitsmith.schoolio.R
import com.taitsmith.schoolio.data.SatResponseModel
import com.taitsmith.schoolio.data.SchoolResponseModel
import com.taitsmith.schoolio.ui.theme.md_theme_light_primary
import com.taitsmith.schoolio.ui.theme.md_theme_light_primaryContainer
import com.taitsmith.schoolio.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ShowLoading(){
    Surface(
        color = md_theme_light_primary,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center),
                color = md_theme_light_primaryContainer
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun MainScreen(schools: List<SchoolResponseModel>) {
    val scope = rememberCoroutineScope()
    val viewModel: MainViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by viewModel.errorMessage.observeAsState(null)
    var snackbarMessage = ""

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) {
        SchoolDataList(schools)

        if (!errorMessage.isNullOrEmpty()) {
            when (errorMessage) {
                "OOB" -> snackbarMessage = "no sat data for this school"
                "TIMEOUT" -> snackbarMessage = "network error"
                "UNKNOWN" -> snackbarMessage = "something went wrong"
            }
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = snackbarMessage,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Long
                )

                when (result) {
                    SnackbarResult.ActionPerformed -> viewModel.resetErrorMessage()
                    SnackbarResult.Dismissed -> viewModel.resetErrorMessage()
                }
            }
        }
    }
}

@Composable
fun SchoolDataList(schools: List<SchoolResponseModel>) {
    Surface(
        color = md_theme_light_primary
    ) {
        LazyColumn {
            items(
                items = schools,
                key = {
                    school ->
                    school.dbn
                }
            ) { school ->
                SchoolCard(school)
            }
        }
    }
}

@Composable
fun SchoolCard(school: SchoolResponseModel) {
    val viewModel: MainViewModel = viewModel()
    val satData by viewModel.satData.observeAsState(null)

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = md_theme_light_primaryContainer,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 4.dp)
    ) {
        var expanded by remember { mutableStateOf(false) }

        val context = LocalContext.current
        Column(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxSize()
                .clickable {
                    expanded = !expanded
                    if (expanded) viewModel.getSatScores(school)
                }
        ) {
            Text(
                text = school.schoolName!!,
                color = md_theme_light_primary,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = school.borough!!,
                color = md_theme_light_primary,
                style = MaterialTheme.typography.bodySmall
            )
            AnimatedVisibility(expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 4.dp)
                ) {
                    Text(
                        text = school.overviewParagraph ?: "Looks like there\'s no info for ${school.schoolName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = md_theme_light_primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 4.dp)
                                .clickable {
                                    context.startActivity(
                                        Intent(
                                            Intent.ACTION_DIAL,
                                            Uri.parse("tel:${school.phoneNumber}")
                                        )
                                    )
                                }
                        ) {
                            Image(
                                painter = painterResource(R.drawable.baseline_local_phone_24),
                                contentDescription = "Phone image",
                                modifier = Modifier
                                    .padding(4.dp),
                                colorFilter = ColorFilter.tint(md_theme_light_primary)
                            )
                            Text(
                                text = school.phoneNumber ?: "No phone number available",
                                style = MaterialTheme.typography.bodySmall,
                                color = md_theme_light_primary,
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .align(Alignment.CenterVertically)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 4.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_SENDTO)
                                    intent.data = Uri.parse("mailto:${school.schoolEmail}")
                                    context.startActivity(intent)
                                },
                        ) {
                            Image(
                                painter = painterResource(R.drawable.baseline_email_24),
                                contentDescription = "email image",
                                modifier = Modifier
                                    .padding(4.dp),
                                colorFilter = ColorFilter.tint(md_theme_light_primary)
                            )
                            Text(
                                text = school.schoolEmail ?: "No email available",
                                style = MaterialTheme.typography.bodySmall,
                                color = md_theme_light_primary,
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(visible = expanded && !satData?.schoolName.isNullOrEmpty()) {
                satData?.let { SatData(satData = it) }
            }
        }
    }
}

@Composable
fun SatData(satData: SatResponseModel) {
    Column {
        Row(
            modifier = Modifier
                .padding(all = 4.dp)
                .fillMaxWidth()
                .wrapContentWidth()
                .align(CenterHorizontally)
            ) {
            Column(
                modifier = Modifier
                    .padding(all = 4.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Text(text = "SAT Writing:",
                    color = md_theme_light_primary,
                    style = MaterialTheme.typography.bodyMedium)
                Text(text = satData.satWritingAvgScore.toString(),
                    color = md_theme_light_primary,
                    style = MaterialTheme.typography.bodySmall)
            }
            Column(
                modifier = Modifier
                    .padding(all = 4.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Text(text = "SAT Reading:",
                    color = md_theme_light_primary,
                    style = MaterialTheme.typography.bodyMedium)
                Text(text = satData.satCriticalReadingAvgScore.toString(),
                    color = md_theme_light_primary,
                    style = MaterialTheme.typography.bodySmall)
            }
            Column(
                modifier = Modifier
                    .padding(all = 4.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Text(text = "SAT Math:",
                    color = md_theme_light_primary,
                    style = MaterialTheme.typography.bodyMedium)
                Text(text = satData.satMathAvgScore.toString(),
                    color = md_theme_light_primary,
                    style = MaterialTheme.typography.bodySmall)
            }
            Column(
                modifier = Modifier
                    .padding(all = 4.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Text(text = "SAT takers:",
                    color = md_theme_light_primary,
                    style = MaterialTheme.typography.bodyMedium)
                Text(text = satData.numOfSatTestTakers.toString(),
                    color = md_theme_light_primary,
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview
@Composable
fun SchoolCardPreview() {
    SchoolCard(SchoolResponseModel("schoolio", schoolName = "cool school", boro = "queens"))
}