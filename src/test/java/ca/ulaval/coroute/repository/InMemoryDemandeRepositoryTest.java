package ca.ulaval.coroute.repository;

import ca.ulaval.coroute.domain.model.Demande;
import ca.ulaval.coroute.domain.model.StatutDemande;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryDemandeRepositoryTest {

    private static final UUID TRAJET_ID = UUID.randomUUID();
    private static final String PASSAGER_ID = "passager-123";
    private static final String CONDUCTEUR_ID = "conducteur-456";

    private InMemoryDemandeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDemandeRepository();
    }

    @Test
    void save_puisFindById_retourneLaDemande() {
        final Demande demande = new Demande(TRAJET_ID, PASSAGER_ID, CONDUCTEUR_ID, "Bonjour");

        repository.save(demande);
        final Optional<Demande> result = repository.findById(demande.getId());

        assertTrue(result.isPresent());
        assertEquals(demande.getId(), result.get().getId());
    }

    @Test
    void findById_demandeAbsente_retourneOptionalVide() {
        assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void findByTrajetId_retourneDemandesDuTrajet() {
        final Demande d1 = new Demande(TRAJET_ID, PASSAGER_ID, CONDUCTEUR_ID, "");
        final Demande d2 = new Demande(UUID.randomUUID(), PASSAGER_ID, CONDUCTEUR_ID, "");
        repository.save(d1);
        repository.save(d2);

        final List<Demande> result = repository.findByTrajetId(TRAJET_ID);

        assertEquals(1, result.size());
        assertEquals(TRAJET_ID, result.get(0).getTrajetId());
    }

    @Test
    void findByPassagerId_retourneDemandesduPassager() {
        repository.save(new Demande(TRAJET_ID, PASSAGER_ID, CONDUCTEUR_ID, ""));
        repository.save(new Demande(TRAJET_ID, "autre-passager", CONDUCTEUR_ID, ""));

        final List<Demande> result = repository.findByPassagerId(PASSAGER_ID);

        assertEquals(1, result.size());
    }

    @Test
    void existeDemandeEnAttente_quandDemandeEnAttente_retourneTrue() {
        repository.save(new Demande(TRAJET_ID, PASSAGER_ID, CONDUCTEUR_ID, ""));

        assertTrue(repository.existeDemandeEnAttente(TRAJET_ID, PASSAGER_ID));
    }

    @Test
    void existeDemandeEnAttente_quandDemandeDecidee_retourneFalse() {
        final Demande demande = new Demande(TRAJET_ID, PASSAGER_ID, CONDUCTEUR_ID, "");
        demande.accepter(CONDUCTEUR_ID);
        repository.save(demande);

        assertFalse(repository.existeDemandeEnAttente(TRAJET_ID, PASSAGER_ID));
    }

    @Test
    void save_miseAJourDemande_remplaceLancienne() {
        final Demande demande = new Demande(TRAJET_ID, PASSAGER_ID, CONDUCTEUR_ID, "");
        repository.save(demande);
        demande.accepter(CONDUCTEUR_ID);
        repository.save(demande);

        final List<Demande> result = repository.findByTrajetId(TRAJET_ID);
        assertEquals(1, result.size());
        assertEquals(StatutDemande.ACCEPTEE, result.get(0).getStatut());
    }
}
