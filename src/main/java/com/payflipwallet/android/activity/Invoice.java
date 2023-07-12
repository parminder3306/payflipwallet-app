package com.payflipwallet.android.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.payflipwallet.android.R;
import com.payflipwallet.android.config.Session;

public class Invoice extends AppCompatActivity {
    Session session;
    String logo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());

        TextView my_name= findViewById(R.id.my_name);
        TextView my_mobile= findViewById(R.id.my_mobile);
        TextView my_email= findViewById(R.id.my_email);
        TextView title= findViewById(R.id.title);
        TextView orderid= findViewById(R.id.orderid);
        TextView date_time= findViewById(R.id.date_time);
        TextView type_name= findViewById(R.id.type_name);
        TextView receive_amount= findViewById(R.id.receive_amount);
        ImageView status_logo= findViewById(R.id.status_logo);

        my_name.setText(session.myname);
        my_mobile.setText(session.mymobile);
        my_email.setText(session.myemail);

        Bundle b =  getIntent().getExtras();
        String gettitle =(String) b.get("title");
        String getorderid =(String) b.get("orderid");
        String getdate_time =(String) b.get("date_time");
        String getamount =(String) b.get("amount");
        String getstatus =(String) b.get("status");
        String getsymbol =(String) b.get("symbol");

        title.setText(gettitle);
        orderid.setText("Order ID: " +getorderid);
        date_time.setText(getdate_time);
        receive_amount.setText("\u20B9" +getamount);

        if(getsymbol.equals("+")){
            type_name.setText(R.string.amount_received_text);
        }else{
            type_name.setText(R.string.amount_paid_text);
        }
        if (getstatus.equals("Success")){
            logo = "order_successful";
        }
        else if(getstatus.equals("Processing")){
            logo = "order_pending";
        }else{
            logo ="order_failed";
        }

        int resID=getResources().getIdentifier(logo,"drawable",getApplicationContext().getPackageName());
        status_logo.setImageDrawable(getResources().getDrawable(resID));

}
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

