package com.example.geoquiz_v4_sqlite;

public class QuestoesDbSchema {

    // Definição da tabela de questões
    public static final class QuestoesTbl {
        public static final String NOME = "Questoes"; // Nome da tabela de questões

        // Colunas da tabela de questões
        public static final class Cols {
            public static final String UUID = "uuid"; // Identificação única da questão
            public static final String TEXTO_QUESTAO = "txt_questao"; // Texto da questão
            public static final String QUESTAO_CORRETA = "questao_correta"; // Se a questão é verdadeira ou falsa
        }
    }

    // Definição da tabela de respostas
    public static final class RespostasTbl {
        public static final String NOME = "Respostas"; // Nome da tabela de respostas

        // Colunas da tabela de respostas
        public static final class Cols {
            public static final String UUID_QUESTAO = "uuid_questao"; // UUID da questão respondida
            public static final String RESPOSTA_CORRETA = "resposta_correta"; // Se a resposta do usuário foi correta (0 ou 1)
            public static final String RESPOSTA_OFERECIDA = "resposta_oferecida"; // Resposta dada pelo usuário (verdadeiro ou falso)
            public static final String COLOU = "colou"; // Se o usuário colou (1 para sim, 0 para não)
        }
    }
}
