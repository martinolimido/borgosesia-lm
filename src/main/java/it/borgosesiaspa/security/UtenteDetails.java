package it.borgosesiaspa.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UtenteDetails implements UserDetails {

    private final Utente utente;

    public UtenteDetails(Utente utente) {
        this.utente = utente;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> collection = new ArrayList<>();
        utente.getRuoli().forEach(c -> {
            collection.add(new SimpleGrantedAuthority(c));
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return utente.getPassword();
    }

    @Override
    public String getUsername() {
        return utente.getUsername();
    }

    // Indica se l'account è attivo, non scaduto, ecc.
    @Override
    public boolean isAccountNonExpired() {
        return utente.isAttivo();
    }

    @Override
    public boolean isAccountNonLocked() {
        return utente.isAttivo();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return utente.isAttivo();
    }

    @Override
    public boolean isEnabled() {
        return utente.isAttivo();
    }

    // Facoltativo: se ti servono i campi del tuo entity
    public Utente getUtente() {
        return this.utente;
    }
}
