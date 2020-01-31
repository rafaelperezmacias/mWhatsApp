package com.zamnadev.mwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.zamnadev.mwhatsapp.MenuPrincipal.AdaptadorMenu;
import com.zamnadev.mwhatsapp.MenuPrincipal.ChatsFragment;
import com.zamnadev.mwhatsapp.MenuPrincipal.UsuariosFragment;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        AdaptadorMenu adaptadorMenu = new AdaptadorMenu(getSupportFragmentManager());
        adaptadorMenu.addFragment(new ChatsFragment(),"CHATS");
        adaptadorMenu.addFragment(new UsuariosFragment(),"USUARIOS");

        viewPager.setAdapter(adaptadorMenu);
        tabLayout.setupWithViewPager(viewPager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menuCerrarSesion) {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();

            return true;
        }

        return false;
    }
}
