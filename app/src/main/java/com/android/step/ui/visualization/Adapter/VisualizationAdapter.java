package com.android.step.ui.visualization.Adapter;


import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.android.step.ui.visualization.fragments.AccelerationFragment;
import com.android.step.ui.visualization.fragments.GPSFragment;
import com.android.step.ui.visualization.fragments.GravityFragment;

import java.util.ArrayList;
import java.util.List;


public class VisualizationAdapter extends FragmentStateAdapter {


    private static final String TAG = "DemoCollectionAdapter";

    List<Fragment> fragmentList = new ArrayList<>();

    public VisualizationAdapter(Fragment fragment) {
        super(fragment);
        addFragmentList();
    }

    private void addFragmentList() {
        fragmentList.add(new GPSFragment());
        fragmentList.add(new GravityFragment());
        fragmentList.add(new AccelerationFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.size() > 0 ? fragmentList.get(position) : new GPSFragment();
    }

    @Override
    public int getItemCount() {
        return fragmentList.size() > 0 ? fragmentList.size() : 0;
    }
}
