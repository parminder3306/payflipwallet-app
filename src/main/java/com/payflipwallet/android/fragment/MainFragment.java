package com.payflipwallet.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.payflipwallet.android.MyService;
import com.payflipwallet.android.R;

import com.payflipwallet.android.activity.AddMoney;
import com.payflipwallet.android.activity.AutoPay;
import com.payflipwallet.android.activity.DataCard;
import com.payflipwallet.android.activity.Dth;
import com.payflipwallet.android.activity.Electricity;
import com.payflipwallet.android.activity.Landline;
import com.payflipwallet.android.activity.Notifications;
import com.payflipwallet.android.activity.Offers;
import com.payflipwallet.android.activity.Recharge;
import com.payflipwallet.android.activity.RequestMoney;
import com.payflipwallet.android.activity.Rewards;
import com.payflipwallet.android.activity.SendMoney;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.helper.ViewPagerAdapter;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment implements View.OnClickListener {
    public MainFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    Session session;
    BottomSheetDialog dialog;
    CardView spotlight;
    List<String> title=new ArrayList<>();
    List<String> logourl=new ArrayList<>();
    ViewPager viewpager;
    ProgressBar progress;
    int addcash100 = 100, addcash500 = 500, addcash1000 = 1000;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.balance);
        item.setTitle("\u20B9" + session.mywallet);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.notifications) {
            startActivity(new Intent(getActivity(), Notifications.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View activity =inflater.inflate(R.layout.fragment_main, container, false);
         session = new Session(getActivity());
         session.checkLogin();
         getActivity().startService(new Intent(getActivity(), MyService.class));

         progress = (ProgressBar)activity.findViewById(R.id.progress);
         spotlight = (CardView)activity.findViewById(R.id.spotlight);
         RelativeLayout b1 = (RelativeLayout)activity.findViewById(R.id.sendmoney);
         RelativeLayout b2 = (RelativeLayout)activity.findViewById(R.id.requestmoney);
         RelativeLayout b3 = (RelativeLayout)activity.findViewById(R.id.addmoney);
         RelativeLayout b4 = (RelativeLayout)activity.findViewById(R.id.coupons);
         RelativeLayout b5 = (RelativeLayout)activity.findViewById(R.id.rewards);
         RelativeLayout b6 = (RelativeLayout)activity.findViewById(R.id.offers);
         RelativeLayout b7 = (RelativeLayout)activity.findViewById(R.id.mobile);
         RelativeLayout b8 = (RelativeLayout)activity.findViewById(R.id.datacard);
         RelativeLayout b9 = (RelativeLayout)activity.findViewById(R.id.dth);
         RelativeLayout b10 = (RelativeLayout)activity.findViewById(R.id.electricity);
         RelativeLayout b11 = (RelativeLayout)activity.findViewById(R.id.landline);
         RelativeLayout b12 = (RelativeLayout)activity.findViewById(R.id.broadband);
         RelativeLayout b13 = (RelativeLayout)activity.findViewById(R.id.googleplay);
         RelativeLayout b14 = (RelativeLayout)activity.findViewById(R.id.autopay);
         viewpager = (ViewPager) activity.findViewById(R.id.pager);
         b1.setOnClickListener(this);
         b2.setOnClickListener(this);
         b3.setOnClickListener(this);
         b4.setOnClickListener(this);
         b5.setOnClickListener(this);
         b6.setOnClickListener(this);
         b7.setOnClickListener(this);
         b8.setOnClickListener(this);
         b9.setOnClickListener(this);
         b10.setOnClickListener(this);
         b11.setOnClickListener(this);
         b12.setOnClickListener(this);
         b13.setOnClickListener(this);
         b14.setOnClickListener(this);
        ShowOffers();
        return activity;
    }
    private void ShowOffers() {
        progress.setVisibility(View.VISIBLE);
        new HttpRequestTask( new HttpRequest(AppUrls.OffersUrl, HttpRequest.GET), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                progress.setVisibility(View.GONE);
                if (response.code == 200) {
                    if(response.equals("empty")){
                        spotlight.setVisibility(View.GONE);
                    }else {
                        try {
                            JSONArray json = new JSONArray(response.body);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject data = json.getJSONObject(i);
                                title.add(data.getString("title"));
                                logourl.add(data.getString("logourl"));
                            }
                            ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(), title,logourl);
                            viewpager.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendmoney:
                startActivity(new Intent(getActivity(), SendMoney.class));
                break;
            case R.id.requestmoney:
                startActivity(new Intent(getActivity(), RequestMoney.class));
                break;
            case R.id.addmoney:
                addmoney();
                break;
            case R.id.coupons:
                startActivity(new Intent(getActivity(), Offers.class));
                break;
            case R.id.rewards:
                startActivity(new Intent(getActivity(), Rewards.class));
                break;
            case R.id.offers:
                startActivity(new Intent(getActivity(), Offers.class));
                break;
            case R.id.mobile:
                Intent mobile=new Intent(getActivity(), Recharge.class);
                mobile.putExtra("opname","");
                mobile.putExtra("opid", "");
                mobile.putExtra("oplogo", "");
                mobile.putExtra("operaror_type", "Prepaid");
                startActivity(mobile);
                break;
            case R.id.datacard:
                Intent datacard=new Intent(getActivity(), DataCard.class);
                datacard.putExtra("opname","");
                datacard.putExtra("opid", "");
                datacard.putExtra("oplogo", "");
                startActivity(datacard);
                break;
            case R.id.dth:
                Intent dth=new Intent(getActivity(), Dth.class);
                dth.putExtra("opname","");
                dth.putExtra("opid", "");
                dth.putExtra("oplogo", "");
                startActivity(dth);
                break;
            case R.id.electricity:
                Intent electricity=new Intent(getActivity(), Electricity.class);
                electricity.putExtra("opname","");
                electricity.putExtra("opid", "");
                electricity.putExtra("oplogo", "");
                startActivity(electricity);
                break;
            case R.id.landline:
                Intent landline=new Intent(getActivity(), Landline.class);
                landline.putExtra("opname","");
                landline.putExtra("opid", "");
                landline.putExtra("oplogo", "");
                startActivity(landline);
                break;
            case R.id.broadband:
                Intent broadband=new Intent(getActivity(), Landline.class);
                broadband.putExtra("opname","");
                broadband.putExtra("opid", "");
                broadband.putExtra("oplogo", "");
                startActivity(broadband);
                break;
            case R.id.googleplay:
                Toast.makeText(getActivity(), session.myname,Toast.LENGTH_SHORT).show();
                break;
            case R.id.autopay:
                Intent autopay=new Intent(getActivity(), AutoPay.class);
                autopay.putExtra("opname","");
                autopay.putExtra("opid", "");
                autopay.putExtra("operaror_type", "Prepaid");
                autopay.putExtra("operaror_type", "Prepaid");
                startActivity(autopay);
                break;
        }
    }

    private void addmoney(){
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_addmoney, null);
        Button addmoney_btn =  view.findViewById(R.id.addmoney_btn);
        TextView cancel =  view.findViewById(R.id.cancel);
        final TextInputLayout add_amount =  view.findViewById(R.id.add_amount);
        Button plus100 = view.findViewById(R.id.plus100);
        Button plus500 = view.findViewById(R.id.plus500);
        Button plus1000 = view.findViewById(R.id.plus1000);
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
                else if (amount1 > 5000) {
                    add_amount.setError("Amount must be more than 10 to 5000");
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
    private void hideKeyboard() {
        View view = getView();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public void requestProcced(final String amount) {
        Intent addmoney=new Intent(getActivity(), AddMoney.class);
        addmoney.putExtra("amount", amount);
        startActivity(addmoney);

    }
}


