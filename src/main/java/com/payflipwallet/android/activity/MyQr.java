package com.payflipwallet.android.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.WriterException;
import com.payflipwallet.android.R;
import com.payflipwallet.android.config.Session;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MyQr extends AppCompatActivity {
    Session session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr);
        session = new Session(getApplicationContext());

        TextView my_name= (TextView)findViewById(R.id.my_name);
        TextView my_mobile= (TextView)findViewById(R.id.my_mobile);
        ImageView image = (ImageView) findViewById(R.id.qrcode);
        my_name.setText(session.myname);
        my_mobile.setText(session.mymobile);

        String data="{\"provider\":\"PayflipWallet\",\"name\":\""+session.myname+"\",\"mobile\":\""+session.mymobile+"\"}";

        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT,500);
        try {
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            image.setImageBitmap(bitmap);
        } catch (WriterException e) {
        }



}
}

