package com.zafaco.DemoApplicationBerec.activities;

/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2019                                                          *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2019-01-03
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.zafaco.DemoApplicationBerec.interfaces.FocusedFragment;
import com.zafaco.common.Tool;
import com.zafaco.DemoApplicationBerec.R;
import com.zafaco.DemoApplicationBerec.WSTool;
import com.zafaco.DemoApplicationBerec.fragments.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class DemoApplication
 */
public class DemoApplication extends AppCompatActivity
{
    /****************************** Objects *******************************/

    private WSTool wsTool = WSTool.getInstance();
    private Tool mTool = wsTool.getToolObject();

    /**************************** Variables ****************************/

    Context ctx;

    //Module Objects
    private String[] mDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerLinearLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    // Identifier for the permission request
    private static final int ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST = 1;

    /*******************************************************************/

    /**
     * Method onCreate
     * @param savedInstanceState Bundle of savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_measuerment);

        this.ctx = this;

        wsTool.setCtx(ctx);

        //Check First Run
        checkFirstRun();

        //Check Permissions
        checkPermission();

        mTitle = mDrawerTitle = getTitle();

        mDrawerTitles = getResources().getStringArray(R.array.nav_drawer);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLinearLayout = findViewById(R.id.left_drawer);
        mDrawerList = findViewById(R.id.list_slidermenu);

        ArrayList<String> navDrawerItems = new ArrayList<>();

        Collections.addAll(navDrawerItems, mDrawerTitles);

        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navDrawerItems));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name)
        {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);


        displayView(0);


    }

    /**
     * Class SlideMenuClickListener
     */
    private class SlideMenuClickListener implements ListView.OnItemClickListener
    {
        /**
         * @param parent
         * @param view
         * @param position
         * @param id
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            displayView(position);
        }
    }

    /**
     * Method dispplayView
     * @param position
     */
    private void displayView(int position)
    {
        FocusedFragment fragment = null;
        String fragmentTag = "";

        switch (position)
        {
            case 0:
                fragment = new SpeedFragment();
                fragmentTag = "SpeedFragment";
                break;
            case 1:
                fragment = new PortBlockingFragment();
                fragmentTag = "PortBlockingFragment";
                break;
            case 2:
                Intent i = new Intent(this, Preferences.class);
                startActivity(i);

                break;

            default:
                break;
        }

        if (fragment != null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, (Fragment)fragment,fragmentTag).addToBackStack(fragmentTag).commit();
            fragmentManager.executePendingTransactions();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerLinearLayout);

            fragment.onDisplayView();
        }
    }

    /**
     * onResume
     */
    @Override
    protected void onResume()
    {
        super.onResume();
    }

    /**
     * Method onPause
     */
    @Override
    protected void onPause()
    {
        super.onPause();
    }

    /**
     * @param menu Object
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    /**
     * @param item Object
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method checkFirstRun
     */
    public void checkFirstRun()
    {
        //Get App Version
        wsTool.getAppVersion();
    }

    /**
     * Method checkPersmission
     */
    public void checkPermission()
    {
        if(
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED
        )
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE}, ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST);
        }
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST )
        {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Berechtigungen hinzugefügt", Toast.LENGTH_SHORT).show();
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

