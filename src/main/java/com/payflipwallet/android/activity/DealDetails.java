package com.payflipwallet.android.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.SoundEffectConstants;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.payflipwallet.android.config.AppUrls;


public class DealDetails extends AppCompatActivity {

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final WebView webview = new WebView(this);
        setContentView(webview);
        final ProgressDialog Dialog = ProgressDialog.show(DealDetails.this, "", "Loading...", true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.setVerticalScrollBarEnabled(false);
        webview.addJavascriptInterface(new WebViewJavaScriptInterface(DealDetails.this), "DealDetails");
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Snackbar.make(getCurrentFocus(), "Something went wrong and we are sorry for that. Please try again later", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                webview.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url,favicon);
                Dialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
               Dialog.hide();
                super.onPageFinished(view,url);
            }

        });
        Bundle extras = getIntent().getExtras();
        String title = extras.getString("title");
        getSupportActionBar().setTitle(title);
        webview.loadUrl(AppUrls.OfferDeatilsUrl+"?title="+title);
    }
    public class WebViewJavaScriptInterface {

        private Context context;

        public WebViewJavaScriptInterface(Context context){
            this.context = context;
        }
        @JavascriptInterface
        public void soundclick(){
            AudioManager  audioManager = (AudioManager)DealDetails. this.getSystemService(Context.AUDIO_SERVICE);
            audioManager.playSoundEffect(SoundEffectConstants.CLICK);
        }
        @JavascriptInterface
        public void Recharge(){
            Intent intent = new Intent(DealDetails.this, Recharge.class);
            startActivity(intent);
        }
        @JavascriptInterface
        public void Dth(){
            Intent intent = new Intent(DealDetails.this, Dth.class);
            startActivity(intent);
        }
        @JavascriptInterface
        public void Electricity(){
            Intent intent = new Intent(DealDetails.this, Electricity.class);
            startActivity(intent);
        }
        @JavascriptInterface
        public void Landline(){
            Intent intent = new Intent(DealDetails.this, Landline.class);
            startActivity(intent);
        }
        @JavascriptInterface
        public void Voucher(){
            Intent intent = new Intent(DealDetails.this, Voucher.class);
            startActivity(intent);
        }
        @JavascriptInterface
        public void Sendmoney(){
            Intent intent = new Intent(DealDetails.this, SendMoney.class);
            startActivity(intent);
        }
        @JavascriptInterface
        public void OpenUrl(String url){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}




