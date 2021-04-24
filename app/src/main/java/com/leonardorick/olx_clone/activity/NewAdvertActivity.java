package com.leonardorick.olx_clone.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leonardorick.olx_clone.R;
import com.leonardorick.olx_clone.helper.Constants;
import com.leonardorick.olx_clone.helper.FirebaseConfig;
import com.leonardorick.olx_clone.helper.MessageHelper;
import com.leonardorick.olx_clone.model.Advert;
import com.leonardorick.olx_clone.model.AdvertHelper;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewAdvertActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTitle, editDesc;
    private CurrencyEditText editValue;
    private MaskEditText editPhone;

    private Spinner spinnerStates, spinnerCategories;
    private ImageView image1, image2, image3;
    private Map<Integer, Uri> toUploadImagesList = new HashMap();
    private List<String> uploadedImagesRef = new ArrayList<>();
    Integer uploaded = 0;

    private static final int STORAGE_REQUEST_IMG1 = 1;
    private static final int STORAGE_REQUEST_IMG2 = 2;
    private static final int STORAGE_REQUEST_IMG3 = 3;

    private static final int SETTINGS_INTENT = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advert);
        initViewElements();
        loadSpinnerData();
    }

    private void initViewElements() {
        editTitle = findViewById(R.id.editTextTitle);
        editDesc = findViewById(R.id.editTextDesc);
        editValue = findViewById(R.id.editTextValue);
        editPhone = findViewById(R.id.editTextPhone);

        spinnerStates = findViewById(R.id.spinnerStates);
        spinnerCategories = findViewById(R.id.spinnerCategories);

        image1 = findViewById(R.id.imageView1);
        image2 = findViewById(R.id.imageView2);
        image3 = findViewById(R.id.imageView3);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        // Config locale to pt -> portugues BR -> brasil
        Locale locale = new Locale("pt", "BR");
        editValue.setLocale(locale);
    }

    private void loadSpinnerData() {
        String[] states = getResources().getStringArray(R.array.states);

        ArrayAdapter<String> statesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, states
        );
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStates.setAdapter(statesAdapter);

        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories
        );
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(categoriesAdapter);
    }

    private void saveAd(String state, String category, String title, String desc, String value, String phone) {
        MessageHelper.openLoadingDialog(this, "Aguarde, carregando");
        Advert advert = new Advert(state, category, title, desc, value, phone);

        for (Map.Entry<Integer, Uri> entry : toUploadImagesList.entrySet()) {

                StorageReference imageRef = FirebaseConfig.getFirebaseStorage().child(Constants.Storage.ADVERTS)
                        .child(advert.getId())
                        .child("image" + uploaded + ".jpeg");
                UploadTask uploadTask = imageRef.putFile(entry.getValue());
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MessageHelper.showLongToast("Erro ao fazer upload da imagem");
                        Log.e("INFO", "Falha ao fazer upload da imagem: " + e.getMessage());
                        MessageHelper.closeLoadingDialog();
                    }
                });

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uploaded++;
                                uploadedImagesRef.add(uri.toString());
                                if (uploadedImagesRef.size() == toUploadImagesList.size()) {
                                    advert.setImages(uploadedImagesRef);
                                    AdvertHelper.saveAdOnDatabase(advert);
                                    MessageHelper.closeLoadingDialog();
                                    finish();
                                }
                            }
                        });
                    }
                });
        }
    }

    public void validateAd(View view) {
        String state = spinnerStates.getSelectedItem().toString();
        String category = spinnerCategories.getSelectedItem().toString();
        String title = editTitle.getText().toString();
        String desc = editDesc.getText().toString();
        String value = editValue.getText().toString();
        String phone = editPhone.getUnMasked();
        if (toUploadImagesList.size() > 0) {
            if (!title.isEmpty()) {
                if (!value.isEmpty() && !value.equals("0")) {
                    if (!desc.isEmpty()) {
                        if (!phone.isEmpty() && phone.length() >= 10) {
                            saveAd(state, category, title, desc, value, phone);
                        } else
                            MessageHelper.showLongToast("Preencha o campo telefone. Digite ao menos 10 números");
                    } else
                        MessageHelper.showLongToast("Preencha o campo descrição");
                } else
                    MessageHelper.showLongToast("Preencha o campo valor");
            } else
                MessageHelper.showLongToast("Preencha o campo titulo");
        } else
            MessageHelper.showLongToast("Selecione pelo menos uma foto");
    }



    private void uploadImage(Uri image, int requestCode) {

        int index = 0;
        if (requestCode == STORAGE_REQUEST_IMG1) {
            image1.setImageURI(image);
        } else if (requestCode == STORAGE_REQUEST_IMG2) {
            image2.setImageURI(image);
            index = 1;
        } else if (requestCode == STORAGE_REQUEST_IMG3) {
            image3.setImageURI(image);
            index = 2;
        }
        boolean notAddedYet = true;
        try {
            toUploadImagesList.remove(index);
        } catch (IndexOutOfBoundsException e) {
            toUploadImagesList.put(index, image);
            notAddedYet = false;
        }
        if (notAddedYet)
            toUploadImagesList.put(index, image);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView1:
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_REQUEST_IMG1);
                break;
            case R.id.imageView2:
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_REQUEST_IMG2);
                break;
            case R.id.imageView3:
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_REQUEST_IMG3);
                break;
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onSuccessPermissionGranted(requestCode);
        } else if (shouldShowRequestPermissionRationale(permissions[0])) {
            alertUserPermissionNeeded();
        } else {
            showDefaultSettingsPermissionGranted();
        }
    }

    private void onSuccessPermissionGranted(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                switch (requestCode) {
                    case STORAGE_REQUEST_IMG1:
                    case STORAGE_REQUEST_IMG2:
                    case STORAGE_REQUEST_IMG3:
                        Uri image = data.getData();
                        if (image != null)
                            uploadImage(image, requestCode);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void alertUserPermissionNeeded() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Permissão Necessária");
        alert.setMessage("Você precisa conceder permissão à galeria se quiser escolher uma foto do seu produto");

        alert.setPositiveButton("Entendi", null);
        alert.create();
        alert.show();
    }

    private void showDefaultSettingsPermissionGranted() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Mudar a permissão em configurações");
        alert.setMessage("Clique em configuraçõeos para manualmente permitir o aplicativo acessar a galeria");
        alert.setNegativeButton("Continuar sem permissões", null);
        alert.setPositiveButton("Configurações", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openPhoneSettings();
            }
        });
        alert.create();
        alert.show();
    }

    /**
     * Open native smartphone settings
     */
    private void openPhoneSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        // current activity will be notified when config is finished (back button is pressed from settings)
        startActivityForResult(intent, SETTINGS_INTENT);
    }
}