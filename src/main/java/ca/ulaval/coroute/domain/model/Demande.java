package ca.ulaval.coroute.domain.model;

import ca.ulaval.coroute.domain.exception.AccesInterditException;
import ca.ulaval.coroute.domain.exception.DemandeDejaDecideeException;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity("demandes")
public class Demande {

    @Id
    private UUID id;

    @Property
    private UUID trajetId;

    @Property
    private String passagerId;

    @Property
    private String conducteurId;

    @Property
    private StatutDemande statut;

    @Property
    private String message;

    @Property
    private LocalDateTime creeLe;

    @Property
    private LocalDateTime decideLe;

    /**
     * Constructeur vide requis par Morphia pour la désérialisation.
     * Ne pas utiliser directement - utiliser DemandeFactory.
     */
    protected Demande() {
        // Required by Morphia for deserialization
    }

    public Demande(
            final UUID trajetId,
            final String passagerId,
            final String conducteurId,
            final String message) {
        this.id = UUID.randomUUID();
        this.trajetId = trajetId;
        this.passagerId = passagerId;
        this.conducteurId = conducteurId;
        this.statut = StatutDemande.EN_ATTENTE;
        this.message = message;
        this.creeLe = LocalDateTime.now();
    }

    public void accepter(final String candidatConducteurId) {
        verifierConducteur(candidatConducteurId);
        verifierEnAttente();
        this.statut = StatutDemande.ACCEPTEE;
        this.decideLe = LocalDateTime.now();
    }

    public void refuser(final String candidatConducteurId) {
        verifierConducteur(candidatConducteurId);
        verifierEnAttente();
        this.statut = StatutDemande.REFUSEE;
        this.decideLe = LocalDateTime.now();
    }

    public boolean estEnAttente() {
        return StatutDemande.EN_ATTENTE.equals(this.statut);
    }

    public boolean estAcceptee() {
        return StatutDemande.ACCEPTEE.equals(this.statut);
    }

    private void verifierConducteur(final String candidatConducteurId) {
        if (!this.conducteurId.equals(candidatConducteurId)) {
            throw new AccesInterditException("décider d'une demande dont vous n'êtes pas le conducteur");
        }
    }

    private void verifierEnAttente() {
        if (!estEnAttente()) {
            throw new DemandeDejaDecideeException(this.id);
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getTrajetId() {
        return trajetId;
    }

    public String getPassagerId() {
        return passagerId;
    }

    public String getConducteurId() {
        return conducteurId;
    }

    public StatutDemande getStatut() {
        return statut;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreeLe() {
        return creeLe;
    }

    public LocalDateTime getDecideLe() {
        return decideLe;
    }
}
