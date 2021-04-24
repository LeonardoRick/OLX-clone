package com.leonardorick.olx_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.leonardorick.olx_clone.R;
import com.leonardorick.olx_clone.adapter.AdvertsAdapter;
import com.leonardorick.olx_clone.helper.FirebaseConfig;
import com.leonardorick.olx_clone.helper.MessageHelper;
import com.leonardorick.olx_clone.model.Advert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdvertsActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    private RecyclerView advertsRecyclerView;
    private Button buttonRegion, buttonCategory;
    private AdvertsAdapter adapter;
    private List<Advert> adverts = new ArrayList();
    private DatabaseReference publicAdvertsRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adverts);
        auth = FirebaseConfig.getAuth();

        publicAdvertsRef = FirebaseConfig.getFirebaseDatabase().child("PublicAdvertsNode");
        configRecyclerView();
        getPublicAdverts();
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
                break;
            case R.id.menuAdverts:
                startActivity(new Intent(getApplicationContext(), MyAdvertsActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void configRecyclerView() {
        advertsRecyclerView = findViewById(R.id.advertsRecyclerView);
        advertsRecyclerView.setHasFixedSize(true);
        advertsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter
        adapter = new AdvertsAdapter(adverts);
        advertsRecyclerView.setAdapter(adapter);
    }


    public void uFFilterResults(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        // spinner configuration
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        Spinner spinner = viewSpinner.findViewById(R.id.filterSpinner);
        alert.setView(viewSpinner);
        String[] ufs = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ufs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        alert.setTitle("Selecione o estado desejado");
        alert.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getPublicAdvertsFilteredUF(spinner.getSelectedItem().toString());
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void categoryFilterResults(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        // spinner configuration
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        Spinner spinner = viewSpinner.findViewById(R.id.filterSpinner);
        alert.setView(viewSpinner);
        String[] ufs = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ufs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        alert.setTitle("Selecione a categoria desejada");
        alert.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getPublicAdvertsFilteredCategory(spinner.getSelectedItem().toString());
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void getPublicAdvertsFilteredUF(String UFFilter) {
        MessageHelper.openLoadingDialog(this, "Aguarde, carregando anúncios...");
        adverts.clear();
        publicAdvertsRef.child(UFFilter).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot category: snapshot.getChildren()) {
                    for (DataSnapshot advert: category.getChildren()) {
                        Advert newAd = advert.getValue(Advert.class);
                        adverts.add(newAd);
                    }
                }

                Collections.reverse(adverts);
                adapter.notifyDataSetChanged();
                MessageHelper.closeLoadingDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPublicAdvertsFilteredCategory(String category) {
        MessageHelper.openLoadingDialog(this, "Aguarde, carregando anúncios...");
        adverts.clear();

        publicAdvertsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uf: snapshot.getChildren()) {
                    for (DataSnapshot categorySnapshot: uf.getChildren()) {
                        for (DataSnapshot advert: categorySnapshot.getChildren()) {
                            if (category.trim().equals(categorySnapshot.getKey().trim())) {
                                Advert newAd = advert.getValue(Advert.class);
                                adverts.add(newAd);
                            }
                        }
                    }
                }

                Collections.reverse(adverts);
                adapter.notifyDataSetChanged();
                MessageHelper.closeLoadingDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPublicAdverts() {
        MessageHelper.openLoadingDialog(this, "Aguarde, carregando anúncios...");
        adverts.clear();
        publicAdvertsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uf: snapshot.getChildren()) {
                    for (DataSnapshot category: uf.getChildren()) {
                        for (DataSnapshot advert: category.getChildren()) {
                            Advert newAd = advert.getValue(Advert.class);
                            adverts.add(newAd);
                        }
                    }
                }
                Collections.reverse(adverts);
                adapter.notifyDataSetChanged();
                MessageHelper.closeLoadingDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}