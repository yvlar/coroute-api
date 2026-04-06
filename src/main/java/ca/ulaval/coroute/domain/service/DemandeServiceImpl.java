package ca.ulaval.coroute.domain.service;

import ca.ulaval.coroute.domain.exception.DemandeDejaExistanteException;
import ca.ulaval.coroute.domain.exception.DemandeNotFoundException;
import ca.ulaval.coroute.domain.exception.TrajetNotFoundException;
import ca.ulaval.coroute.domain.model.Demande;
import ca.ulaval.coroute.domain.model.DemandeFactory;
import ca.ulaval.coroute.domain.model.Trajet;
import ca.ulaval.coroute.dto.request.DemandeCreateRequest;
import ca.ulaval.coroute.dto.request.DecisionRequest;
import ca.ulaval.coroute.dto.response.DemandeResponse;
import ca.ulaval.coroute.repository.DemandeRepository;
import ca.ulaval.coroute.repository.TrajetRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

public class DemandeServiceImpl implements DemandeService {

    private final DemandeRepository demandeRepository;
    private final TrajetRepository trajetRepository;
    private final DemandeFactory demandeFactory;

    @Inject
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Injected by DI framework")
    public DemandeServiceImpl(
            final DemandeRepository demandeRepository,
            final TrajetRepository trajetRepository,
            final DemandeFactory demandeFactory) {
        this.demandeRepository = demandeRepository;
        this.trajetRepository = trajetRepository;
        this.demandeFactory = demandeFactory;
    }

    @Override
    public UUID envoyerDemande(
            final UUID trajetId,
            final String passagerId,
            final DemandeCreateRequest request) {

        final Trajet trajet = trajetRepository.findById(trajetId)
                .orElseThrow(() -> new TrajetNotFoundException(trajetId));

        trajet.verifierProprietaire(trajet.getConducteurId()); // chargé, juste pour accès au conducteurId
        final String conducteurId = trajet.getConducteurId();

        if (conducteurId.equals(passagerId)) {
            throw new ca.ulaval.coroute.domain.exception.AccesInterditException(
                    "envoyer une demande pour son propre trajet");
        }

        if (demandeRepository.existeDemandeEnAttente(trajetId, passagerId)) {
            throw new DemandeDejaExistanteException(trajetId);
        }

        final Demande demande = demandeFactory.creer(
                trajetId,
                passagerId,
                conducteurId,
                request.message());

        demandeRepository.save(demande);
        return demande.getId();
    }

    @Override
    public DemandeResponse decider(
            final UUID demandeId,
            final String conducteurId,
            final DecisionRequest request) {

        final Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new DemandeNotFoundException(demandeId));

        if (Boolean.TRUE.equals(request.accepter())) {
            demande.accepter(conducteurId);
            // Créer la réservation automatiquement lors de l'acceptation
            final Trajet trajet = trajetRepository.findById(demande.getTrajetId())
                    .orElseThrow(() -> new TrajetNotFoundException(demande.getTrajetId()));
            trajet.ajouterReservation(demande.getPassagerId(), 1);
            trajetRepository.save(trajet);
        } else {
            demande.refuser(conducteurId);
        }

        demandeRepository.save(demande);
        return toResponse(demande);
    }

    @Override
    public List<DemandeResponse> findByTrajetId(final UUID trajetId, final String conducteurId) {
        final Trajet trajet = trajetRepository.findById(trajetId)
                .orElseThrow(() -> new TrajetNotFoundException(trajetId));
        trajet.verifierProprietaire(conducteurId);
        return demandeRepository.findByTrajetId(trajetId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<DemandeResponse> findByPassagerId(final String passagerId) {
        return demandeRepository.findByPassagerId(passagerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private DemandeResponse toResponse(final Demande demande) {
        return new DemandeResponse(
                demande.getId(),
                demande.getTrajetId(),
                demande.getPassagerId(),
                demande.getConducteurId(),
                demande.getStatut(),
                demande.getMessage(),
                demande.getCreeLe(),
                demande.getDecideLe());
    }
}
