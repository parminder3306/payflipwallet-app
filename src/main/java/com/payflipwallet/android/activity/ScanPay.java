package com.payflipwallet.android.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.Result;
import com.payflipwallet.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanPay extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    private String flash="false";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActivityCompat.requestPermissions(ScanPay.this, new String[] {Manifest.permission.CAMERA}, 100);
        Context context = ScanPay.this;PackageManager pm = context.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("err", "Device has no camera!");
            return;
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
        // Start camera on resume
    }
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Invaild QR Code !");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mScannerView.resumeCameraPreview(ScanPay.this);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        final ProgressDialog dialog = ProgressDialog.show(ScanPay.this, "", "Please wait...", true);
        final Result result = rawResult;
        dialog.dismiss();
        try {
            JSONObject obj = new JSONObject(String.valueOf(result));
            String  provider =(obj.getString("provider"));
            String  name =(obj.getString("name"));
            String  mobile =(obj.getString("mobile"));
            if(provider.equals("PayflipWallet")) {
                Intent ii = new Intent(getApplicationContext(), Scan_Payments.class);
                ii.putExtra("name", name);
                ii.putExtra("mobile", mobile);
                startActivity(ii);
                dialog.show();
                alert.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scanpay, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.flashlight:
                if (flash=="true") {
                    mScannerView.setFlash(false);
                    flash="false";
                    item.setIcon(R.drawable.flash_on);

                } else {
                    mScannerView.setFlash(true);
                    flash="true";
                    item.setIcon(R.drawable.flash_off);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent ii = new Intent(ScanPay.this, MainActivity.class);
        startActivity(ii);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent ii = new Intent(ScanPay.this, MainActivity.class);
        startActivity(ii);

    }
}

