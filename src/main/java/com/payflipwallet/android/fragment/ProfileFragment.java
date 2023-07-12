package com.payflipwallet.android.fragment;

import android.app.ProgressDialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.payflipwallet.android.R;
import com.payflipwallet.android.activity.AboutUs;
import com.payflipwallet.android.activity.ChangePassword;
import com.payflipwallet.android.activity.MyQr;
import com.payflipwallet.android.activity.ReferEarn;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    public ProfileFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    Session session;
    ProgressDialog Dialog;
    Button update_btn;
    TextView my_name,my_name1,my_mobile,my_email;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.password) {
            Intent ii = new Intent(getActivity(), ChangePassword.class);
            startActivity(ii);
            return true;
        }
        if (id == R.id.logout) {
            session.logoutUser();
            return true;
        }
        if (id == R.id.about) {
            Intent ii = new Intent(getActivity(), AboutUs.class);
            startActivity(ii);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profile = inflater.inflate(R.layout.fragment_profile, container, false);
        session = new Session(getActivity());

        final TextInputLayout enter_name =  profile.findViewById(R.id.enter_name);
        my_name= profile.findViewById(R.id.my_name);
        my_name1= profile.findViewById(R.id.my_name1);
        my_mobile= profile.findViewById(R.id.my_mobile);
        my_email= profile.findViewById(R.id.my_email);
        update_btn =  profile.findViewById(R.id.update_btn);
        RelativeLayout b1 =  profile.findViewById(R.id.myqr_btn);
        RelativeLayout b2 =  profile.findViewById(R.id.referral_btn);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

        update_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard();
                 String editname = enter_name.getEditText().getText().toString();
                if (editname.isEmpty()) {
                    enter_name.setError("Enter your name");
                } else if (editname.length() < 6) {
                    enter_name.setError("Please enter at least 6 characters.");
                }
                else {
                    enter_name.setErrorEnabled(false);
                    requestUpdate(editname);
                }
            }
        });
        my_name.setText(session.myname);
        my_name1.setText(session.myname);
        my_mobile.setText(session.mymobile);
        my_email.setText(session.myemail);
        return profile;
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myqr_btn:
                startActivity(new Intent(getActivity(), MyQr.class));
                break;
            case R.id.referral_btn:
                startActivity(new Intent(getActivity(), ReferEarn.class));
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
    public void requestUpdate(final String name) {
        Request("Updating...");
        new HttpRequestTask( new HttpRequest(AppUrls.ProfileUrl, HttpRequest.POST,"uid="+session.myid+"&name="+name), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                RequestComplete();
                if (response.code == 200) {
                    try {
                    JSONObject json = new JSONObject(response.body);
                    Toast.makeText(getActivity(),json.getString("msg"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
        }
    public void Request(String message){
        Dialog = new ProgressDialog(getActivity());
        Dialog.setMessage(message);
        Dialog.setCancelable(false);
        Dialog.show();
    }
    public void RequestComplete(){
        Dialog.dismiss();
    }
    }


