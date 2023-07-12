package com.payflipwallet.android.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.payflipwallet.android.R;

public class Recharge extends AppCompatActivity {
    Button continue_btn;
    String op_logo, op_id, op_name, operaror_type="Prepaid", get_operaror_type;
    TextInputLayout mobile_no, mobile_operator, mobile_amount;
    EditText mobile_operator_edit;
    RadioButton prepaid_radio, postpaid_radio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mobile_no =  findViewById(R.id.mobile_no);
        mobile_operator =  findViewById(R.id.mobile_operator);
        mobile_amount =  findViewById(R.id.mobile_amount);
        mobile_operator_edit = findViewById(R.id.mobile_operator_edit);
        prepaid_radio = findViewById(R.id.prepaid_radio);
        postpaid_radio =  findViewById(R.id.postpaid_radio);
        continue_btn = findViewById(R.id.continue_btn);

        Bundle b = getIntent().getExtras();
        op_logo =(String) b.get("oplogo");
        op_id =(String) b.get("opid");
        op_name =(String) b.get("opname");
        get_operaror_type =(String) b.get("operaror_type");
        mobile_operator.getEditText().setText(op_name);

        if(get_operaror_type.equals("Prepaid")){
            prepaid_radio.setChecked(true);
            postpaid_radio.setChecked(false);
        }else{
            prepaid_radio.setChecked(false);
            postpaid_radio.setChecked(true);
        }

        prepaid_radio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prepaid_radio.setChecked(true);
                postpaid_radio.setChecked(false);
                operaror_type="Prepaid";
            }
        });
        postpaid_radio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prepaid_radio.setChecked(false);
                postpaid_radio.setChecked(true);
                operaror_type="Postpaid";
            }
        });

        mobile_operator_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ii=new Intent(getApplicationContext(), Operators.class);
                ii.putExtra("operaror_type", operaror_type);
                ii.putExtra("autopay", "No");
                startActivity(ii);
            }
        });

        continue_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                String mobilevaild = "false";
                String amountvaild = "false";
                String operatorvaild = "false";
                String mobile = mobile_no.getEditText().getText().toString();
                String amount = mobile_amount.getEditText().getText().toString();
                String operator=mobile_operator.getEditText().getText().toString();
                int amount1;
                try{
                    amount1=Integer.valueOf(amount);
                }catch (NumberFormatException nfe){
                    amount1=0;
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
                }else{
                    mobile_operator_edit.setText(op_name);
                    operatorvaild = "true";
                }

                if (amount.isEmpty()) {
                    mobile_amount.setError("Enter Amount");
                } else if (amount1 < 10) {
                    mobile_amount.setError("Amount must be more than 10");
                }
                else if (amount1 > 3000) {
                    mobile_amount.setError("Amount must be more than 10 to 3000");
                }
                else {
                    mobile_amount.setErrorEnabled(false);
                    amountvaild = "true";
                }
                if (mobilevaild.equals("true") && amountvaild.equals("true") && operatorvaild.equals("true")) {
                    requestRecharge(mobile,amount);
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
    private void requestRecharge(String mobile,String amount){
        Intent ii=new Intent(getApplicationContext(), Complete_payment.class);
        ii.putExtra("oplogo", op_logo);
        ii.putExtra("opid", op_id);
        ii.putExtra("opname", op_name);
        ii.putExtra("paid_number", mobile);
        ii.putExtra("paid_ac_number", "");
        ii.putExtra("paid_amount", amount);
        ii.putExtra("type", "Recharge");
        startActivity(ii);
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




