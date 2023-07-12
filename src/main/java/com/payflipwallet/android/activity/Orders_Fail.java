package com.payflipwallet.android.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.payflipwallet.android.R;
import com.payflipwallet.android.config.Session;

public class Orders_Fail extends AppCompatActivity {
    Session session;
    RelativeLayout recharge_layout,account_layout;
    String getnumber,getacnumber;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_failure);
        session = new Session(getApplicationContext());

        recharge_layout = findViewById(R.id.recharge_layout);
        account_layout = findViewById(R.id.account_layout);
        Button done_btn =  findViewById(R.id.done_btn);
        TextView orderid= findViewById(R.id.orderid);
        TextView date_time= findViewById(R.id.date_time);
        TextView paid_amount= findViewById(R.id.paid_amount);
        TextView type_name= findViewById(R.id.type_name);
        TextView paid_ac_number= findViewById(R.id.paid_ac_number);
        TextView paid_number= findViewById(R.id.paid_number);
        TextView my_name= findViewById(R.id.my_name);
        TextView my_mobile= findViewById(R.id.my_mobile);
        TextView my_email= findViewById(R.id.my_email);

        Bundle b = getIntent().getExtras();
        String getname =(String) b.get("name");
        getnumber =(String) b.get("paid_number");
        getacnumber =(String) b.get("paid_ac_number");
        String getamount =(String) b.get("paid_amount");
        String getorderid =(String) b.get("orderid");
        String getdate_time =(String) b.get("date_time");

        type_name.setText(getname);
        paid_number.setText(getnumber);
        orderid.setText("Order ID: " +getorderid);
        date_time.setText(getdate_time);
        paid_amount.setText("\u20B9" +getamount);
        my_name.setText(session.myname);
        my_mobile.setText(session.mymobile);
        my_email.setText(session.myemail);

        if(getacnumber.equals("")){
            account_layout.setVisibility(View.GONE);
        }else{
            account_layout.setVisibility(View.VISIBLE);
        }

        if(getnumber.equals("")){
            recharge_layout.setVisibility(View.GONE);
        }else{
            recharge_layout.setVisibility(View.VISIBLE);
        }

        done_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity( new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
    @Override
    public void onBackPressed() {

    }
}

