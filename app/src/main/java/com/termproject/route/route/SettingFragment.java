package com.termproject.route.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;


public class SettingFragment extends Fragment {
    private static final int RC_SIGN_IN =123;
Button logoutBtn;
Button deleteAccountBtn;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_setting,container,false);

        logoutBtn =(Button)rootView.findViewById(R.id.logoutBtn);
        deleteAccountBtn = (Button)rootView.findViewById(R.id.deleteAccountBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                AuthUI.getInstance().signOut(getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(),"Logout success",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                    }
                });
                List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),RC_SIGN_IN);

            }

        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                AuthUI.getInstance()
                        .delete(getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(),"Delete account success",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(),MainActivity.class);
                            }
                        });
                List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),RC_SIGN_IN);

            }
        });
        NumberPicker picker1 = (NumberPicker)rootView.findViewById(R.id.picker1);
        picker1.setMinValue(0);
        picker1.setMaxValue(50);
        picker1.setWrapSelectorWheel(false);
        return rootView;
    }

}
