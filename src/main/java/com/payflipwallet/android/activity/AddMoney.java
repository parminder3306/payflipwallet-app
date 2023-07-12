package com.payflipwallet.android.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.payflipwallet.android.R;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import java.util.Random;

public class AddMoney extends AppCompatActivity implements View.OnClickListener {
    String request_amount;
    Session session;
    int txnid =new Random().nextInt((80 - 20) + 1) + 20;
     PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
     PayUmoneySdkInitializer.PaymentParam paymentParam = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmoney);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());

        TextView payable_amount=(TextView) findViewById(R.id.payable_amount);
        TextView wallet_balance=(TextView) findViewById(R.id.wallet_balance);

        Bundle b = getIntent().getExtras();
        request_amount = (String) b.get("amount");
        payable_amount.setText("\u20B9" +request_amount);
        wallet_balance.setText("\u20B9" + session.mywallet);

        RelativeLayout b1 = (RelativeLayout)findViewById(R.id.b1);
        RelativeLayout b2 = (RelativeLayout)findViewById(R.id.b2);
        RelativeLayout b3 = (RelativeLayout)findViewById(R.id.b3);
        RelativeLayout b4 = (RelativeLayout)findViewById(R.id.b4);
        RelativeLayout b5 = (RelativeLayout)findViewById(R.id.b5);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b1:
                startpay();
                break;
            case R.id.b2:
               startpay();
                break;
            case R.id.b3:
                startpay();
                break;
            case R.id.b4:
                startpay();
                break;
            case R.id.b5:
                startpay();
                break;
        }
    }
    public void startpay(){
        builder.setAmount(request_amount)                          // Payment amount
                .setTxnId(String.valueOf(txnid))                     // Transaction ID
                .setPhone(session.mymobile)                   // User Phone number
                .setProductName("AddMoney")                   // Product Name or description
                .setFirstName(session.myname)                              // User First name
                .setEmail(session.myemail)              // User Email ID
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")     // Success URL (surl)
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")     //Failure URL (furl)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(false)                              // Integration environment - true (Debug)/ false(Production)
                .setKey(AppUrls.MerchantKey)                        // Merchant key
                .setMerchantId(AppUrls.MerchantId);


        try {
            paymentParam = builder.build();
            getHashkey();

        } catch (Exception e) {
        }

    }
    public void getHashkey() {
        String postdata = "key="+AppUrls.MerchantKey+"&txnid="+txnid+"&amount="+request_amount+"&productinfo=AddMoney&firstname="+ session.myname+"&email="+ session.myemail;
        new HttpRequestTask( new HttpRequest(AppUrls.PayuMoneyUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                if (response.code == 200) {
                    paymentParam.setMerchantHash(response.body);
                    PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, AddMoney.this, R.style.AppTheme_default, true);
                }
            }
        }).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    addMoney("Success");
                } else {
                    addMoney("Failure");
                }
            }
        }
    }

    public void addMoney(final String status) {
        String postdata = "uid="+ session.myid+"&mobile="+ session.mymobile+"&amount="+request_amount+"&status="+status;
        new HttpRequestTask( new HttpRequest(AppUrls.PayuMoneyUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                if (response.code == 200) {
                    paymentParam.setMerchantHash(response.body);
                    PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, AddMoney.this, R.style.AppTheme_default, true);
                }
            }
        }).execute();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }
    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        return true;
    }

}