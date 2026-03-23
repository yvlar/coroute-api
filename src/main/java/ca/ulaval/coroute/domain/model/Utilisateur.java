package ca.ulaval.coroute.domain.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

import java.util.UUID;

@Entity("utilisateurs")
public class Utilisateur {

    @Id
    private UUID id;

    @Property
    private String nom;

    @Property
    private String email;

    @Property
    private String motDePasseHash;

    // Constructeur vide requis par Morphia
    protected Utilisateur() {}

    public Utilisateur(final String nom,
                       final String email,
                       final String motDePasseHash) {
        this.id = UUID.randomUUID();
        this.nom = nom;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
    }

    public boolean verifierMotDePasse(final String motDePasseHash) {
        return this.motDePasseHash.equals(motDePasseHash);
    }

    public UUID getId() { return id; }
    public String getNom() { return nom; }
    public String getEmail() { return email; }
    public String getMotDePasseHash() { return motDePasseHash; }
}