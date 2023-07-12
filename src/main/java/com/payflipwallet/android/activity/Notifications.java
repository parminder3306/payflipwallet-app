package com.payflipwallet.android.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.payflipwallet.android.R;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;
import com.payflipwallet.android.json.notificationsJson;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends AppCompatActivity {
    Session session;
    private RecyclerView recyclerView;
    private List<notificationsJson> notificationsList = new ArrayList<notificationsJson>();
    private StoreAdapter mAdapter;
    SwipeRefreshLayout refresh;
    LinearLayout empty_layout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        empty_layout = (LinearLayout)findViewById(R.id.empty_layout);
        refresh =(SwipeRefreshLayout)findViewById(R.id.SwipeRefreshLayout);


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ShowData();
                        refresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        ShowData();
    }
    private void ShowData() {
        notificationsList.clear();
        refresh.setRefreshing(true);
        new HttpRequestTask( new HttpRequest(AppUrls.NotificationsUrl, HttpRequest.POST,"accesskey="+session.myaccesskey), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                refresh.setRefreshing(false);
                if (response.code == 200) {
                    if(response.body.equals("empty")){
                        empty_layout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else {
                        recyclerView.setVisibility(View.VISIBLE);
                        empty_layout.setVisibility(View.GONE);
                        List<notificationsJson> data = new Gson().fromJson(response.body, new TypeToken<List<notificationsJson>>() {}.getType());
                        notificationsList.addAll(data);
                    }
                    mAdapter = new StoreAdapter(getApplicationContext(), notificationsList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setNestedScrollingEnabled(false);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }

    private void requestDelete(final String senderid) {
        new HttpRequestTask( new HttpRequest(AppUrls.LoginUrl, HttpRequest.POST,"senderid="+senderid), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                if (response.code == 200 && response.body.equals("Success")) {
                    Toast.makeText(getApplicationContext(), "Request Successfully Cancel", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }

    class StoreAdapter extends RecyclerView.Adapter<Notifications.StoreAdapter.MyViewHolder> {
        private Context context;
        private List<notificationsJson> notificationsList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView of_message,of_amount,pay_btn,cancel_btn;

            public MyViewHolder(View view) {
                super(view);
                of_message = (TextView) view.findViewById(R.id.of_message);
                of_amount = (TextView) view.findViewById(R.id.of_amount);
                pay_btn = (TextView) view.findViewById(R.id.pay_btn);
                cancel_btn = (TextView) view.findViewById(R.id.cancel_btn);
            }
        }


        public StoreAdapter(Context context, List<notificationsJson> notificationsList) {
            this.context = context;
            this.notificationsList = notificationsList;
        }

        @Override
        public Notifications.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_notifications, parent, false);

            return new Notifications.StoreAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final Notifications.StoreAdapter.MyViewHolder holder, final int position) {
            final notificationsJson notify = notificationsList.get(position);
            holder.of_message.setText(notify.getName());
            holder.of_amount.setText(" \u20B9" + notify.getAmount());

            holder.pay_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ii = new Intent(getApplicationContext(), RequestPay.class);
                    ii.putExtra("name", notify.getName());
                    ii.putExtra("amount", notify.getAmount());
                    ii.putExtra("mobile", notify.getMobile());
                    startActivity(ii);
                }
            });
            holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestDelete(notify.getSenderid());
                    notificationsList.clear();
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
        @Override
        public int getItemCount() {
            return notificationsList.size();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


