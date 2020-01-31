package com.zamnadev.mwhatsapp.MenuPrincipal;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorMenu extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;
    private List<String> titulos;

    public AdaptadorMenu(FragmentManager fm) {
        super(fm);
        titulos = new ArrayList<>();
        fragmentList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titulos.get(position);
    }

    public void addFragment(Fragment fragment, String titulo) {
        fragmentList.add(fragment);
        titulos.add(titulo);
    }
}
