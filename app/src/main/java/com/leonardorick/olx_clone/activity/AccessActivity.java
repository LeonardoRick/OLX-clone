package com.leonardorick.olx_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.leonardorick.olx_clone.R;
import com.leonardorick.olx_clone.helper.FirebaseConfig;
import com.leonardorick.olx_clone.helper.MessageHelper;

public class AccessActivity extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    private Switch loginSwitch;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);
        auth = FirebaseConfig.getAuth();

        initViewElements();
    }

    private void initViewElements() {
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginSwitch = findViewById(R.id.loginSwitch);
    }

    public void loginOrRegister(View view) {
        boolean isRegister = loginSwitch.isChecked();
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();
        if (email.isEmpty() || password.isEmpty())
            MessageHelper.showLongToast("Preencha todos os campos");
        else {
            if (isRegister)
                register(email, password);
            else
                login(email, password);
        }
    }

    private void register(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            MessageHelper.showLongToast("Usuário criado com sucesso");
                            finish();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                MessageHelper.showLongToast("Email inválido");
                            } catch (FirebaseAuthWeakPasswordException e) {
                                MessageHelper.showLongToast("Senha fraca, insira uma que contenha ao menos letras e números");
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                MessageHelper.showLongToast("Email ou senha incorretos");
                            } catch (Exception e) {
                                MessageHelper.showLongToast("Algo deu errado" + e.getMessage());
                            }
                        }
                    }
                });
    }

    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            MessageHelper.showLongToast("Usuário logado");
                            finish();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                              MessageHelper.showLongToast("Email ou senha inválidos");
                            } catch (FirebaseAuthInvalidUserException e) {
                                MessageHelper.showLongToast("Usuário não cadastrado");
                            } catch (Exception e) {
                                MessageHelper.showLongToast("Algo deu errado" + e.getMessage());
                            }

                        }
                    }
                });
    }
}