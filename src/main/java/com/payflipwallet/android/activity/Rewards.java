package com.payflipwallet.android.activity;


import android.app.Dialog;
import android.content.Context;;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.payflipwallet.android.R;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.helper.ScratchCard;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;
import com.payflipwallet.android.json.rewardsJson;

import java.util.ArrayList;
import java.util.List;

public class Rewards extends AppCompatActivity {
    Session session;
    private RecyclerView recyclerView;
    private List<rewardsJson> rewardsList = new ArrayList<rewardsJson>();
    private StoreAdapter mAdapter;
    SwipeRefreshLayout refresh;
    RelativeLayout scratch_card;
    LinearLayout empty_layout;
    TextView wincash;
    ImageView cashback_scratch_background;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewards);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());

        recyclerView = findViewById(R.id.recycler_view);
        empty_layout = findViewById(R.id.empty_layout);
        wincash= findViewById(R.id.wincash);
        refresh = findViewById(R.id.SwipeRefreshLayout);

        wincash.setText("\u20B9" +session.mywincash);
        requestRewards();
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestRewards();
                        refresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });

    }

    private void requestRewards() {
        rewardsList.clear();
        refresh.setRefreshing(true);
        new HttpRequestTask( new HttpRequest(AppUrls.RewardsUrl, HttpRequest.POST,"uid="+session.myid), new HttpRequest.Handler() {
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
                        empty_layout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        List<rewardsJson> data = new Gson().fromJson(response.body, new TypeToken<List<rewardsJson>>() {}.getType());
                        rewardsList.addAll(data);
                    }
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                    recyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new StoreAdapter(getApplicationContext(), rewardsList);
                    recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setNestedScrollingEnabled(false);
                    mAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }

    private void checkRewards(final String orderid) {
        String postdata="uid="+session.myid+"&mobile="+session.mymobile+"&checked=true"+"&orderid="+orderid;
        new HttpRequestTask( new HttpRequest(AppUrls.RewardsUrl, HttpRequest.POST,postdata), new HttpRequest.Handler() {
            @Override
            public void response(HttpResponse response) {
                if (response.code == 200) {
                    requestRewards();
                }
            }
        }).execute();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column - 2) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column - 2) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    class StoreAdapter extends RecyclerView.Adapter<Rewards.StoreAdapter.MyViewHolder> {
        private Context context;
        private List<rewardsJson> rewardsList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView cashback_amount,cashback_status;

            public MyViewHolder(View view) {
                super(view);
                cashback_amount = (TextView) view.findViewById(R.id.cashback_amount);
                cashback_status = (TextView) view.findViewById(R.id.cashback_status);
                scratch_card = (RelativeLayout) view.findViewById(R.id.scratch_card);
                cashback_scratch_background = (ImageView) view.findViewById(R.id.cashback_scratch_background);
            }
        }


        public StoreAdapter(Context context, List<rewardsJson> rewardsList) {
            this.context = context;
            this.rewardsList = rewardsList;
        }

        @Override
        public Rewards.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rewards, parent, false);

            return new Rewards.StoreAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final Rewards.StoreAdapter.MyViewHolder holder, final int position) {
            final rewardsJson reward = rewardsList.get(position);
            holder.cashback_amount.setText("\u20B9" +reward.getCashback());
            holder.cashback_status.setText(reward.getStatus());

                if (reward.getStatus().equals("Success")) {
                    holder.cashback_status.setTextColor(Color.parseColor("#3E8D41"));
                } else if (reward.getStatus().equals("Failure")) {
                    holder.cashback_status.setTextColor(Color.parseColor("#E92517"));
                } else if (reward.getStatus().equals("Processing")) {
                    holder.cashback_status.setTextColor(Color.parseColor("#F39310"));

                }
                if (reward.getStatus().equals("Processing")) {
                    cashback_scratch_background.setVisibility(View.VISIBLE);
                    scratch_card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(reward.getOpenable().equals("Yes")){
                             dialogShow(reward.getCashback(), reward.getOrderid());
                            }else{
                                Toast.makeText(getApplicationContext(), "Open after complete order", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (reward.getStatus().equals("Success") || reward.getStatus().equals("Failure")) {
                    cashback_scratch_background.setVisibility(View.GONE);
                    scratch_card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent invoice = new Intent(getApplicationContext(), Invoice.class);
                            invoice.putExtra("title", "Cashback Received");
                            invoice.putExtra("orderid", reward.getOrderid());
                            invoice.putExtra("date_time", reward.getDate() + " " + reward.getTime());
                            invoice.putExtra("amount", reward.getCashback());
                            invoice.putExtra("status", reward.getStatus());
                            invoice.putExtra("symbol", reward.getSymbol());
                            startActivity(invoice);
                        }
                    });
                }
        }
        @Override
        public int getItemCount() {
            return rewardsList.size();
        }
    }

    private void dialogShow(String cashback, final String orderid){
        final Dialog dialog = new Dialog(Rewards.this);
        dialog.setContentView(R.layout.dialog_rewards);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView close_dialog = (TextView) dialog.findViewById(R.id.close_dialog);
        TextView dialog_cashback_amount = (TextView) dialog.findViewById(R.id.dialog_cashback_amount);
        final ScratchCard mScratchCard = (ScratchCard) dialog.findViewById(R.id.scratchCard);
        dialog_cashback_amount.setText("\u20B9" +cashback);
        mScratchCard.setOnScratchListener(new ScratchCard.OnScratchListener() {
            @Override
            public void onScratch(ScratchCard scratchCard, float visiblePercent) {
                if (visiblePercent > 0.3) {
                    checkRewards(orderid);
                    dialog.dismiss();
                }
            }
        });

        close_dialog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


