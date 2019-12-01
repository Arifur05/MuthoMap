package com.example.muthomap.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.muthomap.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Social extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigationView = findViewById(R.id.social_bottom_navigation_view);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //Default Fragment Transaction
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.replace(R.id.container, fragment1, "");
        fragmentTransaction1.commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.action_home:
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction1.replace(R.id.container, fragment1, "");
                            fragmentTransaction1.commit();

                            return true;
                        case R.id.action_friends:
                            FriendsFragment fragment2 = new FriendsFragment();
                            FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction2.replace(R.id.container, fragment2, "");
                            fragmentTransaction2.commit();

                            return true;
                        case R.id.action_chat:
                            ChatFragment fragment3 = new ChatFragment();
                            FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction3.replace(R.id.container, fragment3, "");
                            fragmentTransaction3.commit();

                            return true;

                    }

                    return false;
                }
            };
}
