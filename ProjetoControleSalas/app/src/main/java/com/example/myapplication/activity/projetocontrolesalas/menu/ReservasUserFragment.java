package com.example.myapplication.activity.projetocontrolesalas.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.activity.projetocontrolesalas.R;
import com.example.myapplication.activity.projetocontrolesalas.adapter.AdapterReservasUser;
import com.example.myapplication.activity.projetocontrolesalas.model.ReservaSala;
import com.example.myapplication.activity.projetocontrolesalas.services.RequestCancelarReserva;
import com.example.myapplication.activity.projetocontrolesalas.services.RequestReservasId;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ReservasUserFragment extends Fragment {

    private View view;
    private TextView dataAtual, quantReunioes, textSemReunioes;
    private int contReunioes;

    private List<ReservaSala> reservas = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listRerservas;
    private List<String> itemReserva = new ArrayList<>();
    private SharedPreferences preferences;
    public static final String userPreferences = "userPreferences";

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String requestReservas;

    private FloatingActionButton floatingActionButton;

    private AlertDialog alerta;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservas, container, false);

        iniciaComponentes();

        return view;
    }

    private void iniciaComponentes() {

        dataAtual = view.findViewById(R.id.dataAtual);
        dataAtual.setText(getDataFormatSemana());

        quantReunioes = view.findViewById(R.id.textQuantReunioes);
        listRerservas = view.findViewById(R.id.listReservas);

        quantReunioes = view.findViewById(R.id.textQuantReunioes);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        textSemReunioes = view.findViewById(R.id.textSemReservas);

        textSemReunioes.setVisibility(View.VISIBLE);
//        listRerservas.setVisibility(View.INVISIBLE);

        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        exibirReservas();

        excluirReservas();

        atualizarReservas();

        criarReserva();
    }

    private void criarReserva() {

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityReserva.class);

                String data = getDataAtual();
                intent.putExtra("dataSelecionada", data);

                startActivity(intent);

            }
        });
    }

    private String getDataAtual() {
        Date data = new Date();
        Locale local = new Locale("pt", "BR");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY", local);

        return dateFormat.format(data);
    }

    public void atualizarReservas() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(getClass().getSimpleName(), "refresh");

                listRerservas.setVisibility(View.INVISIBLE);
                reservas.clear();
                contReunioes = 0;
                textSemReunioes.setVisibility(View.INVISIBLE);
                exibirReservas();

                if (requestReservas != null) {
                    listRerservas.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);

                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "Carregamento incompleto", Toast.LENGTH_LONG);
                }
            }
        });
    }


    private void excluirReservas() {
        listRerservas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

//                Toast.makeText(getActivity(), "position " + position, Toast.LENGTH_LONG).show();

                try {
                    String idStr = String.valueOf(id);
                    final String cancelarReserva = new RequestCancelarReserva().execute(idStr).get();

                    System.out.println("Resultado do cancelamento da Reserva: " + cancelarReserva);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Excluir Reserva");
                    builder.setMessage("Você realmente deseja deletar está reserva permanentemente?");
                    builder.setIcon(R.drawable.ic_cancel);

                    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Toast.makeText(getContext(), "Reserva excluida com sucesso", Toast.LENGTH_SHORT).show();

                            if (cancelarReserva.equals("A reserva foi cancelada com sucesso")) {

                                reservas.remove(position);
                                // adapter.clear();
                                AdapterReservasUser adapter = new AdapterReservasUser(reservas, getActivity());
                                listRerservas.setAdapter(adapter);
                                contReunioes -= 1;

                            } else {
                                Toast.makeText(getActivity(), "Click list" + position, Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Toast.makeText(getContext(), "Cancelardo com sucesso", Toast.LENGTH_SHORT).show();
                            alerta.dismiss();
                        }
                    });

                    alerta = builder.create();
                    alerta.show();

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Erro ao efetuar cadastro!", Toast.LENGTH_SHORT).show();

                }


                return true;
            }
        });
    }

    private String getDataFormatSemana() {
        Date data = new Date();
        Locale local = new Locale("pt", "BR");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd',' EEEE", local);
        return dateFormat.format(data);

    }

    private String getDataAtualCompleta() {
        Date data = new Date();
        Locale local = new Locale("pt", "BR");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", local);
        return dateFormat.format(data);

    }

    private String getDiaSeguinte() {
        Date hoje = new Date();
        Calendar cal = Calendar.getInstance();
        Locale local = new Locale("pt", "BR");
        cal.setTime(hoje);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date amanha = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", local);
        return dateFormat.format(amanha);
    }


    public void exibirReservas() {

        preferences = getActivity().getSharedPreferences(userPreferences, Context.MODE_PRIVATE);
        System.out.println(preferences.getString("userId", null));

        requestReservas = null;
        try {
            requestReservas = new RequestReservasId().execute(preferences.getString("userId", null)).get();

            System.out.println(requestReservas);

            JSONArray reservasJson = new JSONArray(requestReservas);

            if (reservasJson.length() > 0) {

                for (int i = 0; i < reservasJson.length(); i++) {
                    contReunioes++;

                    JSONObject jsonObjectReserva = reservasJson.getJSONObject(i);

                    if (jsonObjectReserva.has("idUsuario") && jsonObjectReserva.has("id")) {

                        int idUser = jsonObjectReserva.getInt("idUsuario");
                        int idSala = jsonObjectReserva.getInt("idSala");
                        int idReserva = jsonObjectReserva.getInt("id");

                        String descricaoReserva = jsonObjectReserva.getString("descricao");
                        String dataHoraInicio = jsonObjectReserva.getString("dataHoraInicio");
                        String dataHoraFim = jsonObjectReserva.getString("dataHoraFim");

                        ReservaSala newReserva = new ReservaSala();

                        newReserva.setIdSala(idSala);
                        newReserva.setDescricaoReserva(descricaoReserva);
                        newReserva.setIdUser(idUser);
                        newReserva.setIdReserva(idReserva);

                        newReserva.setNomeSala("Sala para reuniao");

                        System.out.println("data inicio");

                        //data
                        String data = dataHoraInicio.split("T")[0];
                        String dataSplit = (data.split("-")[2] + "/" + data.split("-")[1]);

                        String dataHoje = getDataAtualCompleta().toUpperCase();
                        String dataAmanha = getDiaSeguinte().toUpperCase();

                        if (dataSplit.equals(dataHoje)) {

                            newReserva.setDataReserva("HOJE");

                        } else if (dataSplit.equals(dataAmanha)) {

                            newReserva.setDataReserva("AMANHÃ");

                        } else {

                            newReserva.setDataReserva(dataSplit);


                        }

                        //hour//
                        String horarioInicioSplit = dataHoraInicio.split("T")[1];
                        String horarioInicioStr = horarioInicioSplit.split(":00Z")[0];

                        String horarioFimSplit = dataHoraFim.split("T")[1];
                        String horarioFimStr = horarioFimSplit.split(":00Z")[0];

                        newReserva.setHorarioInicio(horarioInicioStr);
                        newReserva.setHorarioFinal(horarioFimStr);

                        reservas.add(newReserva);
                        itemReserva.add(descricaoReserva);

                    }

                }

                if (contReunioes > 1) {
                    quantReunioes.setText(contReunioes + " " + "reuniões");
                } else {
                    quantReunioes.setText(contReunioes + " " + "reunião");
                }
                listRerservas = view.findViewById(R.id.listReservas);

                AdapterReservasUser adapter = new AdapterReservasUser(reservas, getActivity());
                listRerservas.setAdapter(adapter);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}