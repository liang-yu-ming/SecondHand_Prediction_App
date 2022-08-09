package com.example.secondhandbookappv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends MyActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.finishOneActivity(ResultActivity.class.getName());
        findView();
    }

    private void findView() {
        FloatingActionButton newRecord = findViewById(R.id.fab_newRecord);
        newRecord.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, DataActivity.class));
            // startActivity(new Intent(MainActivity.this, ResultActivity.class));
        });
        File dir = new File(MainActivity.this.getFilesDir() + "/history");
        File[] files = dir.listFiles();
        if (files != null){
            Log.d(TAG, "findView: jihihi");
            recyclerView = findViewById(R.id.recycleView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(new HistoryAdapter());
        }
    }


    public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{

        File dir = new File(MainActivity.this.getFilesDir() + "/history");
        File[] files = dir.listFiles();

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_history, parent, false);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
            File file = files[position];
            String JSONString = readJSONFile(file);
            try {
                JSONObject object = new JSONObject(JSONString);
                holder.historyTime.setText(file.getName());
                holder.historyBookName.setText(object.getString("bookName"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putExtra("fromWhere", "main");
                    intent.putExtra("time", file.getName());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return files.length;
        }

        public class HistoryViewHolder extends RecyclerView.ViewHolder{
            TextView historyTime;
            TextView historyBookName;
            ConstraintLayout layout;

            public HistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                historyTime = itemView.findViewById(R.id.item_historyTime);
                historyBookName = itemView.findViewById(R.id.item_historyBookName);
                layout = itemView.findViewById(R.id.layout);
            }
        }
    }

    private String readJSONFile(File file) {
        String path = file.getPath() + "/record.json";
        String fileAsString = null;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }

            fileAsString = sb.toString();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return fileAsString;
    }
}