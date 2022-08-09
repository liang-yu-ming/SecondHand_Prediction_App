package com.example.secondhandbookappv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class ResultFragment_1 extends Fragment {

    private static final String TAG = ResultFragment_1.class.getSimpleName();
    public static ResultFragment_1 instance;
    private TextView bookName;
    private TextView bookCategory;
    private TextView bookDate;
    private TextView searchDegree;
    private TextView yellowSpotAverage;
    private TextView yellowSpotSD;
    private TextView letterAverage;
    private TextView letterSD;
    private TextView discount;
    private TextView bookPrice;
    private Button btn_toHome;
    private String time;

    public ResultFragment_1() {
        // Required empty public constructor
    }

    public static ResultFragment_1 getInstance(){
        if (instance == null)
            instance = new ResultFragment_1();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        time = getArguments().getString("time");
        return inflater.inflate(R.layout.fragment_result_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView();
        // setViewValue();
        String jsonString = readJSONFile();
        Log.d(TAG, "onViewCreated: " + jsonString);
        getValue(jsonString);
    }

    private void getValue(String input) {
        try {
            JSONObject object = new JSONObject(input);
            bookName.setText(object.getString("bookName"));
            bookCategory.setText(object.getString("bookCategory"));
            bookDate.setText(object.getString("bookDate"));
            searchDegree.setText(object.getString("searchDegree"));
            double round = object.getDouble("yellowSpotAverage");
            round = Math.round(round * 100.0) / 100.0;
            yellowSpotAverage.setText((round * 100) + " %");
            round = object.getDouble("yellowSpotSD");
            round = Math.round(round * 100.0) / 100.0;
            yellowSpotSD.setText((round * 100) + " %");
            round = object.getDouble("letterAverage");
            round = Math.round(round * 100.0) / 100.0;
            letterAverage.setText((round * 100) + " %");
            round = object.getDouble("letterSD");
            round = Math.round(round * 100.0) / 100.0;
            letterSD.setText((round * 100) + " %");
            discount.setText(object.getString("discount"));
            String priceString = object.getString("bookPrice");
            if (priceString.equals(""))
                bookPrice.setVisibility(View.GONE);
            else {
                int priceInt = Integer.parseInt(priceString) * Integer.parseInt(object.getString("discount"));
                bookPrice.setText(String.valueOf(priceInt));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            System.out.println(fileAsString);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        Log.d(TAG, "readJSONFile: " + fileAsString);
        return fileAsString;
    }

    private void findView() {
        bookName = getView().findViewById(R.id.tv_resultBookName);
        bookCategory = getView().findViewById(R.id.tv_resultBookCategory);
        bookDate = getView().findViewById(R.id.tv_resultBookDate);
        searchDegree = getView().findViewById(R.id.tv_searchDegree);
        yellowSpotAverage = getView().findViewById(R.id.tv_yellowSpotAverage);
        yellowSpotSD = getView().findViewById(R.id.tv_yellowSpotSD);
        letterAverage = getView().findViewById(R.id.tv_letterAverage);
        letterSD = getView().findViewById(R.id.tv_letterSD);
        discount = getView().findViewById(R.id.tv_discount);
        btn_toHome = getView().findViewById(R.id.btn_toHome);
        btn_toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("time", time);
                startActivity(intent);
            }
        });
        bookPrice = getView().findViewById(R.id.tv_bookPrice2);

    }
}