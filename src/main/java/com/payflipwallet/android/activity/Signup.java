package com.payflipwallet.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    ProgressDialog Dialog;
    Button signup_btn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final TextInputLayout enter_name =  findViewById(R.id.enter_name);
        final TextInputLayout enter_mobile =  findViewById(R.id.enter_mobile);
        final TextInputLayout enter_email =  findViewById(R.id.enter_email);
        final TextInputLayout enter_password = findViewById(R.id.enter_password);
        signup_btn = (Button) findViewById(R.id.signup_btn);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                String namevaild = "false";
                String mobilevaild = "false";
                String emailvaild = "false";
                String passwordvaild = "false";
                String name = enter_name.getEditText().getText().toString();
                String mobile = enter_mobile.getEditText().getText().toString();
                String email = enter_email.getEditText().getText().toString();
                String password = enter_password.getEditText().getText().toString();

                if (name.isEmpty()) {
                    enter_name.setError("Enter your Full Name");
                } else if (name.length() < 6) {
                    enter_name.setError("Please enter at least 6 characters.");
                } else {
                    enter_name.setErrorEnabled(false);
                    namevaild = "true";
                }
                if (mobile.isEmpty()) {
                    enter_mobile.setError("Enter your mobile number");
                } else if (mobile.length() < 10) {
                    enter_mobile.setError("Enter vaild mobile number");
                } else {
                    enter_mobile.setErrorEnabled(false);
                    mobilevaild = "true";
                }
                if(email.isEmpty()){
                    enter_email.setError("Enter Your Email ID");
                }
                else if (!validateEmail(email)) {
                    enter_email.setError("Enter Vaild Email ID.");
                }else{
                    enter_email.setErrorEnabled(false);
                    emailvaild = "true";
                }
                if (password.isEmpty()) {
                    enter_password.setError("Enter New Password");
                } else if (password.length() < 6) {
                    enter_password.setError("Please enter at least 6 characters.");
                } else {
                    enter_password.setErrorEnabled(false);
                    passwordvaild = "true";
                }
                if (namevaild.equals("true") && mobilevaild.equals("true") && emailvaild.equals("true") && passwordvaild.equals("true")) {
                    requestSignup(name,mobile,email,password);
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
    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public void requestSignup(final String name, final String mobile, final String email, final String password) {
        Request("Creating...");
        String postdata="name="+name+"&mobile="+mobile+"&email="+email+"&password="+password+"&serial="+Build.SERIAL;
        new HttpRequestTask( new HttpRequest(AppUrls.SignUpUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                RequestComplete();
                if (response.code == 200) {
                    try {
                        JSONObject json = new JSONObject(response.body);
                        if(json.getString("msg").equals("success")){
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        }else{
                            Snackbar.make(getCurrentFocus(), json.getString("msg"), Snackbar.LENGTH_LONG).show();
                        }
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

