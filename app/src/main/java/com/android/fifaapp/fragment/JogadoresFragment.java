package com.android.fifaapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.fifaapp.R;
import com.android.fifaapp.database.CampeonatoDatabase;
import com.android.fifaapp.entity.Jogador;

import java.util.List;

public class JogadoresFragment extends Fragment {

    private EditText nome, nickname, email, dataNascimento;
    private Button salvar, atualizar, deletar;
    private ListView listaJogadores;
    private ArrayAdapter<String> adapter;

    private CampeonatoDatabase db;

    public JogadoresFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jogadores, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referências de UI
        nome = view.findViewById(R.id.nome);
        nickname = view.findViewById(R.id.nickname);
        email = view.findViewById(R.id.email);
        dataNascimento = view.findViewById(R.id.dataNascimento);
        salvar = view.findViewById(R.id.btnSalvar);
        atualizar = view.findViewById(R.id.btnAtualizar);
        deletar = view.findViewById(R.id.btnDeletar);
        listaJogadores = view.findViewById(R.id.listViewJogadores);

        // DB
        db = CampeonatoDatabase.getDatabase(requireContext());

        // Listar jogadores
        carregarJogadores();

        // Salvar
        salvar.setOnClickListener(v -> {
            Jogador j = new Jogador();
            j.nome = nome.getText().toString();
            j.nickname = nickname.getText().toString();
            j.email = email.getText().toString();
            j.dataNascimento = dataNascimento.getText().toString();
            db.jogadorDao().inserir(j);
            Toast.makeText(getContext(), "Jogador salvo", Toast.LENGTH_SHORT).show();
            carregarJogadores();
        });

        // Atualizar
        atualizar.setOnClickListener(v -> {
            Jogador j = db.jogadorDao().buscarPorNomeOuNickname(nickname.getText().toString());
            if (j != null) {
                j.nome = nome.getText().toString();
                j.email = email.getText().toString();
                j.dataNascimento = dataNascimento.getText().toString();
                db.jogadorDao().atualizar(j);
                Toast.makeText(getContext(), "Jogador atualizado", Toast.LENGTH_SHORT).show();
                carregarJogadores();
            }
        });

        // Deletar
        deletar.setOnClickListener(v -> {
            Jogador j = db.jogadorDao().buscarPorNomeOuNickname(nickname.getText().toString());
            if (j != null) {
                db.jogadorDao().deletar(j);
                Toast.makeText(getContext(), "Jogador deletado", Toast.LENGTH_SHORT).show();
                carregarJogadores();
            }
        });
    }

    private void carregarJogadores() {
        List<Jogador> jogadores = db.jogadorDao().listarTodos();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        for (Jogador j : jogadores) {
            adapter.add(j.nickname + " - " + j.nome);
        }
        listaJogadores.setAdapter(adapter);
    }
}