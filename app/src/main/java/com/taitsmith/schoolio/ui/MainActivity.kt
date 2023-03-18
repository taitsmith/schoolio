package com.taitsmith.schoolio.ui

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.taitsmith.schoolio.R
import com.taitsmith.schoolio.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    private var fragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        setContentView(R.layout.activity_main)
        fragmentManager = supportFragmentManager
        if (savedInstanceState == null) {
            val fragment = MainFragment()
            fragmentManager!!.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
        setObservers()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (fragmentManager!!.backStackEntryCount > 0) fragmentManager!!.popBackStack()
        else super.onBackPressed()
    }

    private fun setObservers() {
        viewModel.satData.observe(this) {
            val fragment = SchoolInfoFragment(viewModel.selectedSchool, it)
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, fragment)
                ?.addToBackStack(null)
                ?.commit()
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