package com.leonardorick.olx_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.leonardorick.olx_clone.R;
import com.leonardorick.olx_clone.adapter.AdvertsAdapter;
import com.leonardorick.olx_clone.helper.Constants;
import com.leonardorick.olx_clone.helper.FirebaseConfig;
import com.leonardorick.olx_clone.helper.MessageHelper;
import com.leonardorick.olx_clone.helper.RecyclerItemClickListener;
import com.leonardorick.olx_clone.model.Advert;
import com.leonardorick.olx_clone.model.AdvertHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyAdvertsActivity extends AppCompatActivity {
    private RecyclerView myAdvertsRecyclerView;
    private List<Advert> adverts = new ArrayList<>();
    private AdvertsAdapter adapter;

    private DatabaseReference advertsRef;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_adverts);
        getSupportActionBar().setTitle("Meus anúncios");

        configRecyclerView();
        getAdverts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MessageHelper.openLoadingDialog(this, "Aguarde, carregando anúncios...");
        getAdverts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (valueEventListener != null)
            advertsRef.removeEventListener(valueEventListener);
    }

    public void openNewAdvertActivity(View view) {
        startActivity(new Intent(this, NewAdvertActivity.class));
    }

    public void configRecyclerView() {
        myAdvertsRecyclerView = findViewById(R.id.myAdvertsRecyclerView);
        myAdvertsRecyclerView.setHasFixedSize(true);
        // Layout Manager
        myAdvertsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Adapter
        adapter = new AdvertsAdapter(adverts);
        myAdvertsRecyclerView.setAdapter(adapter);
        configRecyclerViewLongClickListener();
    }

    private void configRecyclerViewLongClickListener() {
        myAdvertsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                myAdvertsRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) { }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MyAdvertsActivity.this);
                        alert.setTitle("Deletar Anúncio");
                        alert.setMessage("Tem certeza que deseja excluir este anúncio? Não será possível recuperar suas informações no futuro");
                        alert.setNegativeButton("Cancelar", null);
                        alert.setPositiveButton("Sim, tenho certeza", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Advert ad = adverts.get(position);
                                AdvertHelper.removeAd(ad);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        alert.create();
                        alert.show();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                }
        ));
    }
    public void getAdverts() {
       advertsRef = FirebaseConfig.getFirebaseDatabase()
                .child(Constants.MyAdvertsNode.KEY)
                .child(FirebaseConfig.getUserId());
       valueEventListener = advertsRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adverts.clear();
                        for (DataSnapshot advertSnapshot : snapshot.getChildren()) {
                            Advert advert = advertSnapshot.getValue(Advert.class);
                            adverts.add(advert);
                            System.out.println("adverts" + advert.getDesc());
                        }
                        Collections.reverse(adverts);
                        adapter.notifyDataSetChanged();
                        MessageHelper.closeLoadingDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        MessageHelper.closeLoadingDialog();
                    }
                });
    }
}