package com.work.jfidiles.BillKeeper.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.work.jfidiles.BillKeeper.AppConfig;
import com.work.jfidiles.BillKeeper.Fragment.fragExport;
import com.work.jfidiles.BillKeeper.Fragment.fragPaid;
import com.work.jfidiles.BillKeeper.Fragment.fragBudget;
import com.work.jfidiles.BillKeeper.Fragment.fragIncome;
import com.work.jfidiles.BillKeeper.Fragment.fragOverdue;
import com.work.jfidiles.BillKeeper.Fragment.fragPayable;
import com.work.jfidiles.BillKeeper.Fragment.fragSummary;
import com.work.jfidiles.BillKeeper.R;
import com.work.jfidiles.BillKeeper.Fragment.fragShare;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean backKeyPressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();

        // Check if the activity has been called by a notification
        if (intent.hasExtra(AppConfig.FRAGMENT)) {
            Bundle bundle = getIntent().getExtras();
            if (!bundle.getString(AppConfig.FRAGMENT).equals(null)) {
                Fragment fragment = new fragPayable();
                setFragment(fragment);
            }
        } else {
            Fragment fragment;
            fragment = new fragSummary();
            setFragment(fragment);
        }
        // initialise back key
        backKeyPressed = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else {
            if (!backKeyPressed) {
                Toast.makeText(MainActivity.this, AppConfig.PRESS_AGAIN_MESSAGE, Toast.LENGTH_SHORT).show();
                backKeyPressed = true;
            }else
                super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
            return true;

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String mTitle = null;
        Fragment fragment;
        int id = item.getItemId();
        backKeyPressed = false;

        if (id == R.id.nav_paid) {
            mTitle = getString(R.string.paid);
            fragment = new fragPaid();
            setFragment(fragment);
        } else if (id == R.id.nav_payable) {
            mTitle = getString(R.string.payable);
            fragment = new fragPayable();
            setFragment(fragment);
        } else if (id == R.id.nav_overdue) {
            mTitle = getString(R.string.overdue);
            fragment = new fragOverdue();
            setFragment(fragment);
        } else if (id == R.id.nav_income) {
            mTitle = getString(R.string.income);
            fragment = new fragIncome();
            setFragment(fragment);
        } else if (id == R.id.nav_budget) {
            mTitle = getString(R.string.budget);
            fragment = new fragBudget();
            setFragment(fragment);
        } else if (id == R.id.nav_sumarry) {
            mTitle = getString(R.string.summary);
            fragment = new fragSummary();
            setFragment(fragment);
        } else if (id == R.id.nav_export) {
            mTitle = getString(R.string.export);
            fragment = new fragExport();
            setFragment(fragment);
        } else if (id == R.id.nav_share) {
            mTitle = getString(R.string.share);
            fragment = new fragShare();
            setFragment(fragment);
        } else if (id == R.id.nav_new) {
            mTitle = getString(R.string.whatisnew);
        }
        setTitle(mTitle);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        backKeyPressed = false;
    }

    private void setFragment (Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, fragment);
        ft.commit();
    }

    public void setTitle(String mTitle) {
        try {
            getSupportActionBar().setTitle(mTitle);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
