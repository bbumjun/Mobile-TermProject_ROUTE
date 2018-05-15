package com.termproject.route.route;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RunningFragment extends Fragment {
Button mapBtn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_running,container,false);

        mapBtn = rootView.findViewById(R.id.mapButton);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(savedInstanceState==null) {

                    MapFragment1 mapFragment1 = new MapFragment1();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mapLayout, mapFragment1, "map").commit();
                }

            }
        });


        return rootView;
    }
}
