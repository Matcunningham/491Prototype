package com.parkking491prototype.parkking;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.parkking491prototype.parkking.QueryType.*;

public class ParkingStatus {
    private ArrayList<StatusDot> statusDotList;
    private int numOfOpenSpots;
    public ParkingStatus(){
        statusDotList= new ArrayList<StatusDot>();
        setTestDotCoords();
    }

    private void setTestDotCoords(){
//        for(int i = 1; i<8; i++){
//            StatusDot sd;
//            if (i<6) {
//                sd = new StatusDot(160, 60 + i * 50 );
//                statusDotList.add(sd);
//            }else if (i>=6 && i<11){
//                sd = new StatusDot(240, 60 + (i-5) * 50 );
//                statusDotList.add(sd);
//            }else if (i>=11 && i<16){
//                sd = new StatusDot(650, 100 + (i-10) * 80 );
//                statusDotList.add(sd);
//            }else{
//                sd = new StatusDot(780, 100 + (i-15) * 80 );
//                statusDotList.add(sd);
//            }
//            int min = 0;
//            int max = 1;
////            sd.setStatus((min + (int)(Math.random() * ((max - min) + 1)))==1);
//            sd.setStatus(false);
//        }
        statusDotList.add(new StatusDot(169, 146 ));
        statusDotList.add(new StatusDot(169, 236 ));
    }

    public ArrayList<StatusDot> getStatusDotList() {
        return statusDotList;
    }


//      //TEST FUNCTION
//    public void updateStatus(){
//        numOfOpenSpots = 0;
//        for(int i = 0; i<statusDotList.size(); i++){
//            statusDotList.get(i).setStatus((0 + (int)(Math.random() * ((1 - 0) + 1)))==1);
//            if (statusDotList.get(i).getStatus()==true){
//                numOfOpenSpots++;
//            }
//        }
//
//    }

    public void updateStatus(JSONArray statusArray){
        numOfOpenSpots = 0;
        final int TEST_LIMIT = 7;

        try {
            for (int i = 0; i < TEST_LIMIT; i++) {
                Double confidence = statusArray.getJSONObject(i).getDouble("confidence");
                System.out.println(confidence);
                statusDotList.get(i).setStatus(confidence>0.5);
                if (statusDotList.get(i).getStatus() == true) {
                    numOfOpenSpots++;
                }
            }
        } catch (
        JSONException e) {
            e.printStackTrace();
        }


    }

    public int getNumOfOpenSpots(){
        return numOfOpenSpots;
    }

}

