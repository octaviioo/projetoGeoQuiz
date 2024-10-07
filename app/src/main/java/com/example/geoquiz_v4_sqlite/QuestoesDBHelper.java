package com.example.geoquiz_v4_sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuestoesDBHelper extends SQLiteOpenHelper {

    private static final int VERSAO = 2; // Aumente a versão do banco de dados
    private static final String NOME_DATABASE = "questoesDB"; // Nome do banco de dados

    public QuestoesDBHelper(Context context) {
        super(context, NOME_DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação da tabela "questoes" no banco de dados
        db.execSQL("CREATE TABLE " + QuestoesDbSchema.QuestoesTbl.NOME + " (" +
                "_id integer PRIMARY KEY autoincrement, " + // Coluna ID auto-incrementada
                QuestoesDbSchema.QuestoesTbl.Cols.UUID + " TEXT, " + // Coluna UUID
                QuestoesDbSchema.QuestoesTbl.Cols.QUESTAO_CORRETA + " INTEGER, " + // Coluna para verificar se a questão foi correta
                QuestoesDbSchema.QuestoesTbl.Cols.TEXTO_QUESTAO + " TEXT" + // Coluna para o texto da questão
                ")"
        );
        Log.d("QuestoesDBHelper", "Tabela questoes criada.");

        // Criação da tabela "respostas" no banco de dados
        db.execSQL("CREATE TABLE " + QuestoesDbSchema.RespostasTbl.NOME + " (" +
                "_id integer PRIMARY KEY autoincrement, " + // Coluna ID auto-incrementada
                QuestoesDbSchema.RespostasTbl.Cols.UUID_QUESTAO + " TEXT, " + // Coluna UUID da questão
                QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_CORRETA + " INTEGER, " + // Coluna se a resposta foi correta
                QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_OFERECIDA + " INTEGER, " + // Coluna se a resposta oferecida foi verdadeira ou falsa
                QuestoesDbSchema.RespostasTbl.Cols.COLOU + " INTEGER" + // Coluna se o usuário colou
                ")"
        );
        Log.d("QuestoesDBHelper", "Tabela respostas criada.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int novaVersao) {
        // Política de upgrade: descartar o conteúdo da tabela e recriá-la
        db.execSQL("DROP TABLE IF EXISTS " + QuestoesDbSchema.QuestoesTbl.NOME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestoesDbSchema.RespostasTbl.NOME);
        onCreate(db); // Recriar o banco de dados
    }
}
