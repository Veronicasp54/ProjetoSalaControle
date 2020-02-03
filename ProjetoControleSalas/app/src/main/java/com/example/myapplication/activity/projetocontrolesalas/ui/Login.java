package com.example.myapplication.activity.projetocontrolesalas.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.activity.projetocontrolesalas.R;
import com.example.myapplication.activity.projetocontrolesalas.services.VerificadorLogin;

public class Login extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextSenha;
    private Button buttonLogin;
    private TextView textViewCadastro;
    private ImageButton btnModoConvidado;

    private SharedPreferences preferences;
    public static final String userPreferences = "userPreferences";

    public static final String Senha = "key";
    public static final String Email = "emailKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        iniciarComponentes();
    }

    private void iniciarComponentes() {

        editTextEmail = findViewById(R.id.email_entrar);
        editTextSenha = findViewById(R.id.senha_entrar);


        buttonLogin = findViewById(R.id.btnLogin);
        textViewCadastro = findViewById(R.id.realizar_cadastro);

        btnModoConvidado = findViewById(R.id.modoConvidado);

        preferences = getSharedPreferences(userPreferences, Context.MODE_PRIVATE);

        if (preferences.contains(Email)) {
            editTextEmail.setText(preferences.getString(Email, ""));

        }
        startCadastro();
        logar();
        modoConvidado();


    }

    private void logar() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String emailStr = editTextEmail.getText().toString().trim();
                String senhaStr = editTextSenha.getText().toString().trim();


                if (verificarDados() == true) {

                    try {
                        String authReturn = new VerificadorLogin().execute(emailStr, senhaStr).get();

                        //Toast.makeText(getApplicationContext(),authReturn,Toast.LENGTH_LONG).show();

                        if (authReturn.equalsIgnoreCase("Login efetuado com sucesso!")) {
                            Toast.makeText(getApplicationContext(), "Login realizado com sucesso", Toast.LENGTH_LONG).show();

                            salvarCredenciais(emailStr);
                            System.out.println(userPreferences);

                            startClass(MainActivity.class);
                        } else {
                            Toast.makeText(getApplicationContext(), "Login inválido!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {

                        Toast.makeText(getApplicationContext(), " inválido", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Senha incorreta", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void salvarCredenciais( String email) {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(Email, email);
        editor.commit();

    }


    private void modoConvidado() {

        btnModoConvidado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startClass(MainActivity.class);
            }
        });
    }


    private void startCadastro() {
        textViewCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startClass(Cadastro.class);
            }
        });

    }


    private void startClass(Class classe) {
        Intent intent = new Intent(this, classe);
        startActivity(intent);
        this.finish();

    }

    public boolean verificarDados() {
        boolean chave = true;

        if (editTextEmail.getText().toString().trim().isEmpty() || !editTextEmail.getText().toString().trim().contains("@")
                || !editTextEmail.getText().toString().trim().contains(".")) {
            editTextEmail.setError("Insira um e-mail válido!");
            chave = false;
        }

        if (editTextSenha.getText().toString().trim().length() < 8) {
            editTextSenha.setError("Insira uma senha válida");
            chave = false;
        }

        return chave;
    }


}



