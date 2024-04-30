package com.example.version1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class onBoarding extends AppCompatActivity {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TextView skipButton;
   private TextView nextButton;
    private LinearLayout dotsLayout;
    private View[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        skipButton = findViewById(R.id.skip);
       nextButton = findViewById(R.id.nextButton);
        dotsLayout = findViewById(R.id.dotsLayout);

        dots = new View[]{
                findViewById(R.id.dot1),
                findViewById(R.id.dot2),
                findViewById(R.id.dot3)
        };

        // Handle button clicks
        // Handle button clicks
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(onBoarding.this, login1.class);
                startActivity(intent);

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Next" button click
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < pagerAdapter.getCount() - 1) {
                    viewPager.setCurrentItem(currentItem + 1);
                } else {
                    // If on the last page (onBording3), perform your action
                    // For example, navigate to the login1 activity
                    Intent intent = new Intent(onBoarding.this, login1.class);
                    startActivity(intent);
                }
            }
        });




        // Set initial state of dots
        selectDot(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
            //...
        });
    }

    private void selectDot(int position) {
        for (int i = 0; i < dots.length; i++) {
            int drawableId = (i == position) ? R.drawable.dot_secl : R.drawable.dot;
            dots[i].setBackgroundResource(drawableId);
        }
    }

    // ...

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        private static final int NUM_PAGES = 3;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new onBording1();
                case 1:
                    return new onbording2();
                case 2:
                    return new onBording3();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
