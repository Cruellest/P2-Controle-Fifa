package com.android.fifaapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.android.fifaapp.R;
import com.android.fifaapp.database.CampeonatoDatabase;
import com.android.fifaapp.entity.Jogador;
import com.android.fifaapp.entity.Partida;

import java.util.List;

public class PartidasFragment extends Fragment {
    EditText data, placar1, placar2, filtro;
    Spinner spinnerJogador1, spinnerJogador2;
    Button btnSalvar, btnAtualizar, btnExcluir, btnLimpar, buscar;
    ListView listaPartidas;
    CampeonatoDatabase db;
    List<Jogador> jogadores;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partidas, container, false);

        // Inicializando os componentes da UI conforme XML
        data = view.findViewById(R.id.etDataPartida);
        placar1 = view.findViewById(R.id.etPlacarJogador1);
        placar2 = view.findViewById(R.id.etPlacarJogador2);
        spinnerJogador1 = view.findViewById(R.id.spinnerJogador1);
        spinnerJogador2 = view.findViewById(R.id.spinnerJogador2);
        btnSalvar = view.findViewById(R.id.btnSalvarPartida);
        btnAtualizar = view.findViewById(R.id.btnAtualizarPartida);
        btnExcluir = view.findViewById(R.id.btnExcluirPartida);
        btnLimpar = view.findViewById(R.id.btnLimparPartida);

        filtro = view.findViewById(R.id.filtroNickname);
        buscar = view.findViewById(R.id.btnBuscar);
        listaPartidas = view.findViewById(R.id.listViewPartidas);

        // Inicializando o banco de dados e obtendo os jogadores
        db = CampeonatoDatabase.getDatabase(requireContext());
        jogadores = db.jogadorDao().listarTodos();

        // Configurando os Spinners com os jogadores disponíveis
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (Jogador j : jogadores) {
            adapter.add(j.nickname);
        }
        spinnerJogador1.setAdapter(adapter);
        spinnerJogador2.setAdapter(adapter);

        // Configuração do botão Salvar
        btnSalvar.setOnClickListener(v -> {
            int pos1 = spinnerJogador1.getSelectedItemPosition();
            int pos2 = spinnerJogador2.getSelectedItemPosition();

            if (pos1 == pos2) {
                Toast.makeText(requireContext(), "Jogadores devem ser diferentes", Toast.LENGTH_SHORT).show();
                return;
            }

            String dataPartida = data.getText().toString();
            String placar1Str = placar1.getText().toString();
            String placar2Str = placar2.getText().toString();

            if (dataPartida.isEmpty() || placar1Str.isEmpty() || placar2Str.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Partida p = new Partida();
            p.data = dataPartida;
            p.idJogador1 = jogadores.get(pos1).idJogador;
            p.idJogador2 = jogadores.get(pos2).idJogador;
            p.placarJogador1 = Integer.parseInt(placar1Str);
            p.placarJogador2 = Integer.parseInt(placar2Str);

            db.partidaDao().inserir(p);
            Toast.makeText(requireContext(), "Partida registrada", Toast.LENGTH_SHORT).show();

            limparCampos();
        });

        btnLimpar.setOnClickListener(v -> limparCampos());

        // Configuração do botão Buscar partidas de um jogador específico
        buscar.setOnClickListener(v -> {
            String textoBusca = filtro.getText().toString();
            Jogador j = db.jogadorDao().buscarPorNomeOuNickname(textoBusca);
            if (j != null) {
                List<Partida> partidas = db.partidaDao().listarPorJogador(j.idJogador);
                ArrayAdapter<String> partidasAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
                for (Partida p : partidas) {
                    partidasAdapter.add(p.data + " - " + p.placarJogador1 + " x " + p.placarJogador2);
                }
                listaPartidas.setAdapter(partidasAdapter);
            } else {
                Toast.makeText(requireContext(), "Jogador não encontrado", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void limparCampos() {
        data.setText("");
        placar1.setText("");
        placar2.setText("");
        spinnerJogador1.setSelection(0);
        spinnerJogador2.setSelection(0);
        filtro.setText("");
    }
}