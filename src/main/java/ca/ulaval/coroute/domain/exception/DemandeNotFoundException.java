package ca.ulaval.coroute.domain.exception;

import java.util.UUID;

public class DemandeNotFoundException extends RuntimeException {

    public DemandeNotFoundException(final UUID demandeId) {
        super("Demande introuvable : " + demandeId);
    }
}
