package com.payflipwallet.android.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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


public class ChangePassword extends AppCompatActivity {
    Session session;
    ProgressDialog Dialog;
    Button changepassword_btn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());

        final TextInputLayout old_password =  findViewById(R.id.old_password);
        final TextInputLayout new_password =  findViewById(R.id.new_password);
        changepassword_btn =  findViewById(R.id.changepassword_btn);
        changepassword_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                String oldpasswordvaild = "false";
                String newpasswordvaild = "false";
                String oldpassword = old_password.getEditText().getText().toString();
                String newpassword = new_password.getEditText().getText().toString();
                if (oldpassword.isEmpty()) {
                    old_password.setError("Enter Old Password");
                } else if (oldpassword.length() < 6) {
                    old_password.setError("Please enter at least 6 characters.");
                } else {
                    old_password.setErrorEnabled(false);
                    oldpasswordvaild = "true";
                }
                if (newpassword.isEmpty()) {
                    new_password.setError("Enter New Password");
                } else if (newpassword.length() < 6) {
                    new_password.setError("Please enter at least 6 characters.");
                } else {
                    new_password.setErrorEnabled(false);
                    newpasswordvaild = "true";
                }
                if (oldpasswordvaild.equals("true") && newpasswordvaild.equals("true")) {
                    requestUpdate(oldpassword,newpassword);
                }
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void requestUpdate(final String oldpass, final String newpass) {
        Request("Updating...");
        String postdata="accesskey="+ session.myaccesskey+"&oldpassword="+oldpass+"&newpassword="+newpass;
        new HttpRequestTask( new HttpRequest(AppUrls.ChangePasswordUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                RequestComplete();
                if (response.code == 200) {
                    try {
                        JSONObject json = new JSONObject(response.body);
                        Snackbar.make(getCurrentFocus(), json.getString("msg"), Snackbar.LENGTH_LONG).show();

                    }  catch (JSONException e) {
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


