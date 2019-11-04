package com.example.muthomap.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.muthomap.R;
import com.google.android.material.tabs.TabLayout;

public class Services extends AppCompatActivity {


    private TabLayout mtabLayout;
    private ViewPager mviewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        mtabLayout= findViewById(R.id.tab_layout);
        mviewPager=findViewById(R.id.view_pager);
        adapter=new ViewPagerAdapter(getSupportFragmentManager());


        //adding Fragments
        adapter.AddFragment(new BookTicket(),"Book Ticket");
        adapter.AddFragment(new OrderFood(),"Order Food");



        mviewPager.setAdapter(adapter);
        mtabLayout.setupWithViewPager(mviewPager);




    }
}
