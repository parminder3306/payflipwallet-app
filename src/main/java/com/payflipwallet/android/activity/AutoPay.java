package com.payflipwallet.android.activity;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
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

import java.util.Calendar;

public class AutoPay extends AppCompatActivity {
    Session session;
    ProgressDialog Dialog;
    Button add_btn, saved_connection_btn;
    String op_logo, operator, op_id, op_name, mobile, amount, date, operaror_type = "Prepaid", get_operaror_type;
    TextInputLayout mobile_no, mobile_operator, mobile_amount, date_pick;
    EditText mobile_operator_edit, date_pick_edit;
    RadioButton prepaid_radio, postpaid_radio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autopay);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());

        mobile_no =  findViewById(R.id.mobile_no);
        mobile_operator =  findViewById(R.id.mobile_operator);
        mobile_amount =  findViewById(R.id.mobile_amount);
        date_pick =  findViewById(R.id.date_pick);
        mobile_operator_edit =  findViewById(R.id.mobile_operator_edit);
        date_pick_edit =  findViewById(R.id.date_pick_edit);
        prepaid_radio =  findViewById(R.id.prepaid_radio);
        postpaid_radio =  findViewById(R.id.postpaid_radio);
        add_btn =  findViewById(R.id.add_btn);
        saved_connection_btn =  findViewById(R.id.saved_connection_btn);


        Bundle b = getIntent().getExtras();
        op_logo = (String) b.get("oplogo");
        op_id = (String) b.get("opid");
        op_name = (String) b.get("opname");
        get_operaror_type = (String) b.get("operaror_type");
        mobile_operator.getEditText().setText(op_name);

        if (get_operaror_type.equals("Prepaid")) {
            prepaid_radio.setChecked(true);
            postpaid_radio.setChecked(false);
        } else {
            prepaid_radio.setChecked(false);
            postpaid_radio.setChecked(true);
        }

        prepaid_radio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prepaid_radio.setChecked(true);
                postpaid_radio.setChecked(false);
                operaror_type = "Prepaid";
            }
        });
        postpaid_radio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prepaid_radio.setChecked(false);
                postpaid_radio.setChecked(true);
                operaror_type = "Postpaid";
            }
        });

        saved_connection_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SavedConnections.class));
            }
        });
        mobile_operator_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ii = new Intent(getApplicationContext(), Operators.class);
                ii.putExtra("operaror_type", operaror_type);
                ii.putExtra("autopay", "Yes");
                startActivity(ii);
            }
        });
        date_pick_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(999);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                String mobilevaild = "false";
                String amountvaild = "false";
                String operatorvaild = "false";
                String datevaild = "false";
                mobile = mobile_no.getEditText().getText().toString();
                amount = mobile_amount.getEditText().getText().toString();
                date = date_pick.getEditText().getText().toString();
                operator = mobile_operator.getEditText().getText().toString();
                int amount1;
                try {
                    amount1 = Integer.valueOf(amount);
                } catch (NumberFormatException nfe) {
                    amount1 = 0;
                }
                if (mobile.isEmpty()) {
                    mobile_no.setError("Enter Mobile Number");
                } else if (mobile.length() < 10) {
                    mobile_no.setError("Enter vaild mobile number");
                } else {
                    mobile_no.setErrorEnabled(false);
                    mobilevaild = "true";
                }

                if (operator.isEmpty()) {
                    mobile_operator.setError("Select Operator");
                } else {
                    mobile_operator_edit.setText(op_name);
                    operatorvaild = "true";
                }
                if (date.isEmpty()) {
                    date_pick.setError("Select Date");
                } else {
                    date_pick.setErrorEnabled(false);
                    datevaild = "true";
                }

                if (amount.isEmpty()) {
                    mobile_amount.setError("Enter Amount");
                } else if (amount1 < 10) {
                    mobile_amount.setError("Amount must be more than 10");
                } else if (amount1 > 3000) {
                    mobile_amount.setError("Amount must be more than 10 to 3000");
                } else {
                    mobile_amount.setErrorEnabled(false);
                    amountvaild = "true";
                }
                if (mobilevaild.equals("true") && amountvaild.equals("true") && operatorvaild.equals("true") && datevaild.equals("true")) {
                    Autopay();
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

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        String fm=""+month;
        String fd=""+day;
        if(month<10){
            fm ="0"+month;
        }
        if (day<10){
            fd="0"+day;
        }
        date_pick_edit.setText(fd + "-" + fm  + "-" + year);
    }

    private void Autopay() {
        Request("Please Wait...");
        String postdata = "uid="+ session.myid+"&opid="+op_id+"&opname="+op_name+"&oplogo="+op_logo+"&mobile="+mobile+"&amount="+amount+"&date="+date+"&type="+get_operaror_type;
        new HttpRequestTask(new HttpRequest(AppUrls.AutoPayUrl, HttpRequest.POST, postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                if (response.code == 200) {
                    RequestComplete();
                    try {
                        JSONObject json = new JSONObject(response.body);
                        Snackbar.make(getCurrentFocus(), json.getString("msg"), Snackbar.LENGTH_LONG).show();
                    } catch (JSONException e) {
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
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}




