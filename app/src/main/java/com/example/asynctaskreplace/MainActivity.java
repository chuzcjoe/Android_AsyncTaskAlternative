package com.example.asynctaskreplace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private AsyncTaskExecutor<Integer, Integer, String> asyncTaskExecutor;

    private Integer count = 10;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.process_bar);
    }

    public void startAsyncTask(View v) {
        asyncTaskExecutor = new Async(MainActivity.this);
        asyncTaskExecutor.execute(10);
    }

    private static class Async extends AsyncTaskExecutor<Integer, Integer, String> {
        private WeakReference<MainActivity> activityWeakReference;

        public Async(MainActivity activity) {
            activityWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            for (int i = 1; i < integers[0]; i++) {
                publishProgress(i * 100 / integers[0]);
                SystemClock.sleep(1000);
            }
            return "Finished";
        }

        @Override
        protected void onPostExecute(String s) {
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            activity.progressBar.setVisibility(View.INVISIBLE);
            activity.progressBar.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.progressBar.setProgress(values[0]);
        }
    }
}