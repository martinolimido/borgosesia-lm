package it.borgosesiaspa.model.enums;

public enum TipologiaRinnovo {
    //Per gestire i contratti 4 + 4 anni, 6 + 6 anni, 9 + 9 anni si usa tacito_al_primo_rinnovo, in questo modo al primo rinnovo il contratto diventa tacito e al secondo rinnovo diventa espresso
    tacito, tacito_al_primo_rinnovo, espresso;
}
