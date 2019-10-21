package com.example.muthomap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

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
        adapter.AddFragment(new TourPlan(),"Plan Tour");


        mviewPager.setAdapter(adapter);
        mtabLayout.setupWithViewPager(mviewPager);




    }
}
