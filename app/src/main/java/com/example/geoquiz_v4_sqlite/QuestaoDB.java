package com.example.geoquiz_v4_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QuestaoDB {

    private Context mContext;
    private static Context mStaticContext;
    private SQLiteDatabase mDatabase;

    public QuestaoDB(Context contexto) {
        mContext = contexto.getApplicationContext();
        mStaticContext = mContext;
        mDatabase = new QuestoesDBHelper(mContext).getWritableDatabase();
    }

    // Método para adicionar uma questão ao banco de dados
    private static ContentValues getValoresConteudo(Questao q) {
        ContentValues valores = new ContentValues();
        valores.put(QuestoesDbSchema.QuestoesTbl.Cols.UUID, q.getId().toString());
        valores.put(QuestoesDbSchema.QuestoesTbl.Cols.TEXTO_QUESTAO,
                mStaticContext.getString(q.getTextoRespostaId())); // recupera valor do recurso string pelo id
        valores.put(QuestoesDbSchema.QuestoesTbl.Cols.QUESTAO_CORRETA, q.isRespostaCorreta());
        return valores;
    }

    public void addQuestao(Questao q) {
        ContentValues valores = getValoresConteudo(q);
        mDatabase.insert(QuestoesDbSchema.QuestoesTbl.NOME, null, valores);
    }

    // Método para adicionar uma resposta ao banco de dados
    public void addResposta(String uuidQuestao, int respostaCorreta, int respostaOferecida, int colou) {
        ContentValues valores = new ContentValues();
        valores.put(QuestoesDbSchema.RespostasTbl.Cols.UUID_QUESTAO, uuidQuestao);
        valores.put(QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_CORRETA, respostaCorreta);
        valores.put(QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_OFERECIDA, respostaOferecida);
        valores.put(QuestoesDbSchema.RespostasTbl.Cols.COLOU, colou);
        mDatabase.insert(QuestoesDbSchema.RespostasTbl.NOME, null, valores);
    }

    // Método para buscar todas as respostas
    public Cursor queryRespostas() {
        return mDatabase.query(QuestoesDbSchema.RespostasTbl.NOME,
                null,  // todas as colunas
                null,  // sem where
                null,  // sem argumentos
                null,  // sem group by
                null,  // sem having
                null   // sem order by
        );
    }

    // Método para deletar todas as respostas da tabela
    public void removeRespostas() {
        mDatabase.delete(QuestoesDbSchema.RespostasTbl.NOME, null, null);
    }

    // Outros métodos de query e atualização de questões (se necessário)
    public Cursor queryQuestao(String clausulaWhere, String[] argsWhere) {
        return mDatabase.query(QuestoesDbSchema.QuestoesTbl.NOME,
                null,  // todas as colunas
                clausulaWhere,
                argsWhere,
                null,  // sem group by
                null,  // sem having
                null   // sem order by
        );
    }

    public void removeBanco() {
        mDatabase.delete(QuestoesDbSchema.QuestoesTbl.NOME, null, null);
    }
}
