package com.example.myapplication.activity.projetocontrolesalas.menu;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.activity.projetocontrolesalas.R;
import com.example.myapplication.activity.projetocontrolesalas.model.Empresa;
import com.example.myapplication.activity.projetocontrolesalas.model.Sala;
import com.example.myapplication.activity.projetocontrolesalas.services.RequestCadastro;
import com.example.myapplication.activity.projetocontrolesalas.services.RequestSalas;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityReuniao extends AppCompatActivity {

    private Spinner spinnerSalas;
    private SharedPreferences preferences;
    public static final String userPreferences = "userPreferences";
    private List<String> listaNomesSalas = new ArrayList<>();
    private EditText textNomeReuniao, textQuantPessoas;
    private TextView textHorarioInicial, textHorarioFinal, textData;
    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private Button buttonSave;
    private List<Sala> listaSalas = new ArrayList();

    private int idSalaSelecionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.box_reservar);

        getSupportActionBar().setTitle("Marcar Reunião");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        iniciarComponentes();

    }

    private void iniciarComponentes() {

        spinnerSalas = findViewById(R.id.spinnerSalas);
        buscarListSalas();
        createSpinner();

        textData = findViewById(R.id.textData);
        textData.setInputType(InputType.TYPE_NULL);

        escolherData();

        textHorarioInicial = findViewById(R.id.textViewHourInicio);
        textHorarioFinal = findViewById(R.id.textViewHourFinal);
        escolherHorario();

        buttonSave = findViewById(R.id.buttonSave);

        getDataSelecionada();

        validarDados();

        salvarReuniao();



    }

    private void createJson(String tituloReuniao,int idSala, String horarioMarcadoInicial, String horarioMarcadoFinal, String dataMarcada, int capacidade) {

        idSala = idSalaSelecionada;

        JSONObject reservaJson = new JSONObject();

        try {
            reservaJson.put("", tituloReuniao);
            reservaJson.put("idSala", idSala);
            reservaJson.put("dataHoraInicio", horarioMarcadoInicial);
            reservaJson.put("dataHoraFim", horarioMarcadoInicial);
            reservaJson.put("data", dataMarcada);
            reservaJson.put("capacidade", capacidade);



            System.out.println(reservaJson.toString());

            String reservaEncoded = Base64.encodeToString(reservaJson.toString().getBytes("UTF-8"), Base64.NO_WRAP);
            System.out.println(reservaEncoded);

            String respostaMetodo = new RequestCadastro().execute(reservaEncoded).get();

            if (respostaMetodo.equals("Reserva realizada com sucesso")) {

                Toast.makeText(ActivityReuniao.this, "Reserva realizada com sucesso!", Toast.LENGTH_SHORT).show();
                startClass(CalendarFragment.class);


            } else {
                Toast.makeText(ActivityReuniao.this, "A reserva não foi realizada, os dados enviados estão incompletos", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(
                    ActivityReuniao.this,
                    "A reserva não foi realizada!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void salvarReuniao() {

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tituloReuniao = textNomeReuniao.getText().toString();
                String horarioMarcadoInicio = textHorarioInicial.getText().toString();
                String horarioMarcadoFinal = textHorarioFinal.getText().toString();
                String dataMarcada = textData.getText().toString();
                String capacidade = textQuantPessoas.getText().toString();


                if (validarDados() == true) {
                    //createJson(tituloReuniao, horarioMarcadoInicio, horarioMarcadoFinal, dataMarcada, capacidade);

                    startClass(CalendarFragment.class);


                } else {
                    Toast.makeText(getApplication(), "Dados Invalidos", Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    private void startClass(Class classe) {
        Intent intent = new Intent(this, classe);
        startActivity(intent);
        this.finish();
    }


    private void getDataSelecionada() {
        Intent intent = getIntent();
        //Bundle dados = new Bundle();
        //dados = intent.getExtras();
        String dataRecuperada = getIntent().getCharSequenceExtra("dataSelecionada").toString();

        textData.setText(dataRecuperada);

        System.out.println(dataRecuperada);
    }

    private void escolherData() {

        textData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                // date picker dialog
                datePicker = new DatePickerDialog(ActivityReuniao.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                                textData.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePicker.show();
                Toast.makeText(getApplication(), "Selected Date: " + textData.getText(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void escolherHorario() {
        textHorarioInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);

                timePicker = new TimePickerDialog(ActivityReuniao.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                textHorarioInicial.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                timePicker.show();
            }
        });

        textHorarioFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);

                timePicker = new TimePickerDialog(ActivityReuniao.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                textHorarioFinal.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                timePicker.show();
            }
        });


    }


    private void buscarListSalas() {
        preferences = getSharedPreferences(userPreferences, Context.MODE_PRIVATE);

        String requestSalas = null;

        try {
            requestSalas = new RequestSalas().execute(preferences.getString("userIdEmpresa", null)).get();
            System.out.println("salas em string: " + requestSalas);


            if (requestSalas.length() > 0) {

                JSONArray jsonArray = new JSONArray(requestSalas);

                if (jsonArray.length() > 0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        if (jsonObject.has("nome")) {

                            String nome = jsonObject.getString("nome");

                            Empresa newEmpresa = new Empresa();

                            newEmpresa.setNomeEmpresa(nome);

                            listaNomesSalas.add(newEmpresa.getNomeEmpresa());

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaNomesSalas);

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinnerSalas.setAdapter(adapter);
                            spinnerSalas.setVisibility(View.VISIBLE);

                        }


                    }


                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createSpinner() {
        spinnerSalas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idSalaSelecionada = listaSalas.get(position).getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return true;

    }

    public boolean validarDados() {

        boolean chave = true;

        if (textNomeReuniao.getText().toString().trim().length() >= 0) {
            textNomeReuniao.setError("Campo Obrigatorio");
            chave = false;
        }

        if (textQuantPessoas.getText().toString().trim().length() >= 0) {
            textQuantPessoas.setError("Digite um valor valido");
            chave = false;
        }

        if (textData.getText().toString().trim().length() >= 0) {
            textData.setError("Campo obrigatório");
            chave = false;

        }
        if (textHorarioInicial.getText().toString().trim().length() >= 0 ||
                textHorarioInicial.getText() == textHorarioFinal.getText()) {
            textHorarioInicial.setError("Campo obrigatório");
            chave = false;
        }
        if (textHorarioFinal.getText().toString().trim().length() >= 0) {
            textHorarioFinal.setError("Campo obrigatório");
            chave = false;
        }

        return chave;
    }
}
