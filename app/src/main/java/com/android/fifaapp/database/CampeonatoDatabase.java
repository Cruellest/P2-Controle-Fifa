package com.android.fifaapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.android.fifaapp.dao.JogadorDao;
import com.android.fifaapp.dao.PartidaDao;
import com.android.fifaapp.entity.Jogador;
import com.android.fifaapp.entity.Partida;

@Database(entities = {Jogador.class, Partida.class}, version = 1)
public abstract class CampeonatoDatabase extends RoomDatabase {
    private static CampeonatoDatabase INSTANCE;

    public abstract JogadorDao jogadorDao();
    public abstract PartidaDao partidaDao();

    public static CampeonatoDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    CampeonatoDatabase.class, "Campeonato").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}
