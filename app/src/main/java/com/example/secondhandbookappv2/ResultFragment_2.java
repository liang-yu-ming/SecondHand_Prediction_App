package com.example.secondhandbookappv2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ResultFragment_2 extends Fragment {

    private static final String TAG = ResultFragment_2.class.getSimpleName();
    public static ResultFragment_2 instance;
    private ImageView[] image = new ImageView[6];
    private ImageView bigImageView;
    private String time;
    private String JSONString;
    private JSONObject object;

    public ResultFragment_2() {
        // Required empty public constructor
    }

    public static ResultFragment_2 getInstance(){
        if (instance == null)
            instance = new ResultFragment_2();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        time = getArguments().getString("time");
        return inflater.inflate(R.layout.fragment_result_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView();
        JSONString = readJSONFile();
        try {
            object = new JSONObject(JSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getValue();
    }

    private void findView() {
        bigImageView = getView().findViewById(R.id.bigImageView);
        bigImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bigImageView.setVisibility(View.GONE);
            }
        });
        int[] imageID = new int[]{ R.id.result2_image1, R.id.result2_image2, R.id.result2_image3,
                                   R.id.result2_image4, R.id.result2_image5, R.id.result2_image6};
        for (int i = 0; i < 6; i++){
            image[i] = getView().findViewById(imageID[i]);
        }

        Button btn_toHome = getView().findViewById(R.id.btn_toHome2);
        btn_toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("time", time);
                startActivity(intent);
            }
        });
        Button btn_showDetected= getView().findViewById(R.id.btn_showDetected);
        btn_showDetected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 6; i++){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(object.getString("detectedImagePath_" + i));
                        image[i].setImageBitmap(bitmap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Button btn_showOrigin = getView().findViewById(R.id.btn_showOrigin);
        btn_showOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 6; i++){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(object.getString("resizeImagePath_" + i));
                        image[i].setImageBitmap(bitmap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String readJSONFile() {
        String path = getContext().getFilesDir() + "/history/" + time + "/record.json";
        String fileAsString = null;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            fileAsString = sb.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return fileAsString;
    }

    private void getValue() {
        try {
            for (int i = 0; i < 6; i++){
                Bitmap bitmap = BitmapFactory.decodeFile(object.getString("detectedImagePath_" + i));
                image[i].setImageBitmap(bitmap);
                image[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bigImageView.setVisibility(View.VISIBLE);
                        bigImageView.setImageBitmap(bitmap);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}