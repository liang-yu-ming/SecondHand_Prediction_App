package com.example.secondhandbookappv2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class TCPIntentService extends IntentService {

    private static final String TAG = TCPIntentService.class.getSimpleName();
    public static final String ACTION_TCP_DONE = "got image";
    private Socket socket;
    private final String serverip = "120.126.151.184";
    private final int serverport = 5422;
    private byte[] sendPackage = new byte[0];
    private double yellowSpotAverage;
    private double yellowSpotSD;
    private double letterAverage;
    private double letterSD;
    private long CTR;
    private double discount;
    private String time;
    private String bookName;
    private String bookDate;
    private String bookCategory;
    private String bookPrice;
    private String[] detectedImagePath = new String[6];

    public TCPIntentService() {
        super("TCPIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        time = intent.getStringExtra("time");
        String[] resizeImagePath = intent.getStringArrayExtra("resizeImagePath");
        try {
            socket = new Socket(serverip,serverport);
            socket.setSoTimeout(10000000);
            sendPackage = getSendPackage(resizeImagePath, sendPackage);
            send(sendPackage);
            getServerData();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getBookInformation();
        reptile();
        calculateDiscount();

        Intent done = new Intent();
        done.setAction(ACTION_TCP_DONE);
        done.putExtra("detectedImagePath", detectedImagePath);
        done.putExtra("searchDegree", CTR);
        done.putExtra("discount", discount);
        done.putExtra("yellowSpotAverage", yellowSpotAverage);
        done.putExtra("yellowSpotSD", yellowSpotSD);
        done.putExtra("letterAverage", letterAverage);
        done.putExtra("letterSD", letterSD);
        sendBroadcast(done);
    }

    private void getBookInformation() {
        SharedPreferences preferences = getSharedPreferences("currentData", Context.MODE_PRIVATE);
        bookName = preferences.getString("bookName", "");
        bookDate = preferences.getString("bookDate", "");
        bookCategory = preferences.getString("bookCategory", "");
        bookPrice = preferences.getString("bookPrice", "");
    }

    private byte[] getSendPackage(String[] srcPath, byte[] sendPackage){
        for (int i = 0; i < 6; i++){
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath[i]);
            byte[] byteImage = bitmapToByte(bitmap);
            if (byteImage != null){
                byte[] byteImageSize = ByteBuffer.allocate(4).putInt(byteImage.length).array();
                byteImage = addBytes(byteImageSize, byteImage);
                sendPackage = addBytes(sendPackage, byteImage);
            }
        }
        byte[] packageSize = ByteBuffer.allocate(4).putInt(sendPackage.length).array();
        sendPackage = addBytes(packageSize, sendPackage);
        return sendPackage;
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        if (bitmap != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }
        return null;
    }

    private void send(byte[] sendMsg) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(sendMsg);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getServerData() {
        int imageCount = 0;
        try {
            byte[] frame =  new byte[1024];
            byte[] data = new byte[0];
            byte[] current_data;
            
            while (data.length < 4){
                int current_rec = socket.getInputStream().read(frame);
                current_data = new byte[current_rec];
                System.arraycopy(frame, 0, current_data, 0,current_rec);
                data = addBytes(data, current_data);
            }
            byte[] alldatalength_byte = new byte[4];
            System.arraycopy(data, 0, alldatalength_byte, 0,alldatalength_byte.length);
            data = cliparray(data, 4);
            int alldatalength_int = ByteBuffer.wrap(alldatalength_byte).getInt();

            // yellowSpotAverage
            while (data.length < 8){
                int current_rec = socket.getInputStream().read(frame);
                current_data = new byte[current_rec];
                System.arraycopy(frame, 0, current_data, 0,current_rec);
                data = addBytes(data, current_data);
            }
            byte[] yellowSpotAverage_byte = new byte[8];
            System.arraycopy(data, 0, yellowSpotAverage_byte, 0,yellowSpotAverage_byte.length);
            data = cliparray(data, 8);
            yellowSpotAverage = ByteBuffer.wrap(yellowSpotAverage_byte).getDouble();
            alldatalength_int -= 8;

            // yellowSpotSD
            while (data.length < 8){
                int current_rec = socket.getInputStream().read(frame);
                current_data = new byte[current_rec];
                System.arraycopy(frame, 0, current_data, 0,current_rec);
                data = addBytes(data, current_data);
            }
            byte[] yellowSpotSD_byte = new byte[8];
            System.arraycopy(data, 0, yellowSpotSD_byte, 0,yellowSpotSD_byte.length);
            data = cliparray(data, 8);
            yellowSpotSD = ByteBuffer.wrap(yellowSpotSD_byte).getDouble();
            alldatalength_int -= 8;

            // letterAverage
            while (data.length < 8){
                int current_rec = socket.getInputStream().read(frame);
                current_data = new byte[current_rec];
                System.arraycopy(frame, 0, current_data, 0,current_rec);
                data = addBytes(data, current_data);
            }
            byte[] letterAverage_byte = new byte[8];
            System.arraycopy(data, 0, letterAverage_byte, 0,letterAverage_byte.length);
            data = cliparray(data, 8);
            letterAverage = ByteBuffer.wrap(letterAverage_byte).getDouble();
            alldatalength_int -= 8;

            // letterSD
            while (data.length < 8){
                int current_rec = socket.getInputStream().read(frame);
                current_data = new byte[current_rec];
                System.arraycopy(frame, 0, current_data, 0,current_rec);
                data = addBytes(data, current_data);
            }
            byte[] letterSD_byte = new byte[8];
            System.arraycopy(data, 0, letterSD_byte, 0,letterSD_byte.length);
            data = cliparray(data, 8);
            letterSD = ByteBuffer.wrap(letterSD_byte).getDouble();
            alldatalength_int -= 8;

            while (alldatalength_int > 0){
                while (data.length < 4){
                    int current_rec = socket.getInputStream().read(frame);
                    current_data = new byte[current_rec];
                    System.arraycopy(frame, 0, current_data, 0,current_rec);
                    data = addBytes(data, current_data);
                }
                byte[] oneimagelength_byte = new byte[4];
                System.arraycopy(data, 0, oneimagelength_byte, 0,oneimagelength_byte.length);
                data = cliparray(data, 4);
                int oneimagelength_int = ByteBuffer.wrap(oneimagelength_byte).getInt();
                alldatalength_int -= 4;
                while (data.length < oneimagelength_int){
                    int current_rec = socket.getInputStream().read(frame);
                    current_data = new byte[current_rec];
                    System.arraycopy(frame, 0, current_data, 0,current_rec);
                    data = addBytes(data, current_data);
                }
                byte[] oneimage = new byte[oneimagelength_int];
                System.arraycopy(data, 0, oneimage, 0,oneimage.length);
                data = cliparray(data, oneimage.length);
                alldatalength_int -= oneimagelength_int;

                detectedImagePath[imageCount] = byteToFile(time, oneimage, imageCount);
                imageCount++;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    private String byteToFile(String folderName, byte[] image, int imageCount){
        String dirPath = this.getFilesDir() + "/history/" + folderName + "/detected";
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        String imagePath = dirPath + "/detectedImage" + imageCount + ".png";
        File imagefile = new File(imagePath);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagefile);
            fos.write(image, 0, image.length);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    private byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }

    public byte[] cliparray(byte[] array, int x){
        byte[] array1 = new byte[array.length-x];
        System.arraycopy(array, x,array1, 0, array1.length);
        return array1;
    }

    private void reptile() {
        String tmp = "";
        if(isNetworkAvailable(this)){
            String NetUrl = "https://www.google.com/search?q=" + bookName;
            Connection conn = Jsoup.connect(NetUrl);
            conn.header("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36");
            try {
                final Document docs = conn.get();
                tmp = docs.select("div[id=result-stats]").text();
            }catch (IOException e){
                e.printStackTrace();
            }
        }else {
            System.out.println("Network error");
        }
        String[] tmpSplit = tmp.split(" ");
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        Number number = 0;
        try {
            number = format.parse(tmpSplit[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        CTR = number.longValue();
        // preferences.edit().putString("SearchDegree", String.valueOf(CTR));
    }

    public boolean isNetworkAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE) ;
        if(cm == null)
            return false;
        else {
            NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0){
                for (int i = 0;i < networkInfo.length; i++)
                    if(networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                        return  true;
            }
        }
        return false;
    }

    private void calculateDiscount() {
        String[] currentTimeSplit = time.split("_");
        int currentTimeInt = Integer.valueOf(currentTimeSplit[0]) * 365 + Integer.valueOf(currentTimeSplit[1]) * 30 + Integer.valueOf(currentTimeSplit[2]);
        String[] bookTimeSplit = bookDate.split("-"); // 取得書本的時間
        int bookTimeInt = Integer.valueOf(bookTimeSplit[0]) * 365 + Integer.valueOf(bookTimeSplit[1]) * 30 + Integer.valueOf(bookTimeSplit[2]);
        double age =  (currentTimeInt - bookTimeInt) / 365.0; // 取得年齡
        double item1 = (double)CTR / ((double)CTR + 1000.0);
        double item2 = Math.pow(2.71, -Math.pow((yellowSpotAverage + letterAverage * 1.5) * (1 + yellowSpotSD + letterSD * 1.5) * 35, 2.0));
        if(bookCategory.equals("文學小說") || bookCategory.equals("人文史地")){
            double item3 = 30.0 / (Math.log(age + 1.0) + 30.0);
            discount = item1 * item2 * item3;
        } else {
            double item3 = ((4.0 / 5.0) * (1.0 / (Math.pow(1.8, age - 8) + 1.0))) + (1.0 / 5.0 * 50.0 / (age + 50.0));
            discount = item1 * item2 * item3;
        }
        discount = (float)Math.round(discount * 100.0)/100.0;
    }

}