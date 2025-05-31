package com.android.fifaapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(tableName = "Jogador", indices = {@Index(value = {"nickname"}, unique = true)})
public class Jogador {
    @PrimaryKey(autoGenerate = true)
    public int idJogador;

    public String nome;
    public String nickname;
    public String email;
    public String dataNascimento;
}
