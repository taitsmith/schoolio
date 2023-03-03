package com.taitsmith.schoolio.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.taitsmith.schoolio.R;
import com.taitsmith.schoolio.viewmodels.MainViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private FragmentManager fragmentManager;

    //api key for accessing data - would normally hide this in gradle.properties and then access
    //through BuildConfig.appToken or something similar, but to keep things simple we'll leave it here
    public static String appToken = "m0BLHEdVKLT5GZWpnSVpPvADb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        if (savedInstanceState == null) {
            MainFragment fragment = new MainFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        setObservers();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) fragmentManager.popBackStack();
        else super.onBackPressed();
    }

    private void setObservers() {
        viewModel.getSatData().observe(this, satResponseModel -> {
            SchoolInfoFragment fragment = new SchoolInfoFragment(viewModel.selectedSchool, satResponseModel);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        viewModel.getErrorMessage().observe(this, s -> {
            switch (s) {
                case "OOB":
                    showSnackbar(getString(R.string.error_no_data), false);
                    break;
                case "TIMEOUT":
                    showSnackbar(getString(R.string.error_timeout), true);
                    break;
                default:
                    //we'll allow a refresh option on the generic error just in case
                    showSnackbar(getString(R.string.error_generic), false);
                    break;
            }
        });
    }

    //let the user know when there's been an error of some kind
    private void showSnackbar(String s, Boolean allowRefresh) {
        FrameLayout layout = findViewById(R.id.container);
        Snackbar snackbar = Snackbar.make(layout, s, Snackbar.LENGTH_SHORT);
        if (allowRefresh) {
            snackbar.setAction("Action", view -> viewModel.getSchoolList());
        }
        snackbar.show();
    }

}