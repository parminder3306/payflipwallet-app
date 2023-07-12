package com.payflipwallet.android.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.payflipwallet.android.R;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.json.operatorsJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Operators extends AppCompatActivity {
    Session session;
    private RecyclerView recyclerView;
    private List<operatorsJson> OperatorsList = new ArrayList<operatorsJson>();
    private StoreAdapter mAdapter;
    public RelativeLayout operator_select;
    String operaror_type, autopay;
    Intent activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operators);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session= new Session(getApplicationContext());

        Bundle b = getIntent().getExtras();
        operaror_type =(String) b.get("operaror_type");
        autopay =(String) b.get("autopay");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mAdapter = new StoreAdapter(getApplicationContext(), OperatorsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        rquestOperators(session.myoperators);

    }
    private void rquestOperators(String json) {
                try {
                    JSONObject jSONObject = new JSONObject(json);
                    JSONArray response = jSONObject.getJSONArray(operaror_type);
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        operatorsJson data = new operatorsJson();
                        data.setOplogo(obj.getString("oplogo"));
                        data.setOpname(obj.getString("opname"));
                        data.setOpid(obj.getString("opid"));
                        OperatorsList.add(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();

    }
    class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {
        private Context context;
        private List<operatorsJson> OperatorsList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView op_name;
            public ImageView op_logo;

            public MyViewHolder(View view) {
                super(view);
                op_name = (TextView) view.findViewById(R.id.op_name);
                op_logo = (ImageView) view.findViewById(R.id.op_logo);
                operator_select=(RelativeLayout)view.findViewById(R.id.operator_select);
            }
        }


        public StoreAdapter(Context context, List<operatorsJson> OperatorsList) {
            this.context = context;
            this.OperatorsList = OperatorsList;
        }

        @Override
        public StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_operators, parent, false);

            return new StoreAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(StoreAdapter.MyViewHolder holder, final int position) {
            final operatorsJson string = OperatorsList.get(position);
            Glide.with(context).load(AppUrls.baseUrl+"/admin/logos/"+string.getOplogo()+".png").placeholder(R.drawable.round_shadow_dark).into(holder.op_logo);
            holder.op_name.setText(string.getOpname());

            operator_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (operaror_type.equals("DTH") && autopay.equals("No")){
                        activity=new Intent(getApplicationContext(), Dth.class);

                    }if (operaror_type.equals("Electricity") && autopay.equals("No")){
                        activity=new Intent(getApplicationContext(), Electricity.class);
                    }
                    if (operaror_type.equals("Prepaid") && autopay.equals("Yes")){
                        activity=new Intent(getApplicationContext(), AutoPay.class);
                    }
                    if (operaror_type.equals("Postpaid") && autopay.equals("Yes")){
                        activity=new Intent(getApplicationContext(), AutoPay.class);
                    }
                    if (operaror_type.equals("Prepaid") && autopay.equals("No")){
                        activity=new Intent(getApplicationContext(), Recharge.class);
                    }
                    if (operaror_type.equals("Postpaid") && autopay.equals("No")){
                        activity=new Intent(getApplicationContext(), Recharge.class);
                    }
                    if (operaror_type.equals("Datacard") && autopay.equals("No")){
                        activity=new Intent(getApplicationContext(), DataCard.class);
                    }
                    if (operaror_type.equals("Landline") && autopay.equals("No")){
                        activity=new Intent(getApplicationContext(), Landline.class);
                    }

                    activity.putExtra("opname", string.getOpname());
                    activity.putExtra("opid", string.getOpid());
                    activity.putExtra("oplogo", string.getOplogo());
                    activity.putExtra("operaror_type", operaror_type);
                    startActivity(activity);
                }
            });
        }
        @Override
        public int getItemCount() {
            return OperatorsList.size();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


