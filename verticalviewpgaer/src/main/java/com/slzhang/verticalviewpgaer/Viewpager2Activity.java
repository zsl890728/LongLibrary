package com.slzhang.verticalviewpgaer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager2垂直分页效果
 * ViewPager2可设置垂直/水平 分页 这里实现的是垂直分页效果
 * 亲测过  ViewPager2内加载的fragment可以再次嵌入ViewPager2
 */
public class Viewpager2Activity extends AppCompatActivity {
    private TabLayout tab_data_type;
    private ViewPager2 viewpager;
    private FragmentStateAdapter adapter;

    private List<Fragment> lsFragment=new ArrayList<>();
    private String[] tabtitle = new String[]{"页面1", "页面2", "页面3"};
    private AFragment aFragment;
    private BFragment bFragment;
    private CFragment cFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager2);

        tab_data_type=findViewById(R.id.tab_data_type);
        viewpager=findViewById(R.id.viewpager);

        aFragment=new AFragment();
        bFragment=new BFragment();
        cFragment=new CFragment();
        lsFragment.add(aFragment);
        lsFragment.add(bFragment);
        lsFragment.add(cFragment);

        adapter=new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return lsFragment.get(position);
            }

            @Override
            public int getItemCount() {
                return lsFragment.size();
            }
        };
        viewpager.setAdapter(adapter);

        //实现viewpager2与tablayout联动
        new TabLayoutMediator(tab_data_type, viewpager, true, new TabLayoutMediator.OnConfigureTabCallback() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                //这里需要根据position修改tab的样式和文字等
                tab.setText(tabtitle[position]);
            }
        }).attach();
        //最后一定要attach()
    }
}
