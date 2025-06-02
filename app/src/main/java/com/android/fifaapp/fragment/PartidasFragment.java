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
import java.util.Locale;
import java.util.List;

public class PartidasFragment extends Fragment {
    EditText data, placar1, placar2, filtro;
    Spinner spinnerJogador1, spinnerJogador2;
    Button btnSalvar, btnAtualizar, btnExcluir, btnLimpar, buscar, btnListarTodas;
    ListView listaPartidas;
    CampeonatoDatabase db;
    List<Jogador> jogadores;
    Integer idPartidaSelecionada = null;
    List<Partida> listaPartidasAtual;
    ArrayAdapter<Partida> partidasAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partidas, container, false);

        // Inicializando os componentes da UI
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
        btnListarTodas = view.findViewById(R.id.btnListarTodas);
        listaPartidas = view.findViewById(R.id.listViewPartidas);

        // Inicializando o banco de dados
        db = CampeonatoDatabase.getDatabase(requireContext());
        jogadores = db.jogadorDao().listarTodos();

        // Configurando os Spinners
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (Jogador j : jogadores) {
            spinnerAdapter.add(j.nickname);
        }
        spinnerJogador1.setAdapter(spinnerAdapter);
        spinnerJogador2.setAdapter(spinnerAdapter);

        // Configurando o adapter da lista de partidas
        partidasAdapter = new ArrayAdapter<Partida>(requireContext(), android.R.layout.simple_list_item_1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                Partida partida = getItem(position);
                if (partida != null) {
                    Jogador j1 = db.jogadorDao().buscarPorId(partida.idJogador1);
                    Jogador j2 = db.jogadorDao().buscarPorId(partida.idJogador2);

                    String texto = String.format(Locale.getDefault(), "%s - %s (%d) x (%d) %s",
                            partida.data,
                            j1 != null ? j1.nickname : "N/A",
                            partida.placarJogador1,
                            partida.placarJogador2,
                            j2 != null ? j2.nickname : "N/A");

                    textView.setText(texto);
                }
                return view;
            }
        };

        listaPartidas.setAdapter(partidasAdapter);
        carregarTodasPartidas();

        // Configurando o clique na lista
        listaPartidas.setOnItemClickListener((parent, view1, position, id) -> {
            Partida partidaSelecionada = partidasAdapter.getItem(position);
            if (partidaSelecionada != null) {
                preencherCamposPartida(partidaSelecionada);
            }
        });

        // Configuração dos botões
        btnSalvar.setOnClickListener(v -> salvarPartida());
        btnAtualizar.setOnClickListener(v -> atualizarPartida());
        btnExcluir.setOnClickListener(v -> excluirPartida());
        btnLimpar.setOnClickListener(v -> limparCampos());
        buscar.setOnClickListener(v -> buscarPartidas());
        btnListarTodas.setOnClickListener(v -> carregarTodasPartidas());

        return view;
    }

    private void carregarTodasPartidas() {
        listaPartidasAtual = db.partidaDao().listarTodas();
        partidasAdapter.clear();
        partidasAdapter.addAll(listaPartidasAtual);
        partidasAdapter.notifyDataSetChanged();
    }

    private void preencherCamposPartida(Partida partida) {
        idPartidaSelecionada = partida.idPartida;
        data.setText(partida.data);
        placar1.setText(String.valueOf(partida.placarJogador1));
        placar2.setText(String.valueOf(partida.placarJogador2));

        // Configurar os spinners
        for (int i = 0; i < jogadores.size(); i++) {
            if (jogadores.get(i).idJogador == partida.idJogador1) {
                spinnerJogador1.setSelection(i);
            }
            if (jogadores.get(i).idJogador == partida.idJogador2) {
                spinnerJogador2.setSelection(i);
            }
        }

        // Atualizar estado dos botões
        btnSalvar.setEnabled(false);
        btnAtualizar.setEnabled(true);
        btnExcluir.setEnabled(true);
    }

    private void salvarPartida() {
        if (!validarCampos()) return;

        Partida novaPartida = new Partida();
        novaPartida.data = data.getText().toString();
        novaPartida.idJogador1 = jogadores.get(spinnerJogador1.getSelectedItemPosition()).idJogador;
        novaPartida.idJogador2 = jogadores.get(spinnerJogador2.getSelectedItemPosition()).idJogador;
        novaPartida.placarJogador1 = Integer.parseInt(placar1.getText().toString());
        novaPartida.placarJogador2 = Integer.parseInt(placar2.getText().toString());

        db.partidaDao().inserir(novaPartida);
        Toast.makeText(requireContext(), "Partida registrada", Toast.LENGTH_SHORT).show();

        limparCampos();
        carregarTodasPartidas();
    }

    private void atualizarPartida() {
        if (idPartidaSelecionada == null) {
            Toast.makeText(requireContext(), "Nenhuma partida selecionada", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarCampos()) return;

        Partida partidaAtualizada = new Partida();
        partidaAtualizada.idPartida = idPartidaSelecionada;
        partidaAtualizada.data = data.getText().toString();
        partidaAtualizada.idJogador1 = jogadores.get(spinnerJogador1.getSelectedItemPosition()).idJogador;
        partidaAtualizada.idJogador2 = jogadores.get(spinnerJogador2.getSelectedItemPosition()).idJogador;
        partidaAtualizada.placarJogador1 = Integer.parseInt(placar1.getText().toString());
        partidaAtualizada.placarJogador2 = Integer.parseInt(placar2.getText().toString());

        db.partidaDao().atualizar(partidaAtualizada);
        Toast.makeText(requireContext(), "Partida atualizada", Toast.LENGTH_SHORT).show();

        limparCampos();
        carregarTodasPartidas();
    }

    private void excluirPartida() {
        if (idPartidaSelecionada == null) {
            Toast.makeText(requireContext(), "Nenhuma partida selecionada", Toast.LENGTH_SHORT).show();
            return;
        }

        Partida partidaParaExcluir = new Partida();
        partidaParaExcluir.idPartida = idPartidaSelecionada;

        db.partidaDao().deletar(partidaParaExcluir);
        Toast.makeText(requireContext(), "Partida excluída", Toast.LENGTH_SHORT).show();

        limparCampos();
        carregarTodasPartidas();
    }

    private void buscarPartidas() {
        String textoBusca = filtro.getText().toString();
        Jogador jogador = db.jogadorDao().buscarPorNomeOuNickname(textoBusca);

        if (jogador != null) {
            listaPartidasAtual = db.partidaDao().listarPorJogador(jogador.idJogador);
            partidasAdapter.clear();
            partidasAdapter.addAll(listaPartidasAtual);
            partidasAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(requireContext(), "Jogador não encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCampos() {
        if (spinnerJogador1.getSelectedItemPosition() == spinnerJogador2.getSelectedItemPosition()) {
            Toast.makeText(requireContext(), "Jogadores devem ser diferentes", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (data.getText().toString().isEmpty() ||
                placar1.getText().toString().isEmpty() ||
                placar2.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void limparCampos() {
        idPartidaSelecionada = null;
        data.setText("");
        placar1.setText("");
        placar2.setText("");
        spinnerJogador1.setSelection(0);
        spinnerJogador2.setSelection(0);
        filtro.setText("");

        btnSalvar.setEnabled(true);
        btnAtualizar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }
}