package com.parkking491prototype.parkking;

import java.util.ArrayList;

public class ParkingStatus {
    private ArrayList<StatusDot> statusDotList;
    private int numOfOpenSpots;
    public ParkingStatus(){
        statusDotList= new ArrayList<StatusDot>();
        setTestDotCoords();
    }

    private void setTestDotCoords(){
        for(int i = 1; i<21; i++){
            StatusDot sd;
            if (i<6) {
                sd = new StatusDot(250, 100 + i * 140 );
                statusDotList.add(sd);
            }else if (i>=6 && i<11){
                sd = new StatusDot(380, 100 + (i-5) * 140 );
                statusDotList.add(sd);
            }else if (i>=11 && i<16){
                sd = new StatusDot(650, 100 + (i-10) * 140 );
                statusDotList.add(sd);
            }else{
                sd = new StatusDot(780, 100 + (i-15) * 140 );
                statusDotList.add(sd);
            }
            int min = 0;
            int max = 1;
            sd.setStatus((min + (int)(Math.random() * ((max - min) + 1)))==1);
        }
    }

    public ArrayList<StatusDot> getStatusDotList() {
        return statusDotList;
    }


    public void updateStatus(){
        numOfOpenSpots = 0;
        for(int i = 0; i<statusDotList.size(); i++){
            statusDotList.get(i).setStatus((0 + (int)(Math.random() * ((1 - 0) + 1)))==1);
            if (statusDotList.get(i).getStatus()==true){
                numOfOpenSpots++;
            }
        }

    }

    public int getNumOfOpenSpots(){
        return numOfOpenSpots;
    }




}

