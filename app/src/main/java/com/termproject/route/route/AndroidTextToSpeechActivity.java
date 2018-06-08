package com.termproject.route.route;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.Locale;


    public abstract class AndroidTextToSpeechActivity extends Activity implements TextToSpeech.OnInitListener {
        /*





        private TextToSpeech tts;
        private Button btnSpeak;
        private EditText txtText;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_running);

            tts = new TextToSpeech(this, this);

            btnSpeak = (Button) findViewById(R.id.btnSpeak);

            txtText = (EditText) findViewById(R.id.timeText);

            // button on click event
            btnSpeak.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    speakOut();
                }

            });
        }

        @Override
        public void onDestroy() {
            // Don't forget to shutdown tts!
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
            super.onDestroy();
        }

        @Override
        public void onInit(int status) {

            if (status == TextToSpeech.SUCCESS) {

                int result = tts.setLanguage(Locale.KOREA);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {
                    btnSpeak.setEnabled(true);
                    speakOut();
                }

            } else {
                Log.e("TTS", "Initilization Failed!");
            }

        }

        private void speakOut() {

            String text = txtText.getText().toString();

            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        }


        */
    }



