package ca.ulaval.coroute.domain.model;

import ca.ulaval.coroute.domain.exception.AccesInterditException;
import ca.ulaval.coroute.domain.exception.PlacesInsuffisantesException;
import ca.ulaval.coroute.domain.exception.ReservationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrajetTest {

    private static final String CONDUCTEUR_ID = "conducteur-123";
    private static final String PASSAGER_ID = "passager-456";
    private static final String AUTRE_ID = "autre-789";
    private static final int PLACES = 3;
    private static final UUID RESERVATION_UUID = UUID.randomUUID();

    @Mock
    private ReservationFactory reservationFactory;
    @Mock
    private Reservation reservationMock;

    private Trajet trajet;

    @BeforeEach
    void setUp() {
        this.trajet = new Trajet(
                CONDUCTEUR_ID, "Québec", "Montréal",
                LocalDate.of(2026, 4, 1), LocalTime.of(8, 30),
                PLACES, 20.0, reservationFactory
        );
    }

    // ─── ajouterReservation ─────────────────────────────────────────────

    @Test
    void givenPlacesDisponibles_whenAjouterReservation_thenRetourneUUID() {
        when(reservationFactory.creer(PASSAGER_ID, 1)).thenReturn(reservationMock);
        when(reservationMock.getId()).thenReturn(RESERVATION_UUID);

        final UUID id = trajet.ajouterReservation(PASSAGER_ID, 1);

        assertNotNull(id);
        assertEquals(RESERVATION_UUID, id);
    }

    @Test
    void givenPlacesDisponibles_whenAjouterReservation_thenPlacesDiminuent() {
        when(reservationFactory.creer(PASSAGER_ID, 2)).thenReturn(reservationMock);
        when(reservationMock.getId()).thenReturn(RESERVATION_UUID);

        trajet.ajouterReservation(PASSAGER_ID, 2);

        assertEquals(1, trajet.getPlacesDisponibles());
    }

    @Test
    void givenPlacesInsuffisantes_whenAjouterReservation_thenLancePlacesInsuffisantesException() {
        assertThrows(PlacesInsuffisantesException.class,
                () -> trajet.ajouterReservation(PASSAGER_ID, 10));
    }

    // ─── annulerReservation ─────────────────────────────────────────────

    @Test
    void givenReservationExistante_whenAnnulerReservation_thenPlacesAugmentent() {
        when(reservationFactory.creer(PASSAGER_ID, 2)).thenReturn(reservationMock);
        when(reservationMock.getId()).thenReturn(RESERVATION_UUID);
        when(reservationMock.appartientA(PASSAGER_ID)).thenReturn(true);
        when(reservationMock.getNombrePlaces()).thenReturn(2);

        trajet.ajouterReservation(PASSAGER_ID, 2);
        trajet.annulerReservation(RESERVATION_UUID, PASSAGER_ID);

        assertEquals(PLACES, trajet.getPlacesDisponibles());
    }

    @Test
    void givenMauvaisPassager_whenAnnulerReservation_thenLanceAccesInterditException() {
        when(reservationFactory.creer(PASSAGER_ID, 1)).thenReturn(reservationMock);
        when(reservationMock.getId()).thenReturn(RESERVATION_UUID);
        when(reservationMock.appartientA(AUTRE_ID)).thenReturn(false);

        trajet.ajouterReservation(PASSAGER_ID, 1);

        assertThrows(AccesInterditException.class,
                () -> trajet.annulerReservation(RESERVATION_UUID, AUTRE_ID));
    }

    @Test
    void givenReservationInexistante_whenAnnulerReservation_thenLanceReservationNotFoundException() {
        assertThrows(ReservationNotFoundException.class,
                () -> trajet.annulerReservation(UUID.randomUUID(), PASSAGER_ID));
    }

    // ─── getReservations ────────────────────────────────────────────────

    @Test
    void givenConducteurValide_whenGetReservations_thenRetourneListe() {
        when(reservationFactory.creer(PASSAGER_ID, 1)).thenReturn(reservationMock);
        when(reservationMock.getId()).thenReturn(RESERVATION_UUID);

        trajet.ajouterReservation(PASSAGER_ID, 1);

        assertEquals(1, trajet.getReservations(CONDUCTEUR_ID).size());
    }

    @Test
    void givenMauvaisConducteur_whenGetReservations_thenLanceAccesInterditException() {
        assertThrows(AccesInterditException.class,
                () -> trajet.getReservations(AUTRE_ID));
    }

    // ─── verifierProprietaire ───────────────────────────────────────────

    @Test
    void givenBonConducteur_whenVerifierProprietaire_thenAucuneException() {
        trajet.verifierProprietaire(CONDUCTEUR_ID);
    }

    @Test
    void givenMauvaisConducteur_whenVerifierProprietaire_thenLanceAccesInterditException() {
        assertThrows(AccesInterditException.class,
                () -> trajet.verifierProprietaire(AUTRE_ID));
    }
}