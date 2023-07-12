package com.payflipwallet.android.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.payflipwallet.android.R;


public class Electricity extends AppCompatActivity {
    Button continue_btn;
    String op_logo, op_id, op_name;
    TextInputLayout electricity_operator, electricity_no, electricity_amount;
    EditText electricity_operator_edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.electricity);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        electricity_no = findViewById(R.id.electricity_no);
        electricity_operator = findViewById(R.id.electricity_operator);
        electricity_amount = findViewById(R.id.electricity_amount);
        electricity_operator_edit = findViewById(R.id.electricity_operator_edit);
        continue_btn = findViewById(R.id.continue_btn);
        Bundle b = getIntent().getExtras();
        op_logo =(String) b.get("oplogo");
        op_id =(String) b.get("opid");
        op_name =(String) b.get("opname");
        electricity_operator.getEditText().setText(op_name);

        electricity_operator_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ii=new Intent(getApplicationContext(), Operators.class);
                ii.putExtra("operaror_type", "Electricity");
                ii.putExtra("autopay", "No");
                startActivity(ii);
            }
        });

        continue_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                String accountnovaild = "false";
                String amountvaild = "false";
                String operatorvaild = "true";
                String accountno = electricity_no.getEditText().getText().toString();
                String amount = electricity_amount.getEditText().getText().toString();
                String operator=electricity_operator.getEditText().getText().toString();
                int amount1;
                try{
                    amount1=Integer.valueOf(amount);
                }catch (NumberFormatException nfe){
                    amount1=0;
                }
                if (accountno.isEmpty()) {
                    electricity_no.setError("Enter Account Number");
                } else if (accountno.length() < 10) {
                    electricity_no.setError("Enter vaild Account Number");
                } else {
                    electricity_no.setErrorEnabled(false);
                    accountnovaild = "true";
                }

                if (operator.isEmpty()) {
                    electricity_operator.setError("Select Operator");
                }else{
                    electricity_operator_edit.setText(op_name);
                    operatorvaild = "true";
                }

                if (amount.isEmpty()) {
                    electricity_amount.setError("Enter Amount");
                } else if (amount1 < 100) {
                    electricity_amount.setError("Amount must be more than 100");
                }
                else if (amount1 > 5000) {
                    electricity_amount.setError("Amount must be more than 100 to 10000");
                }
                else {
                    electricity_amount.setErrorEnabled(false);
                    amountvaild = "true";
                }
                if (accountnovaild.equals("true") && amountvaild.equals("true") && operatorvaild.equals("true")) {
                    requestRecharge(accountno,amount);
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

    private void requestRecharge(String paid_number,String paid_amount){
        Intent ii=new Intent(getApplicationContext(), Complete_payment.class);
        ii.putExtra("oplogo", op_logo);
        ii.putExtra("opid", op_id);
        ii.putExtra("opname", op_name);
        ii.putExtra("paid_number", "");
        ii.putExtra("paid_ac_number", paid_number);
        ii.putExtra("paid_amount", paid_amount);
        ii.putExtra("type", "Electricity");
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




