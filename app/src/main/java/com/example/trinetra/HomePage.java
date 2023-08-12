package com.example.trinetra;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class HomePage extends AppCompatActivity {
    private static final String TAG = HomePage.class.getSimpleName();
    AnimatedBottomBar animatedBottomBar;
    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        animatedBottomBar = findViewById(R.id.bottom_bar);

        if(savedInstanceState == null){
            animatedBottomBar.selectTabById(R.id.home,true);
             fragmentManager = getSupportFragmentManager();
             HomeFragment homeFragment = new HomeFragment();
             fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        }
        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab lasttab, int newIndex, @NonNull AnimatedBottomBar.Tab newTab) {
                Fragment fragment = null;
                switch (newTab.getId()){
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.incidents:
                        fragment = new CurrentIncidentsFragment();
                        break;
                    case R.id.create:
                        fragment = new IncidentFragment();
                        break;
                    case R.id.account:
                        fragment = new AccountFragment();
                        break;
                }
                if(fragment != null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }
                else{
                    Log.e(TAG, "error in creating fragment");
                }
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });
    }
}