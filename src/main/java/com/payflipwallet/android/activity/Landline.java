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

public class Landline extends AppCompatActivity {
    Button continue_btn;
    String op_logo, op_id, op_name, account_open="false";
    TextInputLayout landline_no, landline_operator, landline_amount, landline_account_no;
    EditText landline_operator_edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landline);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        landline_no =  findViewById(R.id.landline_no);
        landline_operator =  findViewById(R.id.landline_operator);
        landline_amount =  findViewById(R.id.landline_amount);
        landline_account_no =  findViewById(R.id.landline_account_no);
        landline_operator_edit = findViewById(R.id.landline_operator_edit);
        continue_btn = findViewById(R.id.continue_btn);

        Bundle b = getIntent().getExtras();
        op_logo =(String) b.get("oplogo");
        op_id =(String) b.get("opid");
        op_name =(String) b.get("opname");
        landline_operator.getEditText().setText(op_name);

        if(op_logo.equals("bsnl")) {
            landline_account_no.setVisibility(View.VISIBLE);
            account_open="true";
        }

        landline_operator_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ii=new Intent(getApplicationContext(), Operators.class);
                ii.putExtra("operaror_type", "Landline");
                ii.putExtra("autopay", "No");
                startActivity(ii);
            }
        });

        continue_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                String landlinenovaild = "false";
                String operatorvaild = "false";
                String amountvaild = "false";
                String landlineno = landline_no.getEditText().getText().toString();
                String landline_accountno = landline_account_no.getEditText().getText().toString();
                String amount = landline_amount.getEditText().getText().toString();
                String operator=landline_operator.getEditText().getText().toString();
                int amount1;
                try{
                    amount1=Integer.valueOf(amount);
                }catch (NumberFormatException nfe){
                    amount1=0;
                }
                if (landlineno.isEmpty()) {
                    landline_no.setError("Enter Phone Number");
                } else if (landlineno.length() < 10) {
                    landline_no.setError("Enter vaild Phone Number");
                } else {
                    landline_no.setErrorEnabled(false);
                    landlinenovaild = "true";
                }

                if(account_open.equals("true")) {
                    if (landline_accountno.isEmpty()) {
                        landline_account_no.setError("Account Number is required");
                    } else if (landline_accountno.length() < 4) {
                        landline_account_no.setError("Account Number must be minimum 4 to 16 characters");
                    } else {
                        landline_account_no.setErrorEnabled(false);
                    }
                }

                if (operator.isEmpty()) {
                    landline_operator.setError("Select Operator");
                }else{
                    landline_operator_edit.setText(op_name);
                    operatorvaild = "true";
                }

                if (amount.isEmpty()) {
                    landline_amount.setError("Enter Amount");
                } else if (amount1 < 100) {
                    landline_amount.setError("Amount must be more than 100");
                }
                else if (amount1 > 5000) {
                    landline_amount.setError("Amount must be more than 100 to 5000");
                }
                else {
                    landline_amount.setErrorEnabled(false);
                    amountvaild = "true";
                }
                if (landlinenovaild.equals("true") && amountvaild.equals("true") && operatorvaild.equals("true")) {
                    requestRecharge(landlineno,landline_accountno,amount);
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
    private void requestRecharge(String paid_number,String paid_ac_number,String paid_amount){
        Intent ii=new Intent(getApplicationContext(), Complete_payment.class);
        ii.putExtra("oplogo", op_logo);
        ii.putExtra("opid", op_id);
        ii.putExtra("opname", op_name);
        ii.putExtra("paid_number", paid_number);
        ii.putExtra("paid_ac_number", paid_ac_number);
        ii.putExtra("paid_amount", paid_amount);
        ii.putExtra("type", "Landline");
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




