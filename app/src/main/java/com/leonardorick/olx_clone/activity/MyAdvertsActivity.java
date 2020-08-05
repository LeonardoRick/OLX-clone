package com.leonardorick.olx_clone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.leonardorick.olx_clone.R;

public class MyAdvertsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_adverts);
        getSupportActionBar().setTitle("Meus anúncios");
    }

    public void openNewAdvertActivity(View view) {
        startActivity(new Intent(this, NewAdvertActivity.class));
    }
}