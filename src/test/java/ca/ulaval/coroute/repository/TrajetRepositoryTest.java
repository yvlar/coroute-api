package ca.ulaval.coroute.repository;

import ca.ulaval.coroute.domain.model.Trajet;
import ca.ulaval.coroute.domain.model.TrajetFactory;
import ca.ulaval.coroute.domain.model.ReservationFactory;
import ca.ulaval.coroute.dto.request.TrajetCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TrajetRepositoryTest {

    private static final String CONDUCTEUR_ID = "conducteur-123";
    private static final TrajetCreateRequest REQUEST = new TrajetCreateRequest(
            "Quebec", "Montreal",
            LocalDate.of(2026, 4, 1),
            LocalTime.of(8, 30),
            3, 20.0
    );

    protected TrajetRepository repository;
    protected TrajetFactory trajetFactory;

    protected abstract TrajetRepository createTrajetRepository();

    @BeforeEach
    void setUp() {
        this.trajetFactory = new TrajetFactory(new ReservationFactory());
        this.repository = createTrajetRepository();
    }

    @Test
    void givenTrajet_whenSave_thenTrouvableParId() {
        final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, REQUEST);
        repository.save(trajet);

        final Optional<Trajet> result = repository.findById(trajet.getId());

        assertTrue(result.isPresent());
    }

    @Test
    void givenIdInexistant_whenFindById_thenRetourneEmpty() {
        final Optional<Trajet> result = repository.findById(UUID.randomUUID());
        assertFalse(result.isPresent());
    }

    @Test
    void givenTrajetsEnregistres_whenFindAll_thenRetourneTous() {
        repository.save(trajetFactory.creer(CONDUCTEUR_ID, REQUEST));
        repository.save(trajetFactory.creer(CONDUCTEUR_ID, REQUEST));

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void givenAucunTrajet_whenFindAll_thenRetourneListeVide() {
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void givenFiltreDepart_whenFindByFiltres_thenRetourneTrajetsCorrespondants() {
        repository.save(trajetFactory.creer(CONDUCTEUR_ID, REQUEST));

        final List<Trajet> result = repository.findByFiltres("Quebec", null, null);

        assertEquals(1, result.size());
    }

    @Test
    void givenTrajetExistant_whenDelete_thenPlusTrouvable() {
        final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, REQUEST);
        repository.save(trajet);
        repository.delete(trajet.getId());

        assertFalse(repository.findById(trajet.getId()).isPresent());
    }
}