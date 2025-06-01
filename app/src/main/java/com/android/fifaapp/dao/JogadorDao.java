package com.android.fifaapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.android.fifaapp.entity.Jogador;

import java.util.List;
@Dao
public interface JogadorDao {
    @Insert
    void inserir(Jogador jogador);

    @Update
    void atualizar(Jogador jogador);

    @Delete
    void deletar(Jogador jogador);

    @Query("SELECT * FROM Jogador")
    List<Jogador> listarTodos();

    @Query("SELECT * FROM Jogador WHERE nickname = :texto OR nome = :texto LIMIT 1")
    Jogador buscarPorNomeOuNickname(String texto);
}
