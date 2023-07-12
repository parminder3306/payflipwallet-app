package com.payflipwallet.android.http;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpResponse;

import java.lang.ref.WeakReference;

public class HttpRequestTask extends AsyncTask<Void, Void, com.payflipwallet.android.http.HttpResponse> {

    private final com.payflipwallet.android.http.HttpRequest httpRequest;
    private final com.payflipwallet.android.http.HttpRequest.Handler handler;
    private final WeakReference<Activity> activityRef;
    private final boolean activityRefSet;

    /**
     * start an async task for handling the http request and given a response handler
     * @param httpRequest
     * @param handler
     */
    public HttpRequestTask(com.payflipwallet.android.http.HttpRequest httpRequest, com.payflipwallet.android.http.HttpRequest.Handler handler) {
        this(httpRequest, handler, null);
    }

    /**
     * start an async task for handling the http request, a response handler and the activity that is sending this request,
     * and it will check if the activity is not finished before calling the response handler
     * @param httpRequest
     * @param handler
     * @param activity
     */
    public HttpRequestTask(com.payflipwallet.android.http.HttpRequest httpRequest, HttpRequest.Handler handler, Activity activity) {
        this.httpRequest = httpRequest;
        this.handler = handler;
        this.activityRef = new WeakReference<>(activity);
        this.activityRefSet = activity != null;
    }

    @Override
    protected com.payflipwallet.android.http.HttpResponse doInBackground(Void... params) {
        return httpRequest.request();
    }

    @Override
    protected void onPostExecute(final com.payflipwallet.android.http.HttpResponse response) {
        handleResponse(response);
    }

    @Override
    protected void onCancelled(){
        handleResponse(new com.payflipwallet.android.http.HttpResponse());
    }

    private void handleResponse(HttpResponse response) {

        if (handler == null) {
            return;
        }

        if (!activityRefSet) {
            handler.response(response);
        } else if (activityRef.get() != null && !activityRef.get().isFinishing()) {
            handler.response(response);
        } else {
            Log.d("http-request", "activity finished so will not respond");
        }
    }
}

