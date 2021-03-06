package com.parkking491prototype.parkking;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecentLots.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecentLots#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentLots extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DataCommunication mCallBack;

    private OnFragmentInteractionListener mListener;

    public RecentLots() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentLots.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentLots newInstance(String param1, String param2) {
        RecentLots fragment = new RecentLots();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recent_lots, container, false);
        Button lot13 = (Button) v.findViewById(R.id.recentLotButtonL13);
        Button g13 = (Button) v.findViewById(R.id.recentLotButtonG13);
        Button g14 = (Button) v.findViewById(R.id.recentLotButtonG14);
        Button model = (Button) v.findViewById(R.id.listLotsButtonModel);


        String selectedLot = mCallBack.getSelectedLot();
        switch (selectedLot) {
            case "lot 13":
                lot13.setVisibility(View.VISIBLE);
                break;
            case "g13":
                g13.setVisibility(View.VISIBLE);
                break;
            case "g14":
                g14.setVisibility(View.VISIBLE);
                break;
            default:
                System.out.println();
        }


        lot13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.setSelectedLot("lot 13");
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mainContentFrameContainer, new LotData()).commit();
            }
        });

        g13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.setSelectedLot("g13");
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mainContentFrameContainer, new LotData()).commit();
            }
        });

        g14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.setSelectedLot("g14");
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mainContentFrameContainer, new LotData()).commit();
            }
        });

//        model.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCallBack.setSelectedLot("model");
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.mainContentFrameContainer, new LotData()).commit();
//            }
//        });

        return v;
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
}
