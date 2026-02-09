package it.borgosesiaspa.security;

import java.util.Set;

public class Utente {
    private String username;
    private String password;
    private String email;

    public String getEmail() {
        return email;
    }

    private boolean attivo;
    private Set<String> ruoli;

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRuoli() {
        return ruoli;
    }

    public boolean isAttivo() {
        return attivo;
    }

}
