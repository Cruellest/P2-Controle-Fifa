package com.android.fifaapp.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(
        tableName = "Partida",
        foreignKeys = {
                @ForeignKey(entity = Jogador.class, parentColumns = "idJogador", childColumns = "idJogador1", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Jogador.class, parentColumns = "idJogador", childColumns = "idJogador2", onDelete = ForeignKey.CASCADE)
        }
)
public class Partida {
    @PrimaryKey(autoGenerate = true)
    public int idPartida;

    public String data;
    public int idJogador1;
    public int idJogador2;
    public int placarJogador1;
    public int placarJogador2;
}
