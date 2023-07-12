package com.payflipwallet.android.activity;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.payflipwallet.android.R;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class Complete_payment extends AppCompatActivity {
    Session session;
    ProgressDialog Dialog;
    BottomSheetDialog dialog;
    RelativeLayout promo_layout, recharge_layout, account_layout;
    String get_oplogo, get_opid, get_opname, get_paid_number, get_paid_ac_number, get_paid_amount,type;
    TextView op_name, paid_amount, total_paid_amount, paid_number, paid_ac_number, wallet_balance;
    ImageView op_logo;
    Button paynow_btn;
    int addcash100 = 100, addcash500 = 500, addcash1000 = 1000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_payment);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());

        promo_layout = findViewById(R.id.promo_layout);
        recharge_layout = findViewById(R.id.recharge_layout);
        account_layout = findViewById(R.id.account_layout);
        paynow_btn = findViewById(R.id.paynow_btn);

        op_logo= findViewById(R.id.op_logo);
        op_name= findViewById(R.id.op_name);
        paid_amount= findViewById(R.id.paid_amount);
        paid_number= findViewById(R.id.paid_number);
        paid_ac_number= findViewById(R.id.paid_ac_number);
        total_paid_amount= findViewById(R.id.total_paid_amount);
        wallet_balance = findViewById(R.id.wallet_balance);

        Bundle b = getIntent().getExtras();
        get_oplogo = (String) b.get("oplogo");
        get_opid = (String) b.get("opid");
        get_opname = (String) b.get("opname");
        get_paid_number = (String) b.get("paid_number");
        get_paid_ac_number = (String) b.get("paid_ac_number");
        get_paid_amount = (String) b.get("paid_amount");
        type = (String) b.get("type");

        Glide.with(getApplicationContext()).load(AppUrls.baseUrl+"/admin/logos/"+get_oplogo+".png").placeholder(R.drawable.round_shadow_dark).into(op_logo);

        op_name.setText(get_opname+" "+get_paid_number);
        paid_amount.setText("Order Amount: "+"\u20B9"+get_paid_amount);
        paid_number.setText(get_paid_number);
        paid_ac_number.setText(get_paid_ac_number);
        total_paid_amount.setText("\u20B9" +get_paid_amount);
        wallet_balance.setText("\u20B9" +session.mywallet);

        if(get_paid_ac_number.equals("")){
            account_layout.setVisibility(View.GONE);
        }else{
            account_layout.setVisibility(View.VISIBLE);
        }

        if(get_paid_number.equals("")){
            recharge_layout.setVisibility(View.GONE);
        }else{
            recharge_layout.setVisibility(View.VISIBLE);
        }


        paynow_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                applyOffers();
            }
        });
        appliedOffers();
    }
    public void appliedOffers() {
        String postdata="apply="+type+"&amount="+get_paid_amount+"&uid="+session.myid;
        new HttpRequestTask( new HttpRequest(AppUrls.CheckOffersUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                if (response.code == 200 && response.body.length()!=0) {
                    promo_layout.setVisibility(View.VISIBLE);
                }
            }
        }).execute();

    }

    public void applyOffers() {
        String postdata="apply="+type+"&amount="+get_paid_amount+"&uid="+session.myid;
        new HttpRequestTask( new HttpRequest(AppUrls.CheckOffersUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                if (response.code == 200 && response.body.length()!=0) {
                    try {
                        JSONObject json = new JSONObject(response.body);
                        requestPayment(json.getString("promocode"),json.getString("cashback"),json.getString("openable"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestPayment("","","");
                }
            }
        }).execute();
    }

    public void requestPayment(final String promocode, final String cashback, final String openable) {
        Request("Paying...");
        String postdata="uid="+session.myid+"&type="+type+"&opid="+get_opid+"&opname="+get_opname+"&amount="+get_paid_amount+"&number="+get_paid_number+"&acnumber="+get_paid_ac_number+"&promocode="+promocode+"&cashback="+cashback+"&openable="+openable;
        new HttpRequestTask( new HttpRequest(AppUrls.WalletPayUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                RequestComplete();
                if (response.code == 200) {
                    try {
                        JSONObject json = new JSONObject(response.body);
                        if(json.getString("msg").equals("Opps")){
                            Toast.makeText(getApplicationContext(),"Server down time",Toast.LENGTH_SHORT).show();
                        }
                        else if(json.getString("msg").equals("Nobalance")){
                            addmoney();
                        }
                        else if(json.getString("msg").equals("Success")){
                            Intent SuccessActivity = new Intent(getApplicationContext(), Orders_Success.class);
                            SuccessActivity.putExtra("name", "Phone Number");
                            SuccessActivity.putExtra("paid_number", get_paid_number);
                            SuccessActivity.putExtra("paid_ac_number", get_paid_ac_number);
                            SuccessActivity.putExtra("paid_amount", get_paid_amount);
                            SuccessActivity.putExtra("date_time", json.getString("date")+" "+json.getString("time"));
                            SuccessActivity.putExtra("orderid", json.getString("orderid"));
                            SuccessActivity.putExtra("reward", json.getString("reward"));
                            startActivity(SuccessActivity);
                        }
                        else if(json.getString("msg").equals("Failure")){
                            Intent Orders_Fail = new Intent(getApplicationContext(), Orders_Fail.class);
                            Orders_Fail.putExtra("name", "Phone Number");
                            Orders_Fail.putExtra("paid_number", get_paid_number);
                            Orders_Fail.putExtra("paid_ac_number", get_paid_ac_number);
                            Orders_Fail.putExtra("paid_amount", get_paid_amount);
                            Orders_Fail.putExtra("date_time", json.getString("date")+" "+json.getString("time"));
                            Orders_Fail.putExtra("orderid", json.getString("orderid"));
                            startActivity(Orders_Fail);
                        }
                        else if(json.getString("msg").equals("Processing")){
                            Intent Orders_Pending = new Intent(getApplicationContext(), Orders_Pending.class);
                            Orders_Pending.putExtra("name", "Phone Number");
                            Orders_Pending.putExtra("paid_number", get_paid_number);
                            Orders_Pending.putExtra("paid_ac_number", get_paid_ac_number);
                            Orders_Pending.putExtra("paid_amount", get_paid_amount);
                            Orders_Pending.putExtra("date_time", json.getString("date")+" "+json.getString("time"));
                            Orders_Pending.putExtra("orderid", json.getString("orderid"));
                            Orders_Pending.putExtra("reward", json.getString("reward"));
                            startActivity(Orders_Pending);
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
        Dialog = new ProgressDialog(Complete_payment.this);
        Dialog.setMessage(message);
        Dialog.setCancelable(false);
        Dialog.show();
    }
    public void RequestComplete(){
        Dialog.dismiss();
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private void addmoney(){
        View view = getLayoutInflater().inflate(R.layout.dialog_addmoney, null);
        Button addmoney_btn = (Button) view.findViewById(R.id.addmoney_btn);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        final TextInputLayout add_amount = (TextInputLayout) view.findViewById(R.id.add_amount);
        Button plus100 = (Button)view.findViewById(R.id.plus100);
        Button plus500 = (Button)view.findViewById(R.id.plus500);
        Button plus1000 = (Button)view.findViewById(R.id.plus1000);
        plus100.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                add_amount.getEditText().setText(Integer.toString(addcash100));
                addcash100=addcash100 + 100;
            }
        });
        plus500.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                add_amount.getEditText().setText(Integer.toString(addcash500));
                addcash500=addcash500 + 500;
            }
        });
        plus1000.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                add_amount.getEditText().setText(Integer.toString(addcash1000));
                addcash1000=addcash1000 + 1000;
            }
        });

        addmoney_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                String amount = add_amount.getEditText().getText().toString();
                int amount1;
                try{
                    amount1=Integer.valueOf(amount);
                }catch (NumberFormatException nfe){
                    amount1=0;
                }
                if (amount.isEmpty()) {
                    add_amount.setError("Enter Amount");
                } else if (amount1 < 10) {
                    add_amount.setError("Amount must be more than 10");
                }
                else if (amount1 > 10000) {
                    add_amount.setError("Amount must be more than 10 to 10000");
                }
                else {
                    add_amount.setErrorEnabled(false);
                    requestProcced(amount);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = new BottomSheetDialog(Complete_payment.this);
        dialog.setContentView(view);
        dialog.show();
    }

    public void requestProcced(final String amount) {
        Intent addmoney=new Intent(getApplicationContext(), AddMoney.class);
        addmoney.putExtra("amount", amount);
        startActivity(addmoney);

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
