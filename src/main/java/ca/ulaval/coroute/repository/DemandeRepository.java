package ca.ulaval.coroute.repository;

import ca.ulaval.coroute.domain.model.Demande;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DemandeRepository {

    void save(Demande demande);

    Optional<Demande> findById(UUID demandeId);

    List<Demande> findByTrajetId(UUID trajetId);

    List<Demande> findByPassagerId(String passagerId);

    boolean existeDemandeEnAttente(UUID trajetId, String passagerId);
}
