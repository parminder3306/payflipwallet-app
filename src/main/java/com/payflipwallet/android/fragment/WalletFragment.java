package com.payflipwallet.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.payflipwallet.android.R;
import com.payflipwallet.android.activity.AddMoney;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.activity.Voucher;


public class WalletFragment extends Fragment implements View.OnClickListener{
    public WalletFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    Session session;
    TextView my_balance, my_spend;
    BottomSheetDialog dialog;
    int addcash100 = 100, addcash500 = 500, addcash1000 = 1000;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View wallet = inflater.inflate(R.layout.fragment_wallet, container, false);
        session = new Session(getActivity());
        my_balance= wallet.findViewById(R.id.my_balance);
        my_spend= wallet.findViewById(R.id.my_spend);
        my_balance.setText("\u20B9" +session.mywallet);
        my_spend.setText("\u20B9" +session.myspendmoney);

        Button b1 =  wallet.findViewById(R.id.addmoney_btn);
        Button b2 =  wallet.findViewById(R.id.addpromo_btn);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        return wallet;

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addpromo_btn:
                startActivity(new Intent(getActivity(), Voucher.class));
                break;
            case R.id.addmoney_btn:
                addmoney();
                break;
        }
    }
    private void hideKeyboard() {
        View view = getView();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private void addmoney(){
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_addmoney, null);
        Button addmoney_btn = (Button) view.findViewById(R.id.addmoney_btn);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        final TextInputLayout add_amount =  view.findViewById(R.id.add_amount);
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

        dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(view);
        dialog.show();
    }

    public void requestProcced(final String amount) {
        Intent addmoney=new Intent(getActivity(), AddMoney.class);
        addmoney.putExtra("amount", amount);
        startActivity(addmoney);

    }
}


