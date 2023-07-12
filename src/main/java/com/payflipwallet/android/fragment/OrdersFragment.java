package com.payflipwallet.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.payflipwallet.android.R;
import com.payflipwallet.android.config.Session;
import com.payflipwallet.android.activity.Invoice;
import com.payflipwallet.android.config.AppUrls;
import com.payflipwallet.android.http.HttpRequest;
import com.payflipwallet.android.http.HttpRequestTask;
import com.payflipwallet.android.http.HttpResponse;
import com.payflipwallet.android.json.txnJson;

import java.util.ArrayList;
import java.util.List;


public class OrdersFragment extends Fragment {
    Session session;
    SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private List<txnJson> txnlist = new ArrayList<txnJson>();
    private StoreAdapter mAdapter;
    public RelativeLayout card_view;
    LinearLayout empty_layout;
    String txnlogo;

    public OrdersFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View txnshow =inflater.inflate(R.layout.fragment_orders, container, false);
        session = new Session(getActivity());

        recyclerView = txnshow.findViewById(R.id.recycler_view);
        empty_layout = txnshow.findViewById(R.id.empty_layout);
        refresh = txnshow.findViewById(R.id.SwipeRefreshLayout);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestmyOrders();
                        refresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        requestmyOrders();
        return txnshow;
    }
    private void requestmyOrders() {
        txnlist.clear();
        refresh.setRefreshing(true);
        new HttpRequestTask( new HttpRequest(AppUrls.OrdersUrl, HttpRequest.POST,"uid="+session.myid), new HttpRequest.Handler() {
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
                        List<txnJson> data = new Gson().fromJson(response.body, new TypeToken<List<txnJson>>() {}.getType());
                        txnlist.addAll(data);
                    }
                    mAdapter = new StoreAdapter(getActivity(), txnlist);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setNestedScrollingEnabled(false);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();


    }
    class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {
        private Context context;
        private List<txnJson> txnlist;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txn_details,txn_amount,txn_date;
            public ImageView txn_logo;

            public MyViewHolder(View view) {
                super(view);
                txn_details = (TextView) view.findViewById(R.id.txn_details);
                txn_date = (TextView) view.findViewById(R.id.txn_date);
                txn_amount = (TextView) view.findViewById(R.id.txn_amount);
                txn_logo = (ImageView) view.findViewById(R.id.txn_logo);
                card_view=(RelativeLayout)view.findViewById(R.id.card_view);
            }
        }


        public StoreAdapter(Context context, List<txnJson> txnlist) {
            this.context = context;
            this.txnlist = txnlist;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_orders, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final txnJson txn = txnlist.get(position);

            if(txn.getType().equals("PaidMoney")){
                txnlogo="paidmoney";
            }
            else if(txn.getType().equals("AddMoney")){
                txnlogo="addmoney";
            }
            else if(txn.getType().equals("SendMoney")){
                txnlogo="sendmoney";
            }
            else if(txn.getType().equals("ReceivedMoney")){
                txnlogo="receivemoney";
            }
            else if(txn.getType().equals("RefundMoney")){
                txnlogo="refundmoney";
            }

                int resID=getResources().getIdentifier(txnlogo,"drawable",getActivity().getPackageName());
                holder.txn_logo.setImageDrawable(getResources().getDrawable(resID));
                holder.txn_details.setText(txn.getDetails());
                holder.txn_date.setText("Date: "+txn.getDate());
                holder.txn_amount.setText(txn.getSymbol() + " \u20B9" + txn.getAmount());

                if(txn.getStatus().equals("Success")){
                    holder.txn_amount.setTextColor(Color.parseColor("#5eaa46"));
                }
                else if(txn.getStatus().equals("Processing")){
                    holder.txn_amount.setTextColor(Color.parseColor("#F39310"));
                }else{
                    holder.txn_amount.setTextColor(Color.parseColor("#ef697a"));
                }


            card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent invoice = new Intent(getActivity(), Invoice.class);
                        invoice.putExtra("title", txn.getOperator());
                        invoice.putExtra("orderid", txn.getOrderid());
                        invoice.putExtra("date_time", txn.getDate()+" "+txn.getTime());
                        invoice.putExtra("amount", txn.getAmount());
                        invoice.putExtra("status", txn.getStatus());
                        invoice.putExtra("symbol", txn.getSymbol());
                        startActivity(invoice);
                }
            });
        }
        @Override
        public int getItemCount() {
            return txnlist.size();
        }
    }
}

