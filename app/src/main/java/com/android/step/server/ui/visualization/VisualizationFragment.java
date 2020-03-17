package com.android.step.server.ui.visualization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.step.R;
import com.android.step.server.ui.visualization.Adapter.VisualizationAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class VisualizationFragment extends Fragment {

    private static final String TAG = "VisualizationFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_visualization, container, false);
        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = root.findViewById(R.id.pager);
        viewPager.setAdapter(new VisualizationAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> {
            if (position == 0) {
                tab.setText("位置方向");
            } else if (position == 1) {
                tab.setText("重力");
            } else if (position == 2) {
                tab.setText("加速度");
            }
        }).attach();
        return root;
    }
}