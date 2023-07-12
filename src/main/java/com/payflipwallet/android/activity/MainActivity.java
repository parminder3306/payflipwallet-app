package com.payflipwallet.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.payflipwallet.android.fragment.MainFragment;
import com.payflipwallet.android.fragment.ProfileFragment;
import com.payflipwallet.android.fragment.OrdersFragment;
import com.payflipwallet.android.fragment.WalletFragment;
import com.payflipwallet.android.R;

public class MainActivity extends AppCompatActivity {

    private ActionBar toolbar;
    private static long backPressed = 0;
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = getSupportActionBar();
        toolbar.setElevation(0);
        navigation = (BottomNavigationView) findViewById(R.id.bottom_menu);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(navigation.getSelectedItemId());
        toolbar.setTitle("PayflipWallet");
        loadFragment(new MainFragment());

    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            final int previousItem = navigation.getSelectedItemId();
            final int nextItem = item.getItemId();
            if (previousItem != nextItem) {
                Fragment fragment;
            switch (nextItem) {
                case R.id.home:
                    toolbar.setTitle("PayflipWallet");
                    fragment = new MainFragment();
                    loadFragment(fragment);
                    break;
                case R.id.wallet:
                    toolbar.setTitle("Wallet");
                    fragment = new WalletFragment();
                    loadFragment(fragment);
                    break;
                case R.id.scanpay:
                    startActivity(new Intent(getApplicationContext(), ScanPay.class));
                    break;
                case R.id.transactions:
                    toolbar.setTitle("Transactions");
                    fragment = new OrdersFragment();
                    loadFragment(fragment);
                    break;
                case R.id.account:
                    toolbar.setTitle("Profile");
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    break;
            }
            }

            return true;
        }
    };

    /**
     * loading fragment into FrameLayout
     *
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public void onBackPressed() {
        if (backPressed + 2500 > System.currentTimeMillis()){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else{
            Toast.makeText(MainActivity.this,R.string.tapAgain, Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

}
