package com.example.geoquiz_v4_sqlite;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button mBotaoVerdadeiro;
    private Button mBotaoFalso;
    private Button mBotaoProximo;
    private Button mBotaoMostra;
    private Button mBotaoDeleta;
    private Button mBotaoCola;
    private TextView mTextViewQuestao;
    private TextView mTextViewQuestoesArmazenadas;

    private static final String TAG = "QuizActivity";
    private static final String CHAVE_INDICE = "INDICE";
    private static final int CODIGO_REQUISICAO_COLA = 0;

    private Questao[] mBancoDeQuestoes = new Questao[]{
            new Questao(R.string.questao_suez, true),
            new Questao(R.string.questao_alemanha, false)
    };

    QuestaoDB mQuestoesDb;
    private int mIndiceAtual = 0;
    private boolean mEhColador;

    @Override
    protected void onCreate(Bundle instanciaSalva) {
        super.onCreate(instanciaSalva);
        setContentView(R.layout.activity_main);

        if (instanciaSalva != null) {
            mIndiceAtual = instanciaSalva.getInt(CHAVE_INDICE, 0);
        }

        mTextViewQuestao = findViewById(R.id.view_texto_da_questao);
        atualizaQuestao();

        mBotaoVerdadeiro = findViewById(R.id.botao_verdadeiro);
        mBotaoVerdadeiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(true);
            }
        });

        mBotaoFalso = findViewById(R.id.botao_falso);
        mBotaoFalso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(false);
            }
        });

        mBotaoProximo = findViewById(R.id.botao_proximo);
        mBotaoProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIndiceAtual = (mIndiceAtual + 1) % mBancoDeQuestoes.length;
                mEhColador = false;
                atualizaQuestao();
            }
        });

        mBotaoCola = findViewById(R.id.botao_cola);
        mBotaoCola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean respostaEVerdadeira = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
                Intent intent = ColaActivity.novoIntent(MainActivity.this, respostaEVerdadeira);
                startActivityForResult(intent, CODIGO_REQUISICAO_COLA);
            }
        });

        mBotaoMostra = findViewById(R.id.botao_mostra_questoes);
        mBotaoMostra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostraRespostas();
            }
        });

        mBotaoDeleta = findViewById(R.id.botao_deleta);
        mBotaoDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletaRespostas();
            }
        });

        if (mQuestoesDb == null) {
            mQuestoesDb = new QuestaoDB(getBaseContext());
        }
    }

    private void atualizaQuestao() {
        int questao = mBancoDeQuestoes[mIndiceAtual].getTextoRespostaId();
        mTextViewQuestao.setText(questao);
    }

    private void verificaResposta(boolean respostaPressionada) {
        boolean respostaCorreta = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
        int idMensagemResposta = 0;

        if (mEhColador) {
            idMensagemResposta = R.string.toast_julgamento;
        } else {
            if (respostaPressionada == respostaCorreta) {
                idMensagemResposta = R.string.toast_correto;
            } else {
                idMensagemResposta = R.string.toast_incorreto;
            }
        }

        Toast.makeText(this, idMensagemResposta, Toast.LENGTH_SHORT).show();

        // Cadastra a resposta no banco de dados
        cadastraResposta(respostaPressionada, respostaCorreta);
    }

    private void cadastraResposta(boolean respostaOferecida, boolean respostaCorreta) {
        if (mQuestoesDb == null) return;

        String uuidQuestao = mBancoDeQuestoes[mIndiceAtual].getId().toString();
        int respostaCorretaInt = respostaCorreta ? 1 : 0;
        int respostaOferecidaInt = respostaOferecida ? 1 : 0;
        int colouInt = mEhColador ? 1 : 0;

        mQuestoesDb.addResposta(uuidQuestao, respostaCorretaInt, respostaOferecidaInt, colouInt);
    }

    private void mostraRespostas() {
        if (mQuestoesDb == null) return;
        if (mTextViewQuestoesArmazenadas == null) {
            mTextViewQuestoesArmazenadas = findViewById(R.id.texto_questoes_a_apresentar);
        } else {
            mTextViewQuestoesArmazenadas.setText("");
        }

        Cursor cursor = mQuestoesDb.queryRespostas();
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                mTextViewQuestoesArmazenadas.setText("Nada a apresentar");
            }
            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String uuid = cursor.getString(cursor.getColumnIndex(QuestoesDbSchema.RespostasTbl.Cols.UUID_QUESTAO));
                    int respostaCorreta = cursor.getInt(cursor.getColumnIndex(QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_CORRETA));
                    int respostaOferecida = cursor.getInt(cursor.getColumnIndex(QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_OFERECIDA));
                    int colou = cursor.getInt(cursor.getColumnIndex(QuestoesDbSchema.RespostasTbl.Cols.COLOU));

                    String respostaTexto = "UUID: " + uuid + ", Correta: " + respostaCorreta +
                            ", Oferecida: " + respostaOferecida + ", Colou: " + colou;
                    mTextViewQuestoesArmazenadas.append(respostaTexto + "\n");
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void deletaRespostas() {
        if (mQuestoesDb != null) {
            mQuestoesDb.removeRespostas();
            if (mTextViewQuestoesArmazenadas == null) {
                mTextViewQuestoesArmazenadas = findViewById(R.id.texto_questoes_a_apresentar);
            }
            mTextViewQuestoesArmazenadas.setText("");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle instanciaSalva) {
        super.onSaveInstanceState(instanciaSalva);
        instanciaSalva.putInt(CHAVE_INDICE, mIndiceAtual);
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent dados) {
        if (codigoResultado != Activity.RESULT_OK) {
            return;
        }
        if (codigoRequisicao == CODIGO_REQUISICAO_COLA) {
            if (dados == null) {
                return;
            }
            mEhColador = ColaActivity.foiMostradaResposta(dados);
        }
    }
}
