package com.payflipwallet.android.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.payflipwallet.android.R;
import com.payflipwallet.android.config.Session;

public class Money_Success extends AppCompatActivity {
    Session session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_success);
        session = new Session(getApplicationContext());

        Button done_btn =  findViewById(R.id.done_btn);
        TextView orderid= findViewById(R.id.orderid);
        TextView date_time= findViewById(R.id.date_time);
        TextView wallet_balance= findViewById(R.id.wallet_balance);
        TextView receive_amount= findViewById(R.id.receive_amount);
        TextView my_name= findViewById(R.id.my_name);
        TextView my_mobile= findViewById(R.id.my_mobile);
        TextView my_email= findViewById(R.id.my_email);

        Bundle b = getIntent().getExtras();
        String getorderid =(String) b.get("orderid");
        String getamount =(String) b.get("amount");
        String getdate_time =(String) b.get("date_time");

        orderid.setText("Order ID: " +getorderid);
        date_time.setText(getdate_time);
        wallet_balance.setText("\u20B9" +session.mywallet);
        receive_amount.setText("\u20B9" +getamount);
        my_name.setText(session.myname);
        my_mobile.setText(session.mymobile);
        my_email.setText(session.myemail);


        done_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
    @Override
    public void onBackPressed() {

    }
}

