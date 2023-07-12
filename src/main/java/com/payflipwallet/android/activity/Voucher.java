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

public class Voucher extends AppCompatActivity {
    Session session;
    ProgressDialog Dialog;
    Button redeem_btn;
    String promocode;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voucher);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());

        final TextInputLayout enter_promo = findViewById(R.id.enter_promo);
        redeem_btn =  findViewById(R.id.redeem_btn);
        TextView walletbalance= findViewById(R.id.wallet_balance) ;
        redeem_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                promocode = enter_promo.getEditText().getText().toString();
                if (promocode.isEmpty()) {
                    enter_promo.setError("Enter Promocode");
                } else if (promocode.length() < 4) {
                    enter_promo.setError("Please enter at least 4 characters.");
                }
                else {
                    enter_promo.setErrorEnabled(false);
                    requestRedeem();
                }
            }
        });
        walletbalance.setText("\u20B9" +session.mywallet);
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public void requestRedeem() {
        Request("Please wait...");
        String postdata="uid="+session.myid+"&promocode="+promocode;
        new HttpRequestTask( new HttpRequest(AppUrls.VoucherUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                RequestComplete();
                if (response.code == 200) {
                    try {
                        JSONObject json = new JSONObject(response.body);
                        if(json.getString("msg").equals("success")){
                            Intent money_success = new Intent(getApplicationContext(), Money_Success.class);
                            money_success.putExtra("orderid", json.getString("orderid"));
                            money_success.putExtra("amount", json.getString("amount"));
                            money_success.putExtra("date_time", json.getString("date")+" "+json.getString("time"));
                            startActivity(money_success);
                        }else{
                            Snackbar.make(getCurrentFocus(), json.getString("msg"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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




