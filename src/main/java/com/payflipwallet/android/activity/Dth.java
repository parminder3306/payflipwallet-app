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

public class Dth extends AppCompatActivity {
    Button continue_btn;
    String op_logo, op_id, op_name;
    TextInputLayout dth_no, dth_operator, dth_amount;
    EditText dth_operator_edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dth);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dth_no =  findViewById(R.id.dth_no);
        dth_operator = findViewById(R.id.dth_operator);
        dth_amount =  findViewById(R.id.dth_amount);
        dth_operator_edit =  findViewById(R.id.dth_operator_edit);
        continue_btn = findViewById(R.id.continue_btn);

        Bundle b = getIntent().getExtras();
        op_logo =(String) b.get("oplogo");
        op_id =(String) b.get("opid");
        op_name =(String) b.get("opname");
        dth_operator.getEditText().setText(op_name);

        dth_operator_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ii=new Intent(getApplicationContext(), Operators.class);
                ii.putExtra("operaror_type", "DTH");
                ii.putExtra("autopay", "No");
                startActivity(ii);
            }
            });

        continue_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                String dthnovaild = "false";
                String operatorvaild = "false";
                String amountvaild = "false";
                String dthno = dth_no.getEditText().getText().toString();
                String amount = dth_amount.getEditText().getText().toString();
                String operator=dth_operator.getEditText().getText().toString();
                int amount1;
                try{
                    amount1=Integer.valueOf(amount);
                }catch (NumberFormatException nfe){
                    amount1=0;
                }
                if (dthno.isEmpty()) {
                    dth_no.setError("Enter DTH Number");
                } else if (dthno.length() < 6) {
                    dth_no.setError("Enter vaild DTH Number");
                } else {
                    dth_no.setErrorEnabled(false);
                    dthnovaild = "true";
                }

                if (operator.isEmpty()) {
                    dth_operator.setError("Select Operator");
                }else{
                    dth_operator_edit.setText(op_name);
                    operatorvaild = "true";
                }

                if (amount.isEmpty()) {
                    dth_amount.setError("Enter Amount");
                } else if (amount1 < 100) {
                    dth_amount.setError("Amount must be more than 100");
                }
                else if (amount1 > 5000) {
                    dth_amount.setError("Amount must be more than 100 to 5000");
                }
                else {
                    dth_amount.setErrorEnabled(false);
                    amountvaild = "true";
                }
                if (dthnovaild.equals("true") && amountvaild.equals("true") && operatorvaild.equals("true")) {
                    requestRecharge(dthno,amount);
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
        ii.putExtra("paid_number", paid_number);
        ii.putExtra("paid_ac_number", "");
        ii.putExtra("paid_amount", paid_amount);
        ii.putExtra("type", "DTH");
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




