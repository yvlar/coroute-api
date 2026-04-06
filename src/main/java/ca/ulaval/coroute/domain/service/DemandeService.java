package ca.ulaval.coroute.domain.service;

import ca.ulaval.coroute.dto.request.DemandeCreateRequest;
import ca.ulaval.coroute.dto.request.DecisionRequest;
import ca.ulaval.coroute.dto.response.DemandeResponse;

import java.util.List;
import java.util.UUID;

public interface DemandeService {

    UUID envoyerDemande(UUID trajetId, String passagerId, DemandeCreateRequest request);

    DemandeResponse decider(UUID demandeId, String conducteurId, DecisionRequest request);

    List<DemandeResponse> findByTrajetId(UUID trajetId, String conducteurId);

    List<DemandeResponse> findByPassagerId(String passagerId);
}
