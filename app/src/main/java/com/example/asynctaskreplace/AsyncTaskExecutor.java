package com.example.asynctaskreplace;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class AsyncTaskExecutor<Params, Progress, Result> {
    private ExecutorService executor;
    private Handler handler;

    public AsyncTaskExecutor() {
        executor = Executors.newSingleThreadExecutor();
    }

    public Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    protected void onPreExecute() {}

    protected abstract Result doInBackground(Params... params); // must be implemented by derived class

    protected abstract void onPostExecute(Result result);

    protected void onProgressUpdate(Progress... values) {}

    public void publishProgress(Progress... values) {
        getHandler().post(() -> onProgressUpdate(values));
    }

    public void execute(Params... params) {
        getHandler().post(() -> {
            onPreExecute();
            executor.execute(() -> {
                Result result = doInBackground(params);
                getHandler().post(() -> onPostExecute(result));
            });
        });
    }

}
