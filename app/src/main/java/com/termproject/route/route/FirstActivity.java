package com.termproject.route.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class FirstActivity extends AppCompatActivity {

    ImageView imageView1;
    Animation fadeInAnimation,fadeOutAnimation;
    private static final int RC_SIGN_IN =123;
    protected void onCreate(Bundle savedinstanceState) {
        super.onCreate(savedinstanceState);
        setContentView(R.layout.activity_first);

        imageView1 = (ImageView) findViewById(R.id.startImage);

        fadeInAnimation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
        fadeInAnimation.setAnimationListener(animationFadeInListener);
        fadeOutAnimation.setAnimationListener(animationFadeOutListener);
        imageView1.startAnimation(fadeInAnimation);

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

            List<AuthUI.IdpConfig>providers = Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),RC_SIGN_IN);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==RC_SIGN_IN) {
            IdpResponse response =IdpResponse.fromResultIntent(data);
            if(resultCode==RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(getApplication(),"Login success",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            } else {

            }
        }
    }
}
