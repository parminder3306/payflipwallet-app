package com.payflipwallet.android.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.payflipwallet.android.R;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class SendMoney extends AppCompatActivity {
    Session session;
    ProgressDialog Dialog;
    String mobile,amount;
    TextInputLayout enter_mobile,enter_amount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmoney);
        session = new Session(getApplicationContext());

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        enter_mobile = findViewById(R.id.enter_mobile);
        enter_amount =  findViewById(R.id.enter_amount);
        Button sendmoney_btn = findViewById(R.id.sendmoney_btn);


        sendmoney_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                String mobilevaild = "false";
                String amountvaild = "false";
                mobile = enter_mobile.getEditText().getText().toString();
                amount = enter_amount.getEditText().getText().toString();
                int amount1;
                try{
                    amount1=Integer.valueOf(amount);
                }catch (NumberFormatException nfe){
                    amount1=0;
                }
                if (mobile.isEmpty()) {
                    enter_mobile.setError("Enter Mobile Number");
                } else if (mobile.length() < 10) {
                    enter_mobile.setError("Enter vaild mobile number");
                } else {
                    enter_mobile.setErrorEnabled(false);
                    mobilevaild = "true";
                }
                if (amount.isEmpty()) {
                    enter_amount.setError("Enter Amount");
                } else if (amount1 < 10) {
                    enter_amount.setError("Amount must be more than 10");
                }
                else if (amount1 > 5000) {
                    enter_amount.setError("Amount must be more than 10 to 5000");
                }
                else {
                    enter_amount.setErrorEnabled(false);
                    amountvaild = "true";
                }
                if (mobilevaild.equals("true") && amountvaild.equals("true")) {
                    applyOffers();
                }
            }
        });

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void applyOffers() {
        String postdata="apply=SendMoney&amount="+amount+"&uid="+session.myid;
        new HttpRequestTask( new HttpRequest(AppUrls.CheckOffersUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                if (response.code == 200 ) {
                    if(response.body.length()==0){
                        requestSendmoney("","","");

                    }else {

                        try {
                            JSONObject json = new JSONObject(response.body);
                            requestSendmoney(json.getString("promocode"), json.getString("cashback"), json.getString("openable"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).execute();
    }


    public void requestSendmoney(final String promocode,final String cashback, final String openable) {
        Request("Please Wait...");
        String postdata="uid="+session.myid+"&mobile="+mobile+"&promocode="+promocode+"&cashback="+cashback+"&openable="+openable+"&amount="+amount;
        new HttpRequestTask( new HttpRequest(AppUrls.SendMoneyUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                RequestComplete();
                if (response.code == 200) {
                    try {
                        JSONObject json = new JSONObject(response.body);
                        if(json.getString("msg").equals("success")){
                            Intent SuccessActivity = new Intent(getApplicationContext(), Orders_Success.class);
                            SuccessActivity.putExtra("name", "Mobile Number");
                            SuccessActivity.putExtra("paid_number", mobile);
                            SuccessActivity.putExtra("paid_ac_number", "");
                            SuccessActivity.putExtra("paid_amount", amount);
                            SuccessActivity.putExtra("date_time", json.getString("date")+" "+json.getString("time"));
                            SuccessActivity.putExtra("orderid", json.getString("orderid"));
                            SuccessActivity.putExtra("reward", json.getString("reward"));
                            startActivity(SuccessActivity);
                        }else{
                            Snackbar.make(getCurrentFocus(), json.getString("msg"), Snackbar.LENGTH_LONG).show();
                        }
                    }  catch (JSONException e) {
                        e.printStackTrace();
                        RequestComplete();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }


    public void Request(String message){
        Dialog = new ProgressDialog(this);
        Dialog.setMessage(message);
        Dialog.setCancelable(false);
        Dialog.show();
    }
    public void RequestComplete(){
        Dialog.dismiss();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}




