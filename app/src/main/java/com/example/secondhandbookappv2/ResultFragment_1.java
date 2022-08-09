package com.example.secondhandbookappv2;

import android.content.Intent;
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ResultFragment_1 extends Fragment {

    private static final String TAG = ResultFragment_1.class.getSimpleName();
    public static ResultFragment_1 instance;
    private TextView bookName;
    private TextView bookCategory;
    private TextView bookDate;
    private TextView searchDegree;
    private TextView originBookPriceTitle;
    private TextView originBookPrice;
    private TextView yellowSpotAverage;
    private TextView yellowSpotSD;
    private TextView letterAverage;
    private TextView letterSD;
    private TextView discount;
    private TextView bookPrice;
    private TextView bookPriceTitle;
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
        String jsonString = readJSONFile();
        getValue(jsonString);
    }

    private void getValue(String input) {
        try {
            JSONObject object = new JSONObject(input);
            bookName.setText(object.getString("bookName"));
            bookCategory.setText(object.getString("bookCategory"));
            bookDate.setText(object.getString("bookDate"));
            searchDegree.setText(object.getString("searchDegree"));
            yellowSpotAverage.setText(String.format("%.2f", object.getDouble("yellowSpotAverage") * 100) + " %");
            yellowSpotSD.setText(String.format("%.2f", object.getDouble("yellowSpotSD")));
            letterAverage.setText(String.format("%.2f", object.getDouble("letterAverage") * 100) + " %");
            letterSD.setText(String.format("%.2f", object.getDouble("letterSD")));
            discount.setText(String.format("%.1f", object.getDouble("discount") * 100) + " %");
            String priceString = object.getString("bookPrice");
            if (priceString.equals("")) {
                bookPriceTitle.setVisibility(View.GONE);
                bookPrice.setVisibility(View.GONE);
                originBookPriceTitle.setVisibility(View.GONE);
                originBookPrice.setVisibility(View.GONE);
            }
            else {
                originBookPrice.setText("NT " + priceString);
                double priceInt = Integer.parseInt(priceString) * object.getDouble("discount");
                bookPrice.setText("NT " + (int)priceInt);
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
        } catch (IOException e){
            e.printStackTrace();
        }
        return fileAsString;
    }

    private void findView() {
        bookName = getView().findViewById(R.id.tv_resultBookName);
        bookCategory = getView().findViewById(R.id.tv_resultBookCategory);
        bookDate = getView().findViewById(R.id.tv_resultBookDate);
        searchDegree = getView().findViewById(R.id.tv_searchDegree);
        originBookPriceTitle = getView().findViewById(R.id.tv_resultBookPriceTitle);
        originBookPrice = getView().findViewById(R.id.tv_resultBookPrice);
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
        bookPriceTitle = getView().findViewById(R.id.tv_bookPriceTitle);
    }
}