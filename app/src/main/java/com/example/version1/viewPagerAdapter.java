package com.example.version1;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class viewPagerAdapter extends FragmentStateAdapter {
    public viewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:return  new recherche();
            case 2:return  new add();
            case 3:return  new panier();
            case 4:return  new profil();
            default:return new accueil();

        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
