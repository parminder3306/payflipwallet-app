package com.payflipwallet.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.payflipwallet.android.R;

/**
 * Created by Parminder Samra on 19-Dec-17.
 */
public class AboutUs extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView txt = findViewById(R.id.check_terms);
        txt.setText(R.string.terms_conditions);
        txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")));
    }
}