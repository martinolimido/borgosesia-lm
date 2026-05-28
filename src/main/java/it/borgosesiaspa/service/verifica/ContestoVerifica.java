package it.borgosesiaspa.service.verifica;

import java.time.LocalDate;

/**
 * Contesto comune passato a tutte le {@link ContrattoRule} durante un run del
 * task giornaliero di verifica.
 *
 * <p>Mantiene riferimenti che le regole devono condividere (es. data di
 * riferimento, configurazione globale). Espresso come oggetto immutabile per
 * facilitare il testing — è possibile costruirlo passando una {@code oggi} fissa
 * indipendente dal clock di sistema.</p>
 */
public final class ContestoVerifica {

    private final LocalDate oggi;
    private final boolean azioniAutomaticheAbilitate;

    public ContestoVerifica(LocalDate oggi, boolean azioniAutomaticheAbilitate) {
        this.oggi = oggi;
        this.azioniAutomaticheAbilitate = azioniAutomaticheAbilitate;
    }

    public LocalDate getOggi() {
        return oggi;
    }

    public boolean isAzioniAutomaticheAbilitate() {
        return azioniAutomaticheAbilitate;
    }
}
