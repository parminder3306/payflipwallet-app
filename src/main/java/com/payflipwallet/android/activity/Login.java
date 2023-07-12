package com.payflipwallet.android.activity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.payflipwallet.android.MyService;
import com.payflipwallet.android.R;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    ProgressDialog Dialog;
    Button forget_btn, signup_btn, login_btn;
    TextInputLayout enter_mobile, enter_password;
    EditText change_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        enter_mobile =  findViewById(R.id.enter_mobile);
        enter_password =  findViewById(R.id.enter_password);
        change_type =  findViewById(R.id.change_type);
        login_btn =  findViewById(R.id.login_btn);
        forget_btn =  findViewById(R.id.forget_btn);
        signup_btn =  findViewById(R.id.signup_btn);

        enter_password.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (change_type.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
                    change_type.setTransformationMethod(new SingleLineTransformationMethod());
                }
                else {
                    change_type.setTransformationMethod(new PasswordTransformationMethod());
                }

            }
        });
        forget_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Forget.class));
            }
        });
        signup_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class));
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                String mobilevaild = "false";
                String passwordvaild = "false";
                String mobile = enter_mobile.getEditText().getText().toString();
                String password = enter_password.getEditText().getText().toString();
                if (mobile.isEmpty()) {
                    enter_mobile.setError("Enter Mobile Number");
                } else if (mobile.length() < 10) {
                    enter_mobile.setError("Enter vaild mobile number");
                } else {
                    enter_mobile.setErrorEnabled(false);
                    mobilevaild = "true";
                }
                if (password.isEmpty()) {
                    enter_password.setError("Enter Your Password");
                } else if (password.length() < 6) {
                    enter_password.setError("Please enter at least 6 characters.");
                } else {
                    enter_password.setErrorEnabled(false);
                    passwordvaild = "true";
                }
                if (mobilevaild.equals("true") && passwordvaild.equals("true")) {
                    requestLogin(mobile,password);
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

    public void requestLogin(final String mobile, final String password) {
        Request("Authenticating...");
        String postdata = "serial="+Build.SERIAL+"&mobile="+mobile+"&password="+password;

        new HttpRequestTask( new HttpRequest(AppUrls.LoginUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                RequestComplete();
                if (response.code == 200) {
                    try {
                        JSONObject json = new JSONObject(response.body);
                        if(json.getString("msg").equals("success")){
                            new Session(getApplicationContext()).createLoginSession(response.body);
                            stopService(new Intent(getApplicationContext(), MyService.class));
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }else{
                            Snackbar.make(getCurrentFocus(), json.getString("msg"), Snackbar.LENGTH_LONG).show();
                        }
                    }  catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    RequestComplete();
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
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}


