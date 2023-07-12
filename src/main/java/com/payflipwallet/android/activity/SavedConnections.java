package com.payflipwallet.android.activity;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.payflipwallet.android.R;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;
import com.payflipwallet.android.json.connectionsJson;

import java.util.ArrayList;
import java.util.List;

public class SavedConnections extends AppCompatActivity {
    Session session;
    private RecyclerView recyclerView;
    private List<connectionsJson> connectionsList = new ArrayList<connectionsJson>();
    private StoreAdapter mAdapter;
    SwipeRefreshLayout refresh;
    LinearLayout empty_layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savedconnections);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());

        recyclerView = findViewById(R.id.recycler_view);
        empty_layout = findViewById(R.id.empty_layout);
        refresh = findViewById(R.id.SwipeRefreshLayout);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestSavedconnections();
                        refresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        requestSavedconnections();
    }

    private void requestDelete(final String mobile) {
        new HttpRequestTask( new HttpRequest(AppUrls.SavedConnectionsUrl, HttpRequest.POST,"mobile="+mobile), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                if (response.code == 200 && response.body.equals("Success")) {
                    Toast.makeText(getApplicationContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }
    private void requestSavedconnections() {
        connectionsList.clear();
        refresh.setRefreshing(true);
        new HttpRequestTask( new HttpRequest(AppUrls.SavedConnectionsUrl, HttpRequest.POST,"uid="+session.myid), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                refresh.setRefreshing(false);
                if (response.code == 200) {
                    if(response.body.equals("empty")){
                        empty_layout.setVisibility(View.VISIBLE);
                    }else{
                        recyclerView.setVisibility(View.VISIBLE);
                        List<connectionsJson> data = new Gson().fromJson(response.body, new TypeToken<List<connectionsJson>>() {}.getType());
                        connectionsList.addAll(data);
                    }
                    mAdapter = new StoreAdapter(getApplicationContext(), connectionsList);
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

    class StoreAdapter extends RecyclerView.Adapter<SavedConnections.StoreAdapter.MyViewHolder> {
        private Context context;
        private List<connectionsJson> connectionsList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView of_opname,of_mobile,of_amount,of_date,delete_btn;
            public ImageView of_oplogo;

            public MyViewHolder(View view) {
                super(view);
                of_oplogo = (ImageView) view.findViewById(R.id.of_oplogo);
                of_opname = (TextView) view.findViewById(R.id.of_opname);
                of_mobile = (TextView) view.findViewById(R.id.of_mobile);
                of_amount = (TextView) view.findViewById(R.id.of_amount);
                of_date = (TextView) view.findViewById(R.id.of_date);
                delete_btn = (TextView) view.findViewById(R.id.delete_btn);
            }
        }


        public StoreAdapter(Context context, List<connectionsJson> connectionsList) {
            this.context = context;
            this.connectionsList = connectionsList;
        }

        @Override
        public SavedConnections.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_connections, parent, false);
            return new SavedConnections.StoreAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final SavedConnections.StoreAdapter.MyViewHolder holder, final int position) {
            final connectionsJson connections = connectionsList.get(position);
            Glide.with(context).load("http://192.168.43.175/admin/logos/"+connections.getOplogo()+".png").placeholder(R.drawable.round_shadow_dark).into(holder.of_oplogo);
            holder.of_opname.setText(connections.getOpname()+" "+connections.getType());
            holder.of_mobile.setText(connections.getMobile());
            holder.of_date.setText("DATE: "+connections.getDate());
            holder.of_amount.setText(" \u20B9" + connections.getAmount());

            holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestDelete(connections.getMobile());
                    requestSavedconnections();
                }
            });
        }
        @Override
        public int getItemCount() {
            return connectionsList.size();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


