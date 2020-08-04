package com.leonardorick.olx_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardorick.olx_clone.R;
import com.leonardorick.olx_clone.helper.FirebaseConfig;

public class AdvertsActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adverts);
        auth = FirebaseConfig.getAuth();
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (auth.getCurrentUser() == null)
            menu.setGroupVisible(R.id.groupNotLogged, true);
        else
            menu.setGroupVisible(R.id.groupLogged, true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                auth.signOut();
                invalidateOptionsMenu(); // used to reload menus
                break;
            case R.id.menuAccess:
                startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}