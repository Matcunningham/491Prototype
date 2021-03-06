package com.parkking491prototype.parkking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.parkking491prototype.parkking.QueryType.OVERLAYCOORDS;
import static com.parkking491prototype.parkking.QueryType.OVERLAYMAP;
import static com.parkking491prototype.parkking.QueryType.STATUS;


public class LotData extends Fragment implements DownloadCallback<String> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    /*
    NEW STUFF
     */
    private static final String BASE_URL = "http://54.186.186.248:3000/";
    private static final String OVERLAY_URL = BASE_URL + "api/overlayimage?parkinglot_ID=";
    private static final String OVERLAY_COORDS_URL = BASE_URL + "api/overlaycoordinates?parkinglot_ID=";
    private static final String STATUS_URL = BASE_URL + "api/status/";

    DataCommunication mCallBack;

    //test purposes
    private static String CAMERA_NAME = "";

    private NetworkFragment netFrag = null;
    private NetworkFragment netFrag2 = null;
    private NetworkFragment netFragStatus = null;
    private boolean downloadingOverlayCoord = false;
    private boolean downloadingOverlayMap = false;
    private boolean downloadingStatus = false;


    private Button getImage;
    protected PinchZoomPan pinchZoomPan;
    private final ParkingStatus parkingStatus = new ParkingStatus();
    private boolean loadParkingDataSuccessful=true;
    private  TextView parkingStatusTextView;

    private ProgressBar progressBar;




    public LotData() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LotData.
     */
    // TODO: Rename and change types and number of parameters
    public static LotData newInstance(String param1, String param2) {
        LotData fragment = new LotData();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //NEW

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_lot_data, container, false);
//        // Inflate the layout for this fragment
//        View v = inflater.inflate(R.layout.fragment_lot_data, container, false);
//        ImageView lot = (ImageView) v.findViewById(R.id.lotMapImage);
//
//        Picasso.get()
//                .load("https://daf.csulb.edu/maps/parking/images/campus_map_dec-2018.png")
//                .error(R.drawable.ic_launcher_foreground)
//                .into(lot);

//        return v;


        String selectedLot = mCallBack.getSelectedLot();
        switch (selectedLot) {
            case "g13":
                CAMERA_NAME = "G13";
                break;
            case "g14":
                CAMERA_NAME = "G14";
                break;
            case "lot 13":
                CAMERA_NAME = "lot13";
                break;
            case "model":
                CAMERA_NAME = "model";
                break;
            default:
                CAMERA_NAME = "model";
                break;
        }

        progressBar = v.findViewById(R.id.progressBar2);


        netFragStatus = NetworkFragment.getInstance(getFragmentManager(), "StatusTag", this);
        netFrag = NetworkFragment.getInstance(getFragmentManager(), "MainFragment", this);
        netFrag2 = NetworkFragment.getInstance(getFragmentManager(), "MainFragment2", this);

        pinchZoomPan = (PinchZoomPan) v.findViewById(R.id.ivImage);
        pinchZoomPan.setParkingStatus(parkingStatus);


        //grab overlay and update
        netFrag.setUrlStringAndQueryType(OVERLAYMAP ,OVERLAY_URL + CAMERA_NAME);
        startOverlayMapDownload();

        //grab coords and update
        netFrag2.setUrlStringAndQueryType(OVERLAYCOORDS ,OVERLAY_COORDS_URL + CAMERA_NAME);
        startOverlayCoordDownload();


        //grab status
        netFragStatus.setUrlStringAndQueryType(STATUS, STATUS_URL + CAMERA_NAME);
        startFragDownload();
        pinchZoomPan = (PinchZoomPan) v.findViewById(R.id.ivImage);
        pinchZoomPan.invalidate();
        parkingStatusTextView = (TextView) v.findViewById(R.id.parkingStatusTextView);
        parkingStatusTextView.setText("Open Spots: " + parkingStatus.getNumOfOpenSpots());
        parkingStatusTextView.invalidate();



        //keep refreshing status every x seconds
        final Handler refreshHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {//please dont cringe at this
                    // do updates for imageview
                    if(loadParkingDataSuccessful) {
                        netFragStatus.setUrlStringAndQueryType(STATUS, STATUS_URL + CAMERA_NAME);
                        startFragDownload();
                        pinchZoomPan = (PinchZoomPan) v.findViewById(R.id.ivImage);
                        pinchZoomPan.invalidate();
                        TextView parkingStatusTextView = (TextView) v.findViewById(R.id.parkingStatusTextView);
                        parkingStatusTextView.setText("Open Spots:" + parkingStatus.getNumOfOpenSpots());
                        parkingStatusTextView.invalidate();
                        System.out.println("Refreshed!");
                        refreshHandler.postDelayed(this, 5 * 1000);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        refreshHandler.postDelayed(runnable, 1 * 100);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBack = (DataCommunication) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DataCommunication");
        }
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    //NEW
    private void startOverlayMapDownload() {
        if (!downloadingOverlayMap && netFrag != null) {

            // Execute the async download.
            netFrag.startDownload();
            downloadingOverlayMap = true;
        }
    }

    private void startOverlayCoordDownload() {
        if (!downloadingOverlayCoord && netFrag2 != null) {

            // Execute the async download.
            netFrag2.startDownload();
            downloadingOverlayCoord = true;
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
        try {
            if(qType != null && result != null) {
                if (qType == STATUS) {

                    JSONObject jObject1 = new JSONObject(result);
                    String statusString = jObject1.getString("status");
                    JSONArray statusArray = new JSONArray(statusString);
                    parkingStatus.updateStatus(statusArray);
                } else {
                    switch (qType) {
                        case OVERLAYMAP:
                            JSONArray jArray = new JSONArray(result);
                            JSONObject jObject = jArray.getJSONObject(0);
                            String data = jObject.getString("data");
                            System.out.println("Overlaymap data: " + data);

                            Bitmap b = decodeToImage(data);
                            pinchZoomPan.loadImageOnCanvas(b);
                            break;

                        case OVERLAYCOORDS:
                            JSONArray jArray2 = new JSONArray(result);
                            //
                            JSONObject jObject2= jArray2.getJSONObject(0);
                            String data2 = jObject2.getString("data");
                            System.out.println("Coord data: " + data2);
                            JSONArray dataArray = new JSONArray(data2);
                            parkingStatus.setDots(dataArray);
                            progressBar.setVisibility(View.INVISIBLE);
                            pinchZoomPan.invalidate();
                            break;
                    }

                }
            }


        } catch (JSONException e) {
            //if fails to read json, display error.
            loadParkingDataSuccessful = false;
            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            parkingStatusTextView.setText("Unable to load data.");
            progressBar.setVisibility(View.INVISIBLE);
            parkingStatusTextView.invalidate();


        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch (progressCode) {
            // You can add UI behavior for progress updates here.
            case DownloadCallback.Progress.ERROR:
                break;
            case DownloadCallback.Progress.CONNECT_SUCCESS:
                break;
            case DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS:
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS:
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
        else if(qtype == OVERLAYMAP){
            downloadingOverlayMap = false;
            if (netFrag != null) {
                netFrag.cancelDownload();
            }
        }
        else if(qtype == OVERLAYCOORDS) {
            downloadingOverlayCoord = false;
            if (netFrag2 != null) {
                netFrag2.cancelDownload();
            }
        }
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
