package br.ufpb.poo.brasileirao.match;

public enum KnockoutRound {
    ROUND_OF_32("Dezesseis-avos de Final", "R32"),
    ROUND_OF_16("Oitavas de Final", "R16"),
    QUARTERFINAL("Quartas de Final", "QF"),
    SEMIFINAL("Semifinal", "SF"),
    THIRD_PLACE("Disputa pelo 3º Lugar", "3P"),
    FINAL("Final", "F");

    private final String displayName;
    private final String code;

    KnockoutRound(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() { return displayName; }
    public String getCode() { return code; }
}
