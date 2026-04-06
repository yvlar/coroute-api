package ca.ulaval.coroute.domain.exception;

import java.util.UUID;

public class DemandeDejaDecideeException extends RuntimeException {

    public DemandeDejaDecideeException(final UUID demandeId) {
        super("La demande a déjà été traitée : " + demandeId);
    }
}
