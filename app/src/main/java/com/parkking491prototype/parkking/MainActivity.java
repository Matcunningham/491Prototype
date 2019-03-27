package com.parkking491prototype.parkking;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.net.Uri;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
<<<<<<< HEAD
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DownloadCallback<String> {
    private static final String BASE_URL = "http://54.186.186.248:3000/";
    private static final String OVERLAY_URL = BASE_URL + "api/overlayimage?";

    private NetworkFragment netFrag;
    private boolean downloading = false;

    private StatusDrawableView cdv;


    private Button getImage;
    protected PinchZoomPan pinchZoomPan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        netFrag = NetworkFragment.getInstance(getFragmentManager(), OVERLAY_URL + "parkinglot_ID=491");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownload();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);





        final ParkingStatus parkingStatus = new ParkingStatus();
        pinchZoomPan = (PinchZoomPan) findViewById(R.id.ivImage);
        pinchZoomPan.setParkingStatus(parkingStatus);
        pinchZoomPan.loadImageOnCanvas();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!this.isInterrupted()) {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pinchZoomPan = (PinchZoomPan) findViewById(R.id.ivImage);
                                parkingStatus.updateStatus();
                                pinchZoomPan.invalidate();

                                TextView parkingStatusTextView = (TextView) findViewById(R.id.parkingStatusTextView);
                                parkingStatusTextView.setText("Open Spots:" + parkingStatus.getNumOfOpenSpots());
                                parkingStatusTextView.invalidate();
                                System.out.println("Refreshed!");
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();

    }


    private void startDownload() {
        if (!downloading && netFrag != null) {
            // Execute the async download.
            netFrag.startDownload();
            downloading = true;
        }
    }

    @Override
    public void updateFromDownload(String result) {
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        try {
            JSONArray jArray = new JSONArray(result);
            JSONObject jObject = jArray.getJSONObject(0);
            String data = jObject.getString("data");
            System.out.println("data: " + data);
            Drawable d = decodeToImage(data);
            cdv = new StatusDrawableView(this);
            cdv.setParkingLotImageDrawable(d);
            cdv.setId(R.id.statusDrawableView);
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.contentLayout);
            layout.addView(cdv);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch (progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                break;
            case Progress.CONNECT_SUCCESS:
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                break;
        }
    }

    @Override
    public void finishDownloading() {
        downloading = false;
        if (netFrag != null) {
            netFrag.cancelDownload();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static Drawable decodeToImage(String imageString) {

        Drawable image = null;
        byte[] imageByte;
        try {
            imageByte = Base64.decode(imageString, Base64.DEFAULT);
            InputStream bis = new ByteArrayInputStream(imageByte);
            image = Drawable.createFromStream(bis, "MapOverlay");
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;

    }
}
