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

public class DataCard extends AppCompatActivity {
    Button continue_btn;
    String op_logo, op_id, op_name;
    TextInputLayout datacard_no, datacard_operator, datacard_amount;
    EditText datacard_operator_edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datacard);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        datacard_no =  findViewById(R.id.datacard_no);
        datacard_operator =  findViewById(R.id.datacard_operator);
        datacard_amount =  findViewById(R.id.datacard_amount);
        datacard_operator_edit =  findViewById(R.id.datacard_operator_edit);
        continue_btn =  findViewById(R.id.continue_btn);

        Bundle b = getIntent().getExtras();
        op_logo =(String) b.get("oplogo");
        op_id =(String) b.get("opid");
        op_name =(String) b.get("opname");
        datacard_operator.getEditText().setText(op_name);

        datacard_operator_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ii=new Intent(getApplicationContext(), Operators.class);
                ii.putExtra("operaror_type", "Datacard");
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
                String mobile = datacard_no.getEditText().getText().toString();
                String amount = datacard_amount.getEditText().getText().toString();
                String operator=datacard_operator.getEditText().getText().toString();
                int amount1;
                try{
                    amount1=Integer.valueOf(amount);
                }catch (NumberFormatException nfe){
                    amount1=0;
                }
                if (mobile.isEmpty()) {
                    datacard_no.setError("Enter Mobile Number");
                } else if (mobile.length() < 10) {
                    datacard_no.setError("Enter vaild mobile number");
                } else {
                    datacard_no.setErrorEnabled(false);
                    mobilevaild = "true";
                }

                if (operator.isEmpty()) {
                    datacard_operator.setError("Select Operator");
                }else{
                    datacard_operator_edit.setText(op_name);
                    operatorvaild = "true";
                }

                if (amount.isEmpty()) {
                    datacard_amount.setError("Enter Amount");
                } else if (amount1 < 10) {
                    datacard_amount.setError("Amount must be more than 10");
                }
                else if (amount1 > 3000) {
                    datacard_amount.setError("Amount must be more than 10 to 3000");
                }
                else {
                    datacard_amount.setErrorEnabled(false);
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




