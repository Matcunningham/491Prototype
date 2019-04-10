package com.parkking491prototype.parkking;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import static com.parkking491prototype.parkking.QueryType.*;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DownloadCallback<String> {

    private static final String BASE_URL = "http://54.186.186.248:3000/";
    private static final String OVERLAY_URL = BASE_URL + "api/overlayimage?parkinglot_ID=";
    private static final String OVERLAY_COORDS_URL = BASE_URL + "api/overlaycoordinates?parkinglot_ID=";
    private static final String STATUS_URL = BASE_URL + "api/status?parkinglot_ID=";
    private Activity context;


    //test purposes
    private static final String TEST_CAMERA_NAME = "testCamera7";


   private NetworkFragment netFrag = null;
   private NetworkFragment netFragStatus = null;
    private boolean downloading = false;
    private boolean downloadingStatus = false;


    private Button getImage;
    protected PinchZoomPan pinchZoomPan;
    private final ParkingStatus parkingStatus = new ParkingStatus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        netFragStatus = NetworkFragment.getInstance(getFragmentManager(), "StatusTag");
        netFrag = NetworkFragment.getInstance(getFragmentManager(), "MainFragment");
        //NetworkFragment netFrag = null;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                netFrag.setUrlStringAndQueryType(OVERLAYMAP ,OVERLAY_URL + "491");
                startDownload();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                netFrag.setUrlStringAndQueryType(STATUS, STATUS_URL + TEST_CAMERA_NAME);
                //netFrag.setUrlStringAndQueryType(OVERLAYCOORDS,OVERLAY_COORDS_URL + "491");
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






        pinchZoomPan = (PinchZoomPan) findViewById(R.id.ivImage);
        pinchZoomPan.setParkingStatus(parkingStatus);
        pinchZoomPan.loadImageOnCanvas(null);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!this.isInterrupted()) {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                netFragStatus.setUrlStringAndQueryType(STATUS, STATUS_URL + TEST_CAMERA_NAME);
                                //startDownload();
                                startFragDownload();

                                pinchZoomPan = (PinchZoomPan) findViewById(R.id.ivImage);
//                                parkingStatus.updateStatus();
                                pinchZoomPan.invalidate();

                                TextView parkingStatusTextView = (TextView) findViewById(R.id.parkingStatusTextView);
                                parkingStatusTextView.setText("Open Spots:" + parkingStatus.getNumOfOpenSpots());
                                parkingStatusTextView.invalidate();
//                                System.out.println("Refreshed!");
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

    private void startFragDownload()
    {
        if(!downloadingStatus && netFragStatus != null)
        {
            netFragStatus.startDownload();
            downloadingStatus = true;
        }
    }

    @Override
    public void updateFromDownload(QueryType qType, String result) {
        // For testing purposes
        //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        try {
//            JSONArray jArray = new JSONArray(result);
//            JSONObject jObject = jArray.getJSONObject(0);
//            String data = jObject.getString("data");
//            System.out.println("data: " + data);

            if(qType != null && result != null) {
                if (qType == STATUS) {
                    // TODO: integrate data with dots
                    JSONArray jArray1 = new JSONArray(result);
                    JSONObject jObject1 = jArray1.getJSONObject(0);
                    String statusString = jObject1.getString("status");
//                    JSONArray statusObject = jObject1.getJSONArray("status");
                    JSONArray statusArray = new JSONArray(statusString);

//                    System.out.println("TEST: " + statusString);
//                    final int STATUS_INDEX= 2;
//                    JSONArray statusArray = jArray1.getJSONArray(STATUS_INDEX);
                    parkingStatus.updateStatus(statusArray);
//                    System.out.println(statusArray.getJSONObject(0).getString("confidence"));
                } else {
                    switch (qType) {
                        case OVERLAYMAP:
                            JSONArray jArray = new JSONArray(result);
                            JSONObject jObject = jArray.getJSONObject(0);
                            String data = jObject.getString("data");
                            System.out.println("data: " + data);

                            Bitmap b = decodeToImage(data);
                            pinchZoomPan.loadImageOnCanvas(b);
                            break;

                        case OVERLAYCOORDS:
                            //TODO: ?
                            break;
                    }

                }
            }


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
    public void finishDownloading(QueryType qtype) {

        if(qtype == STATUS)
        {
            downloadingStatus = false;
            if(netFragStatus != null)
            {
                netFragStatus.cancelDownload();
            }
        }
        else {
            downloading = false;
            if (netFrag != null) {
                netFrag.cancelDownload();
            }
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


    public static Bitmap decodeToImage(String imageString) {

        Bitmap image = null;
        byte[] imageByte;
        try {
            imageByte = Base64.decode(imageString, Base64.DEFAULT);
            InputStream bis = new ByteArrayInputStream(imageByte);
            image = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;

    }
}
