package com.example.myapplication.activity.projetocontrolesalas.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.myapplication.activity.projetocontrolesalas.R;


public class PerfilFragment extends Fragment {

    private Button buttonAtualizar, buttonSair;
    private ImageView imageViewEscuro, imageViewClaro;
    private TextView textViewNomeEmpresa, textViewNomeUser, textViewEmailUser;
    private View view;
    private SharedPreferences preferences;
    public static final String userPreferences = "userPreferences";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user, container, false);

        iniciaComponentes();

        return view;
    }

    public void iniciaComponentes() {

        imageViewEscuro = (ImageView) view.findViewById(R.id.icon_theme_escuro);
        imageViewClaro = (ImageView) view.findViewById(R.id.icon_theme_claro);
        buttonAtualizar = (Button) view.findViewById(R.id.buttonAtualizar);
        buttonSair = (Button) view.findViewById(R.id.buttonSair);
        textViewNomeEmpresa = (TextView) view.findViewById(R.id.nome_empresa);
        textViewNomeUser = (TextView) view.findViewById(R.id.textViewNome);
        textViewEmailUser = (TextView) view.findViewById(R.id.textViewEmail);

        //    preferences = view.getSharedPreferences(userPreferences,
        //           Context.MODE_PRIVATE);

        // SharedPreferences pref = view.getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode


        changeTheme();
        atualizarConta();


    }

    private void atualizarConta() {
        buttonAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Click buttonSalvar", Toast.LENGTH_LONG).show();


            }
        });
    }

    private void logout() {

        buttonSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Click buttonSair", Toast.LENGTH_LONG).show();
                removerCredenciais();
            }
        });

    }

    private void removerCredenciais() {

        //editor.remove("name");
        //editor.remove("email");
        //editor.commit();
    }

    private void changeTheme() {

        imageViewEscuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Click modo Noite", Toast.LENGTH_LONG).show();

            }
        });

        imageViewClaro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.getContext().setTheme(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(getContext(), "Click modo Dia", Toast.LENGTH_LONG).show();


            }
        });
    }


}