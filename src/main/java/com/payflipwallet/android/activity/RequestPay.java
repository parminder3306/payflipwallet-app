package com.payflipwallet.android.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.payflipwallet.android.R;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestPay extends AppCompatActivity {
    Session session;
    ProgressDialog Dialog;
    Button paynow_btn;
    String mobile, amount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requestpay);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());

        paynow_btn =  findViewById(R.id.paynow_btn);
        TextView requestamount =  findViewById(R.id.requestamount);
        TextView wallet_balance =  findViewById(R.id.wallet_balance);
        TextView my_mobile =  findViewById(R.id.my_mobile);
        TextView my_name =  findViewById(R.id.my_name);
        TextView friend_name =  findViewById(R.id.friend_name);
        TextView friend_no =  findViewById(R.id.friend_no);

        wallet_balance.setText("\u20B9" +session.mywallet);
        my_mobile.setText(session.mymobile);
        my_name.setText(session.myname);

        Bundle b = getIntent().getExtras();
        String getname =(String) b.get("name");
        amount =(String) b.get("amount");
        mobile =(String) b.get("mobile");
        requestamount.setText("\u20B9" +amount);
        friend_name.setText(getname);
        friend_no.setText("Send To: "+mobile);

        paynow_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    applyOffers();
            }
        });
    }

    public void applyOffers() {
        String postdata="apply=SendMoney&amount="+amount+"&uid="+session.myid;
        new HttpRequestTask( new HttpRequest(AppUrls.CheckOffersUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                if (response.code == 200 && response.body.length()!=0) {
                    try {
                        JSONObject json = new JSONObject(response.body);
                        requestSendmoney(json.getString("promocode"),json.getString("cashback"),json.getString("openable"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestSendmoney("","","");
                }
            }
        }).execute();
    }
    public void requestSendmoney(final String promocode, final String cashback, final String openable) {
        Request("Please Wait...");
        String postdata="uid="+session.myid+"&mobile"+mobile+"&promocode"+promocode+"&cashback"+cashback+"&openable"+openable+"&amount"+amount;
        new HttpRequestTask( new HttpRequest(AppUrls.SendMoneyUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                RequestComplete();
                if (response.code == 200) {
                    try {
                        JSONObject json = new JSONObject(String.valueOf(response));
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
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }
}




