package com.termproject.route.route;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

public class FirstActivity extends AppCompatActivity {

    ImageView imageView1;
    Animation fadeInAnimation,fadeOutAnimation;

    private static final int RC_SIGN_IN =123;
// ...

    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

    protected void onCreate(Bundle savedinstanceState) {
        super.onCreate(savedinstanceState);
        setContentView(R.layout.activity_first);

        imageView1 = (ImageView) findViewById(R.id.startImage);

        TextView textView1 = findViewById(R.id.startText);
        fadeInAnimation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
        fadeInAnimation.setAnimationListener(animationFadeInListener);
        fadeOutAnimation.setAnimationListener(animationFadeOutListener);
        imageView1.startAnimation(fadeInAnimation);
        textView1.startAnimation(fadeInAnimation);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_USE_LOGO);


        LottieAnimationView lottie = (LottieAnimationView) findViewById(R.id.lottie);
        lottie.playAnimation();
        lottie.loop(true);

    }

    Animation.AnimationListener animationFadeInListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            imageView1.startAnimation(fadeOutAnimation);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {


        }
    };
    Animation.AnimationListener animationFadeOutListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            imageView1.setVisibility(View.GONE);

            new TedPermission(FirstActivity.this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA,
                            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.INTERNET,
                            android.Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.VIBRATE)
                    .check();

         }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
         //   Toast.makeText(FirstActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
                    RC_SIGN_IN);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(FirstActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }


    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent = new Intent(getApplicationContext(),newRunningActivity.class);
                startActivity(intent);
                finish();
                // ...
            } else {
                // Sign in failed, check response for error code
                // ...
            }


        }
    }
}
