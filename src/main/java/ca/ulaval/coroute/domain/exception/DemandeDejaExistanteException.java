package ca.ulaval.coroute.domain.exception;

import java.util.UUID;

public class DemandeDejaExistanteException extends RuntimeException {

    public DemandeDejaExistanteException(final UUID trajetId) {
        super("Une demande est déjà en attente pour le trajet : " + trajetId);
    }
}
