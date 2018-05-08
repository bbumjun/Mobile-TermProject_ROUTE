package com.android.route.route;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;


public class SettingFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_setting,container,false);


        NumberPicker picker1 = (NumberPicker)rootView.findViewById(R.id.picker1);
        picker1.setMinValue(0);
        picker1.setMaxValue(50);
        picker1.setWrapSelectorWheel(false);
        return rootView;
    }

}
