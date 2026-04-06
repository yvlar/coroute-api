package ca.ulaval.coroute.domain.model;

import java.util.UUID;

public class DemandeFactory {

    public Demande creer(
            final UUID trajetId,
            final String passagerId,
            final String conducteurId,
            final String message) {
        return new Demande(trajetId, passagerId, conducteurId, message);
    }
}
