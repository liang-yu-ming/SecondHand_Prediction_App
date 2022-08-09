package com.example.secondhandbookappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = UploadActivity.class.getSimpleName();
    private ImageProcessor imageProcessor;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject object = generateJSONObject(intent);
            writeJSon(object);
            Intent toResultPage = new Intent(UploadActivity.this, ResultActivity.class);
            toResultPage.putExtra("time", imageProcessor.getTime());
            startActivity(toResultPage);
        }
    };

    private void writeJSon(JSONObject object) {
        File file = new File(this.getFilesDir() + "/history/" + imageProcessor.getTime() + "/record.json");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            fOut.write(object.toString().getBytes(StandardCharsets.UTF_8));
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject generateJSONObject(Intent intent) {
        String[] resizeImagePath = imageProcessor.getResizeImagePath();
        String[] detectedImagePath = intent.getStringArrayExtra("detectedImagePath");
        SharedPreferences preferences = getSharedPreferences("currentData", Context.MODE_PRIVATE);
        JSONObject object = new JSONObject();
        try {
            object.put("bookName", preferences.getString("bookName", ""));
            object.put("bookDate", preferences.getString("bookDate", ""));
            object.put("bookCategory", preferences.getString("bookCategory", ""));
            object.put("bookPrice", preferences.getString("bookPrice", ""));
            for (int i = 0; i < 6; i++){
                object.put("resizeImagePath_" + i, resizeImagePath[i]);
                object.put("detectedImagePath_" + i, detectedImagePath[i]);
            }
            object.put("searchDegree", intent.getLongExtra("searchDegree", 0));
            object.put("yellowSpotAverage", intent.getDoubleExtra("yellowSpotAverage", 0));
            object.put("yellowSpotSD", intent.getDoubleExtra("yellowSpotSD", 0));
            object.put("letterAverage", intent.getDoubleExtra("letterAverage", 0));
            object.put("letterSD", intent.getDoubleExtra("letterSD", 0));
            object.put("discount", intent.getDoubleExtra("discount", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        
        findView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String time = getCurrentTime();
        imageProcessor = new ImageProcessor(this, time);
        SharedPreferences preferences = getSharedPreferences("currentData", Context.MODE_PRIVATE);
        preferences.edit().putString("time", time).commit();
        Intent intent = new Intent(this, TCPIntentService.class);
        intent.putExtra("time", imageProcessor.getTime());
        intent.putExtra("resizeImagePath", imageProcessor.getResizeImagePath());
        startService(intent);

        IntentFilter filter = new IntentFilter(TCPIntentService.ACTION_TCP_DONE);
        registerReceiver(receiver, filter);
    }

    private void findView() {
        ProgressBar progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
    }

    private String getCurrentTime(){
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
        Date currentDate = new Date(System.currentTimeMillis());
        String date = timeFormat.format(currentDate);
        return date;
    }
}