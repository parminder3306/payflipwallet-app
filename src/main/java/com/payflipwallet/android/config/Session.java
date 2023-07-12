package com.payflipwallet.android.config;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.payflipwallet.android.activity.Login;

import org.json.JSONException;
import org.json.JSONObject;

public class Session {
	SharedPreferences pref;
	Context context;
	public Editor editor;
	private static final String IS_LOGIN = "IsLoggedIn";
	public static final String UserData = "userdata";
	public static final String Operators = "operators";
    public String myid, myaccesskey, mywincash, myspendmoney, mywallet, myrefercode, myname, myemail, mymobile,myoperators;

	public Session(Context context){
		this.context = context;
		pref = context.getSharedPreferences("PayflipWallet", 0);
		editor = pref.edit();
        myData();
	}

	public void createLoginSession(String data){
            editor.putBoolean(IS_LOGIN, true);
            editor.putString(UserData,data);
            editor.putString(Operators,null);
            editor.commit();
	}
	public void checkLogin(){
		if(!this.isLoggedIn()){
			Intent i = new Intent(context, Login.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
		}
		
	}
	private void myData(){
	    myoperators=pref.getString(Operators, "");
            try {
                JSONObject json = new JSONObject(pref.getString(UserData, ""));
                 myid=json.getString("uid");
                 myaccesskey=json.getString("accesskey");
                 mywincash=json.getString("wincash");
                 myspendmoney=json.getString("spendmoney");
                 mywallet=json.getString("wallet");
                 myrefercode=json.getString("refercode");
                 myname=json.getString("name");
                 myemail=json.getString("email");
                 mymobile=json.getString("mobile");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }


	public void logoutUser(){
		editor.clear();
		editor.commit();
		Intent i = new Intent(context, Login.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
	}
	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}

	}
