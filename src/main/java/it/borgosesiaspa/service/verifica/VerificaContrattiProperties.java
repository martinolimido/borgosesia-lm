package it.borgosesiaspa.service.verifica;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configurazione del task giornaliero di verifica contratti.
 * Tutte le proprietà sono opzionali e hanno default ragionevoli.
 *
 * <pre>
 * borgosesia:
 *   verifica-contratti:
 *     enabled: true
 *     cron: "0 0 2 * * *"
 *     azioni-automatiche-abilitate: true
 *     morosita:
 *       soglia-giorni-aperta: 60
 *     deposito:
 *       massimo-mensilita: 3
 * </pre>
 */
@ConfigurationProperties(prefix = "borgosesia.verifica-contratti")
public class VerificaContrattiProperties {

    /**
     * Quando false, il job @Scheduled non esegue nulla (utile in dev/test).
     */
    private boolean enabled = true;

    /**
     * Cron Spring (6 campi). Default: 02:00 ogni giorno.
     */
    private String cron = "0 0 2 * * *";

    /**
     * Kill-switch globale: quando false, le regole con azione automatica
     * registrano solo l'anomalia ma non applicano l'azione.
     */
    private boolean azioniAutomaticheAbilitate = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public boolean isAzioniAutomaticheAbilitate() {
        return azioniAutomaticheAbilitate;
    }

    public void setAzioniAutomaticheAbilitate(boolean azioniAutomaticheAbilitate) {
        this.azioniAutomaticheAbilitate = azioniAutomaticheAbilitate;
    }
}
