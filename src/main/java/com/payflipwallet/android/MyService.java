package com.payflipwallet.android;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.google.firebase.iid.FirebaseInstanceId;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Janny Samra on 11/3/2018.
 */

public class MyService extends Service {
    Session session;
    String UserData = "userdata";
    String Operators = "operators";
    private Timer timer = new Timer();

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        session = new Session(getApplicationContext());
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ReceiveData();
                loadOperators();
            }
        }, 0, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void ReceiveData() {
        if(session.myid !=null) {
            String postdata = "uid=" + session.myid + "&serial=" + Build.SERIAL + "&token=" + FirebaseInstanceId.getInstance().getToken();
            new HttpRequestTask(new HttpRequest(AppUrls.ReceiveDataUrl, HttpRequest.POST, postdata), new HttpRequest.Handler() {
                @Override
                public void response(HttpResponse response) {
                    if (response.code == 200) {
                        session.editor.putString(UserData,response.body);
                        session.editor.commit();
                    }
                }
            }).execute();
        }
        }
        private void loadOperators(){
        new HttpRequestTask(new HttpRequest(AppUrls.OperatorsUrl, HttpRequest.GET), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                if (response.code == 200) {
                    session.editor.putString(Operators,response.body);
                    session.editor.commit();
                }
            }
        }).execute();
    }

    }
