package br.ufpb.poo.brasileirao.tournament;

public enum TournamentPhase {
    NOT_STARTED,
    GROUP_STAGE,
    ROUND_OF_32,
    ROUND_OF_16,
    QUARTERFINAL,
    SEMIFINAL,
    FINISHED;

    public String getDisplayName() {
        return switch (this) {
            case NOT_STARTED -> "Não Iniciado";
            case GROUP_STAGE -> "Fase de Grupos";
            case ROUND_OF_32 -> "Dezesseis-avos de Final";
            case ROUND_OF_16 -> "Oitavas de Final";
            case QUARTERFINAL -> "Quartas de Final";
            case SEMIFINAL -> "Semifinal";
            case FINISHED -> "Encerrado";
        };
    }
}
