package com.example.secondhandbookappv2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;

public class DataFragment_2 extends Fragment {

    private static final int READ_REQUEST_CODE = 3;
    private static final int CAMERA_REQUEST_CODE = 4;
    public static boolean TO_READ_OR_CAMERA = false;
    private static final String TAG = DataFragment_2.class.getSimpleName();
    public static DataFragment_2 instance;
    private int currentView = -1;
    private ImageView[] imageViews = new ImageView[6];
    private boolean[] imageViewsState = new boolean[]{false, false, false, false, false, false};
    private int imageViewCount = 0;
    private boolean singleChange = false;
    private Uri[] imageUri = new Uri[6];
    private FloatingActionButton fab_camera;
    private Queue<Uri> uriQueue = new LinkedList<Uri>();
    private Button btn_send;
    private TextView tv_choosePage;

    public DataFragment_2() {
        // Required empty public constructor
    }

    public static DataFragment_2 getInstance(){
        if (instance == null)
            instance = new DataFragment_2();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        findView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        imageViewsState = new boolean[]{false, false, false, false, false, false};
        singleChange = false;
        currentView = -1;
        imageViewCount = 0;
    }

    private void findView() {
        int[] imageIDs = new int[]{ R.id.Data2_image1,
                R.id.Data2_image2,
                R.id.Data2_image3,
                R.id.Data2_image4,
                R.id.Data2_image5,
                R.id.Data2_image6 };
        for (int i = 0; i < 6; i++){
            int finalI = i;
            imageViews[i] = getView().findViewById(imageIDs[i]);
            imageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentView = finalI;
                    toMediaCabinet();
                }
            });
        }
        fab_camera = getView().findViewById(R.id.fab_camera1);
        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TO_READ_OR_CAMERA = true;
                Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });
        btn_send = getView().findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean Fragment2IsDone = true;
                for (boolean currentFlag : imageViewsState){
                    if (!currentFlag)
                        Fragment2IsDone = false;
                }
                SharedPreferences preferences = getContext().getSharedPreferences("currentData", Context.MODE_PRIVATE);
                preferences.edit().putBoolean("Fragment-2-IsDone", Fragment2IsDone).commit();
                if (Fragment2IsDone){
                    preferences.edit()
                            .putString("page-1", imageUri[0].toString())
                            .putString("page-2", imageUri[1].toString())
                            .putString("page-3", imageUri[2].toString())
                            .putString("page-4", imageUri[3].toString())
                            .putString("page-5", imageUri[4].toString())
                            .putString("page-6", imageUri[5].toString())
                            .commit();
                }

                boolean[] pageIsDone = new boolean[2];
                pageIsDone[0] = preferences.getBoolean("Fragment-1-IsDone", false);
                pageIsDone[1] = preferences.getBoolean("Fragment-2-IsDone", false);
                if (pageIsDone[0] && pageIsDone[1]){
                    Intent intent = new Intent(getContext(), UploadActivity.class);
                    startActivity(intent);
                }
                else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("提醒")
                            .setMessage("前方欄位尚未填完，需填完後才可繼續下一步")
                            .show();
                }
            }
        });
    }

    private void toMediaCabinet() {
        TO_READ_OR_CAMERA = true;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (!imageViewsState[currentView]){
            singleChange = false;
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        else
            singleChange = true;
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TO_READ_OR_CAMERA = false;
        processPhoto(requestCode, resultCode, data);
    }

    private void processPhoto(int requestCode, int resultCode, Intent data) {
        Uri selectedImage;
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getData() != null) {
                selectedImage = data.getData();
                uriQueue.offer(selectedImage);
            }
            else {
                if (data.getClipData() != null){
                    for (int i = 0; i < data.getClipData().getItemCount(); i++){
                        selectedImage = data.getClipData().getItemAt(i).getUri();
                        uriQueue.offer(selectedImage);
                    }
                }
            }
            if (singleChange){
                Uri uri = uriQueue.poll();
                Bitmap image = null;
                try {
                    image = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                    image = getResizedBitmap(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageViews[currentView].setImageBitmap(image);
                imageUri[currentView] = uri;
            }
            else {
                while (!uriQueue.isEmpty()){
                    Uri uri = uriQueue.poll();
                    try {
                        Bitmap image = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                        image = getResizedBitmap(image);
                        if (imageViewCount < 6){
                            imageViews[imageViewCount].setImageBitmap(image);
                            imageUri[imageViewCount] = uri;
                            imageViewsState[imageViewCount] = true;
                            imageViewCount++;
                        }
                        else {
                            uriQueue.clear();
                            imageViewCount = 0;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        float newWidth = 512, newHeight = 512;
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}