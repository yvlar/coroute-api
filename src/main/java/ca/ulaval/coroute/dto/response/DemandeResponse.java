package ca.ulaval.coroute.dto.response;

import ca.ulaval.coroute.domain.model.StatutDemande;

import java.time.LocalDateTime;
import java.util.UUID;

public record DemandeResponse(
        UUID id,
        UUID trajetId,
        String passagerId,
        String conducteurId,
        StatutDemande statut,
        String message,
        LocalDateTime creeLe,
        LocalDateTime decideLe) {
}
