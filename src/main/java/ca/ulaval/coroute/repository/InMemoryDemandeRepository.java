package ca.ulaval.coroute.repository;

import ca.ulaval.coroute.domain.model.Demande;
import ca.ulaval.coroute.domain.model.StatutDemande;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryDemandeRepository implements DemandeRepository {

    private final List<Demande> demandes = new ArrayList<>();

    @Override
    public void save(final Demande demande) {
        demandes.removeIf(d -> d.getId().equals(demande.getId()));
        demandes.add(demande);
    }

    @Override
    public Optional<Demande> findById(final UUID demandeId) {
        return demandes.stream()
                .filter(d -> d.getId().equals(demandeId))
                .findFirst();
    }

    @Override
    public List<Demande> findByTrajetId(final UUID trajetId) {
        return demandes.stream()
                .filter(d -> d.getTrajetId().equals(trajetId))
                .toList();
    }

    @Override
    public List<Demande> findByPassagerId(final String passagerId) {
        return demandes.stream()
                .filter(d -> d.getPassagerId().equals(passagerId))
                .toList();
    }

    @Override
    public boolean existeDemandeEnAttente(final UUID trajetId, final String passagerId) {
        return demandes.stream()
                .anyMatch(d -> d.getTrajetId().equals(trajetId)
                        && d.getPassagerId().equals(passagerId)
                        && StatutDemande.EN_ATTENTE.equals(d.getStatut()));
    }
}
