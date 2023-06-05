package com.taitsmith.schoolio.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.taitsmith.schoolio.R
import com.taitsmith.schoolio.data.SchoolResponseModel
import com.taitsmith.schoolio.ui.theme.md_theme_light_primary
import com.taitsmith.schoolio.ui.theme.md_theme_light_primaryContainer
import com.taitsmith.schoolio.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

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
        viewModel.satData.observe(this) {

        }
        viewModel.schools.observe(this) {
            setContent { 
                SchoolDataList(schools = it)
            }
        }
        viewModel.errorMessage.observe(this) { s: String? ->
            when (s) {
                "OOB" -> showSnackbar(getString(R.string.error_no_data), false)
                "TIMEOUT" -> showSnackbar(getString(R.string.error_timeout), true)
                else ->                     //we'll allow a refresh option on the generic error just in case
                    showSnackbar(getString(R.string.error_generic), false)
            }
        }
    }

    //let the user know when there's been an error of some kind
    private fun showSnackbar(s: String, allowRefresh: Boolean) {
        val layout = findViewById<FrameLayout>(R.id.container)
        val snackbar = Snackbar.make(layout, s, Snackbar.LENGTH_SHORT)
        if (allowRefresh) {
            snackbar.setAction("Action") { viewModel.getSchoolList() }
        }
        snackbar.show()
    }

    companion object {
        //api key for accessing data - would normally hide this in gradle.properties and then access
        //through BuildConfig.appToken or something similar, but to keep things simple we'll leave it here
        var appToken = "m0BLHEdVKLT5GZWpnSVpPvADb"
    }
}

@Composable
fun ShowLoading(){
    Surface(
        shape = MaterialTheme.shapes.medium,
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

@Composable
fun SchoolDataList(schools: List<SchoolResponseModel>) {
    Surface(
        color = md_theme_light_primary
    ) {
        LazyColumn {
            items(schools) { school ->
                SchoolCard(school)
            }
        }
    }
}

@Composable
fun SchoolCard(school: SchoolResponseModel) {
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
                .clickable { expanded = !expanded }
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
                        text = school.overviewParagraph!!,
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
        }
    }
}

@Preview
@Composable
fun SchoolCardPreview() {
    SchoolCard(SchoolResponseModel("schoolio", schoolName = "cool school", boro = "queens"))
}