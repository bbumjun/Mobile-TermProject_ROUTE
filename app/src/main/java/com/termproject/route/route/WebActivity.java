package com.termproject.route.route;



import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

    public class WebActivity extends AppCompatActivity {
        WebView browser;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_web);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.webdust_layout);

            browser = (WebView) findViewById(R.id.webkit);

            browser.getSettings().setJavaScriptEnabled(true); // allow scripts
            browser.setWebViewClient(new WebViewClient() );   // page navigation

            browser.loadUrl("http://www.airkorea.or.kr/index");


        }
    }
