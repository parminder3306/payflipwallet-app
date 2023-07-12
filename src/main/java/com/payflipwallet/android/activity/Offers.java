package com.payflipwallet.android.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;
import com.payflipwallet.android.json.offersJson;

import java.util.ArrayList;
import java.util.List;

public class Offers extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<offersJson> offersList = new ArrayList<offersJson>();
    private StoreAdapter mAdapter;
    RelativeLayout card_view;
    LinearLayout empty_layout;
    SwipeRefreshLayout refresh;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycler_view);
        empty_layout = findViewById(R.id.empty_layout);
        refresh = findViewById(R.id.SwipeRefreshLayout);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ShowOffers();
                        refresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        ShowOffers();
    }
    private void ShowOffers() {
        offersList.clear();
        refresh.setRefreshing(true);
        new HttpRequestTask( new HttpRequest(AppUrls.OffersUrl, HttpRequest.GET), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                refresh.setRefreshing(false);
                if (response.code == 200) {
                    if(response.body.equals("empty")){
                        empty_layout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else {
                        empty_layout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        List<offersJson> data = new Gson().fromJson(response.body, new TypeToken<List<offersJson>>() {}.getType());
                        offersList.addAll(data);
                    }
                    mAdapter = new StoreAdapter(getApplicationContext(), offersList);
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
    class StoreAdapter extends RecyclerView.Adapter<Offers.StoreAdapter.MyViewHolder> {
        private Context context;
        private List<offersJson> offersList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView of_image;

            public MyViewHolder(View view) {
                super(view);
                of_image = (ImageView) view.findViewById(R.id.of_image);
                card_view = (RelativeLayout) view.findViewById(R.id.card_view);
            }
        }


        public StoreAdapter(Context context, List<offersJson> offersList) {
            this.context = context;
            this.offersList = offersList;
        }

        @Override
        public Offers.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_offers, parent, false);

            return new Offers.StoreAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final Offers.StoreAdapter.MyViewHolder holder, final int position) {
            final offersJson offers = offersList.get(position);
            Glide.with(context).load(offers.getLogourl()).into(holder.of_image);
            card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ii=new Intent(getApplicationContext(), DealDetails.class);
                    ii.putExtra("title", offers.getTitle());
                    startActivity(ii);
                }
            });
        }
        @Override
        public int getItemCount() {
            return offersList.size();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


