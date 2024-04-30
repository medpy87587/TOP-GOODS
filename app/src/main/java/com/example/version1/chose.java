package com.example.version1;

import static com.example.version1.R.*;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class chose extends AppCompatActivity {

    ViewPager2 viewPager2;
    viewPagerAdapter viewPagerAdapter;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_chose);
        bottomNavigationView=findViewById(id.bottomNav);
        viewPager2=findViewById(id.viewPager);
        viewPagerAdapter=new viewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {


            @SuppressLint("NonConstantResourceId")
            @Override


            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == id.accueil) {
                    viewPager2.setCurrentItem(0);
                } else if (itemId == id.recherche) {
                    viewPager2.setCurrentItem(1);
                } else if (itemId == id.add) {
                    viewPager2.setCurrentItem(2);
                }else if (itemId == id.panier) {
                    viewPager2.setCurrentItem(3);
                }else if (itemId == id.profile) {
                    viewPager2.setCurrentItem(4);
                }
                return true;
            }
        });


        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(id.accueil).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(id.recherche).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(id.add).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(id.panier).setChecked(true);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(id.profile).setChecked(true);
                        break;
                }

                super.onPageSelected(position);
            }
        });


    }
}