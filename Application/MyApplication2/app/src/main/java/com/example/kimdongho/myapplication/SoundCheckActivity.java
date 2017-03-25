package com.example.kimdongho.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class SoundCheckActivity extends Activity {
    private SpeechRecognizer mRecognizer;
    private Intent intent, resultIntent;
    private EditText editText;
    private ProgressBar progressBar;
    private long speechRecognizerStartTime;
    private TimeOutControl timeOutControl;
    private MediaPlayer mediaPlayer;
    private boolean isRecognition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_check);
        editText = (EditText)findViewById(R.id.editText1);
        editText.setFocusableInTouchMode(false);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.GONE);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.applesound);
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        intent = new Intent(RecognizerIntent.EXTRA_PREFER_OFFLINE);
        intent.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH, true);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mRecognizer.setRecognitionListener(listener);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mRecognizer.startListening(intent);
                speechRecognizerStartTime = SystemClock.currentThreadTimeMillis();
                timeOutControl = new TimeOutControl();
                timeOutControl.start();
            }
        });
    }

    private RecognitionListener listener = new RecognitionListener() {

        @Override
        public void onRmsChanged(float rmsdB) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onResults(Bundle results) {

        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            // TODO Auto-generated method stub
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // TODO Auto-generated method stub
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = partialResults.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            editText.setText(""+rs[0]);
            Log.d("speech_check", "partial result call ok");
            if (rs[0].length() < 2) {
                Log.d("speech_check", "length of result is " + String.valueOf(rs[0].length()));
                return;
            }
            progressBar.setVisibility(View.GONE);
            if(rs[0].subSequence(0, 2).equals("사과")){//?뚯꽦?몄떇 泥댄겕遺遺?
                Log.d("speech_check", "success in partial result");
                Log.d("activity_change", "Sound check to warning put TRUE");
                setTextViewColorPartial(editText, rs[0], rs[0].substring(0, 2), 0xffff7011);
                setResultIntentInfo(true);
            } else {
                Log.d("speech_check", "fail in partial result");
                Log.d("activity_change", "Sound check to warning put FALSE");
                setResultIntentInfo(false);
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(int error) {

            // TODO Auto-generated method stub
            Log.d("activity_change", "Sound check to warning put ERROR/FALSE" + " error number : " + String.valueOf(error));
            long duration = SystemClock.currentThreadTimeMillis() - speechRecognizerStartTime;
            if (duration < 500 && (error == 7 )) {
                mRecognizer.startListening(intent);
                return;
            }
            resultIntent = new Intent();
            resultIntent.putExtra("checkSound", false);
            setResult(RESULT_OK, resultIntent);
            //SystemClock.sleep(1000);
            Log.d("speech_error", "onError");
            finish();
        }

        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub
            //   dialog.dismiss();
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub

        }
    };

    @Override
    protected void onDestroy() {
        if (mRecognizer != null) {
            try {
                mRecognizer.destroy();

            } catch (Exception e) {
                Log.e("mRecognizer_Destroy", "Exception:" + e.toString());
            }
        }
        super.onDestroy();
    }
    private void setResultIntentInfo(boolean pass) {
        resultIntent = new Intent();
        resultIntent.putExtra("checkSound", pass);
        setResult(RESULT_OK, resultIntent);
        SystemClock.sleep(1000);
        finish();
    }
    public static void setTextViewColorPartial(TextView view, String fullText, String subText, int color) {
        view.setText(fullText, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fullText.indexOf(subText);
        str.setSpan(new ForegroundColorSpan(color), i, i + subText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    private class TimeOutControl extends Thread {
        private long elapsedTime, currentTime;

        public void run() {
            elapsedTime = 0;
            while (elapsedTime < 5) {
                currentTime = SystemClock.currentThreadTimeMillis();
                elapsedTime = (currentTime - speechRecognizerStartTime) / 1000;
            }
            setResultIntentInfo(false);
        }
    }
}
