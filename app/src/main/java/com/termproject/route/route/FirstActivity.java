package com.termproject.route.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class FirstActivity extends AppCompatActivity {

    ImageView imageView1;
    Animation fadeInAnimation,fadeOutAnimation;

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
            Intent intent =new Intent (getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}
