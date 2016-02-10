package com.example.jimmy.navigationdrawer.Activity;

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

import com.example.jimmy.navigationdrawer.AppConfig;
import com.example.jimmy.navigationdrawer.Authorisation;
import com.example.jimmy.navigationdrawer.Fragment.fragExport;
import com.example.jimmy.navigationdrawer.Fragment.fragPaid;
import com.example.jimmy.navigationdrawer.Fragment.fragBudget;
import com.example.jimmy.navigationdrawer.Fragment.fragIncome;
import com.example.jimmy.navigationdrawer.Fragment.fragOverdue;
import com.example.jimmy.navigationdrawer.Fragment.fragPayable;
import com.example.jimmy.navigationdrawer.Fragment.fragSummary;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.Fragment.fragShare;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean backKeyPressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //SharedPreferences sharedPreferences = getSharedPreferences(SHRED_PREF,)
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        if (intent.hasExtra(AppConfig.FRAGMENT)) {
            Bundle bundle = getIntent().getExtras();
            if (!bundle.getString(AppConfig.FRAGMENT).equals(null)) {
                Fragment fragment = new fragPayable();
                setFragment(fragment);
            }
        } else {
            //Welcome page
            Fragment fragment;
            fragment = new fragSummary();
            setFragment(fragment);
        }
        //onbackpressed Make sure user wants to exit
        backKeyPressed = false;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!backKeyPressed) {
                Toast.makeText(MainActivity.this, "Press back again to leave", Toast.LENGTH_SHORT).show();
                backKeyPressed = true;
            }else
                super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String mTitle = null;
        Fragment fragment;
        int id = item.getItemId();
        backKeyPressed = false;

        //Paid fragment
        if (id == R.id.nav_paid) {
            mTitle = getString(R.string.paid);
            fragment = new fragPaid();
            setFragment(fragment);
        } else if (id == R.id.nav_payable) {
            //Payable fragment
            mTitle = getString(R.string.payable);
            fragment = new fragPayable();
            setFragment(fragment);
        } else if (id == R.id.nav_overdue) {
            //Overdue fragment
            mTitle = getString(R.string.overdue);
            fragment = new fragOverdue();
            setFragment(fragment);
        } else if (id == R.id.nav_income) {
            //Income fragment
            mTitle = getString(R.string.income);
            fragment = new fragIncome();
            setFragment(fragment);
        } else if (id == R.id.nav_budget) {
            //Budget fragment
            mTitle = getString(R.string.budget);
            fragment = new fragBudget();
            setFragment(fragment);
        } else if (id == R.id.nav_sumarry) {
            //Summary fragment
            mTitle = getString(R.string.summary);
            fragment = new fragSummary();
            setFragment(fragment);
        } else if (id == R.id.nav_export) {
            //fragExport fragment
            mTitle = getString(R.string.export);
            fragment = new fragExport();
            setFragment(fragment);
        } else if (id == R.id.nav_share) {
            //Share fragment
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
