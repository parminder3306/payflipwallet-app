package com.payflipwallet.android.activity;

import android.content.Intent;
import android.os.Bundle;;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.payflipwallet.android.R;
import com.payflipwallet.android.config.Session;

/**
 * Created by Parminder Samra on 19-Dec-17.
 */
public class ReferEarn extends AppCompatActivity {
    Session session;
    TextView refer_code, offer;
    String refermoney, referurl;
    Button share_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refer_earn);;
        session = new Session(getApplicationContext());

        refer_code= findViewById(R.id.refer_code);
        offer= findViewById(R.id.offer);
        share_btn= findViewById(R.id.share_btn);


        refer_code.setText(session.myrefercode);
        offer.setText("");

        share_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent sharingIntent= new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"I have added \u20B9"+refermoney+" to your PayflipWallet wallet.");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,referurl);
                startActivity(Intent.createChooser(sharingIntent,"Share With"));

            }
        });
    }
}