package com.android.fifaapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.android.fifaapp.entity.Partida;

import java.util.List;
@Dao
public interface PartidaDao {
    @Insert
    void inserir(Partida partida);

    @Update
    void atualizar(Partida partida);

    @Delete
    void deletar(Partida partida);

    @Query("SELECT * FROM Partida")
    List<Partida> listarTodas();

    @Query("SELECT * FROM Partida WHERE idJogador1 = :id OR idJogador2 = :id")
    List<Partida> listarPorJogador(int id);

}
