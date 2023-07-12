package com.payflipwallet.android.helper;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.payflipwallet.android.R;
import com.payflipwallet.android.activity.DealDetails;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    private List<String> title,banner;
    private LayoutInflater inflater;
    public ViewPagerAdapter (Context context, List<String> title,List<String> banner) {
        this.title = title;
        this.banner = banner;
        this.context = context;
    }
    @Override
    public int getCount() {
        return this.banner.size();
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewLayout = inflater.inflate(R.layout.slider, container, false);
        ImageView imgDisplay = (ImageView) viewLayout.findViewById(R.id.logo);
        Glide.with(context).load(banner.get(position)).into(imgDisplay);
        (container).addView(viewLayout);
        imgDisplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ii=new Intent(context, DealDetails.class);
                ii.putExtra("title", title.get(position));
                context.startActivity(ii);
            }
        });
        return viewLayout;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((RelativeLayout) object);
    }
} 