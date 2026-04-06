package ca.ulaval.coroute.repository;

import ca.ulaval.coroute.domain.model.Demande;
import ca.ulaval.coroute.domain.model.StatutDemande;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MongoDemandeRepository implements DemandeRepository {

    private final Datastore datastore;

    @Inject
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Injected by DI, thread-safe")
    public MongoDemandeRepository(final Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public void save(final Demande demande) {
        this.datastore.save(demande);
    }

    @Override
    public Optional<Demande> findById(final UUID demandeId) {
        return Optional.ofNullable(
                this.datastore.find(Demande.class)
                        .filter(Filters.eq("_id", demandeId))
                        .first());
    }

    @Override
    public List<Demande> findByTrajetId(final UUID trajetId) {
        return this.datastore.find(Demande.class)
                .filter(Filters.eq("trajetId", trajetId))
                .iterator()
                .toList();
    }

    @Override
    public List<Demande> findByPassagerId(final String passagerId) {
        return this.datastore.find(Demande.class)
                .filter(Filters.eq("passagerId", passagerId))
                .iterator()
                .toList();
    }

    @Override
    public boolean existeDemandeEnAttente(final UUID trajetId, final String passagerId) {
        return this.datastore.find(Demande.class)
                .filter(
                        Filters.eq("trajetId", trajetId),
                        Filters.eq("passagerId", passagerId),
                        Filters.eq("statut", StatutDemande.EN_ATTENTE))
                .count() > 0;
    }
}
