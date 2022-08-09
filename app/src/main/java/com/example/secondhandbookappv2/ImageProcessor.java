package com.example.secondhandbookappv2;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class ImageProcessor{
    private String time;
    private Context context;
    private Uri[] uriImages = new Uri[6];
    private Bitmap[] bitmapImages = new Bitmap[6];
    private String[] originImagePath = new String[6];
    private String[] resizeImagePath = new String[6];

    public ImageProcessor(Context context, String time) {
        this.context = context;
        this.time = time;
        getBitmapFromUri(uriImages, bitmapImages, originImagePath);
        saveResizeBitmap(bitmapImages, resizeImagePath);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Uri[] getUriImages() {
        return uriImages;
    }

    public void setUriImages(Uri[] uriImages) {
        this.uriImages = uriImages;
    }

    public Bitmap[] getBitmapImages() {
        return bitmapImages;
    }

    public void setBitmapImages(Bitmap[] bitmapImages) {
        this.bitmapImages = bitmapImages;
    }

    public String[] getOriginImagePath() {
        return originImagePath;
    }

    public void setOriginImagePath(String[] originImagePath) {
        this.originImagePath = originImagePath;
    }

    public String[] getResizeImagePath() {
        return resizeImagePath;
    }

    public void setResizeImagePath(String[] resizeImagePath) {
        this.resizeImagePath = resizeImagePath;
    }

    private void getBitmapFromUri(Uri[] uriImages, Bitmap[] bitmapImages, String[] imagePath) {
        SharedPreferences preferences = context.getSharedPreferences("currentData", context.MODE_PRIVATE);
        for (int i = 0; i < 6; i++){
            uriImages[i] = Uri.parse(preferences.getString("page-" + (i + 1), ""));
            imagePath[i] = parseUriToPath(uriImages[i]);
            bitmapImages[i] = getResizedBitmap(imagePath[i]);
        }
    }

    private String parseUriToPath(Uri uri) {
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        }
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private Bitmap getResizedBitmap(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        int rotated = getOrientation(imagePath);
        bitmap = rotateImage(bitmap, rotated);
        return Bitmap.createScaledBitmap(bitmap, 512, 512, true);
    }

    private int getOrientation(String imageSrc){
        int rotated = 0;
        try {
            ExifInterface ei = new ExifInterface(imageSrc);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotated = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotated = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotated = 270;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotated = 0;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return rotated;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void saveResizeBitmap(Bitmap[] bitmapImages, String[] resizeImagePath) {
        File dir = new File(context.getFilesDir() + "/history/" + time + "/resized");
        if(!dir.exists()){
            dir.mkdirs();
        }
        for (int i = 0; i < 6; i++){
            try {
                String image_name = dir + "/resizedImage" + i + ".png";
                FileOutputStream fOut = new FileOutputStream(image_name);
                bitmapImages[i].compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                resizeImagePath[i] = image_name;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}