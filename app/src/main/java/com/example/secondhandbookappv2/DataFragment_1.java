package com.example.secondhandbookappv2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

public class DataFragment_1 extends Fragment {

    private static final String TAG = DataFragment_1.class.getSimpleName();
    public static DataFragment_1 instance;
    private EditText et_bookName;
    private EditText et_bookDate;
    private Spinner spinner_bookCategory;
    private EditText et_bookPage;
    private EditText et_bookPrice;
    private Button btn_choosePage;
    private boolean btn_choosePageIsClicked = false;
    private TextView tv_choosePage;

    public DataFragment_1() {
        // Required empty public constructor
    }

    public static DataFragment_1 getInstance(){
        if (instance == null)
            instance = new DataFragment_1();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.fragment_data_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView();
    }

    @Override
    public void onStart() {
        super.onStart();
        et_bookName.setText("");
        et_bookDate.setText("");
        et_bookPrice.setText("");
        et_bookPage.setText("");
    }

    @Override
    public void onPause() {
        super.onPause();
        boolean Fragment1IsDone = true;
        if (et_bookName.getText().toString().equals("") ||
                et_bookDate.getText().toString().equals("") ||
                String.valueOf(spinner_bookCategory.getSelectedItem()).equals("") ||
                !btn_choosePageIsClicked){
            Fragment1IsDone = false;
        }
        SharedPreferences preferences = getContext().getSharedPreferences("currentData", Context.MODE_PRIVATE);
        preferences.edit()
                .putBoolean("Fragment-1-IsDone", Fragment1IsDone)
                .putString("bookName", et_bookName.getText().toString())
                .putString("bookDate", et_bookDate.getText().toString())
                .putString("bookCategory", String.valueOf(spinner_bookCategory.getSelectedItem()))
                .putString("bookPrice", et_bookPrice.getText().toString())
                .putString("choosePage", tv_choosePage.getText().toString())
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private void findView() {
        et_bookName = getView().findViewById(R.id.et_bookName);
        et_bookDate = getView().findViewById(R.id.et_bookDate);
        et_bookDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickDlg();
            }
        });
        et_bookDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    showDatePickDlg();
            }
        });

        spinner_bookCategory = getView().findViewById(R.id.spinner_bookCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.book_category
                , android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_bookCategory.setAdapter(adapter);

        et_bookPage = getView().findViewById(R.id.et_bookPage);
        et_bookPrice = getView().findViewById(R.id.et_bookPrice);
        tv_choosePage = getView().findViewById(R.id.tv_choosePage);
        btn_choosePage = getView().findViewById(R.id.btn_choosePage);
        btn_choosePage.setOnClickListener(view -> {
            int totalPage = Integer.parseInt(et_bookPage.getText().toString());
            int[] page = new int[6];
            boolean randomFlag = false;
            if(totalPage >= 6){
                if(randomFlag){
                    for(int i = 0; i < 6; i++){
                        page[i] = new Random().nextInt(totalPage) + 1;
                        for (int j = 0; j < i; j++){
                            if(page[i] == page[j]){
                                page[i] = new Random().nextInt(totalPage) + 1;
                                j = -1;
                            }
                        }
                    }
                }
                else {
                    page[0] = 1;
                    page[1] = 2;
                    page[2] = totalPage / 3;
                    page[3] = totalPage / 2;
                    page[4] = totalPage / 3 * 2;
                    page[5] = totalPage;
                }
                Arrays.sort(page);
                String pageContent = Arrays.toString(page).replace("[", "");
                pageContent = pageContent.replace("]", "");
                pageContent = "選擇以下 " + pageContent + " 內頁頁數";
                tv_choosePage.setText(pageContent);
                btn_choosePageIsClicked = true;
            }
            else{
                Toast.makeText(getContext(), "請選擇 6 頁內頁以上的書籍!!", Toast.LENGTH_SHORT).show();
                et_bookPage.setText("");
            }
        });
    }

    private void showDatePickDlg() {
        Calendar calendar = Calendar.getInstance();//調用Calendar類獲取年月日
        int  mYear = calendar.get(Calendar.YEAR);//年
        int  mMonth = calendar.get(Calendar.MONTH);//月份要加一個一，這個值的初始值是0。不加會日期會少一月。
        int  mDay = calendar.get(Calendar.DAY_OF_MONTH);//日
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                et_bookDate.setText(i + "-" + (i1+1) + "-" + i2);//當選擇完後將時間顯示,記得月份i1加一
            }
        }, mYear,mMonth, mDay);//將年月日放入DatePickerDialog中，並將值傳給參數
        datePickerDialog.show();
    }
}