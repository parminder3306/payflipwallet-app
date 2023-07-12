package com.payflipwallet.android.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class RequestMoney extends AppCompatActivity {
    Session session;
    ProgressDialog Dialog;
    String mobile, amount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requestmoney);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());

        final TextInputLayout enter_mobile = (TextInputLayout) findViewById(R.id.enter_mobile);
        final TextInputLayout enter_amount = (TextInputLayout) findViewById(R.id.enter_amount);
        Button requestmoney_btn = (Button) findViewById(R.id.requestmoney_btn);
        requestmoney_btn.setOnClickListener(new View.OnClickListener() {
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
                    requestSend();
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

    public void requestSend() {
        Request("Please Wait...");
        String postdata="name="+session.myname+"&mymobile="+session.mymobile+"&mobile="+mobile+"&amount="+amount;
        new HttpRequestTask( new HttpRequest(AppUrls.RequestMoneyUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                RequestComplete();
                if (response.code == 200) {
                    try {
                        JSONObject json = new JSONObject(response.body);
                        Snackbar.make(getCurrentFocus(), json.getString("msg"), Snackbar.LENGTH_LONG).show();
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




