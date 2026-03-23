package ca.ulaval.coroute.api.controller;

import ca.ulaval.coroute.domain.service.TrajetService;
import ca.ulaval.coroute.dto.request.ReservationCreateRequest;
import ca.ulaval.coroute.dto.request.TrajetCreateRequest;
import ca.ulaval.coroute.dto.response.ReservationResponse;
import ca.ulaval.coroute.dto.response.TrajetResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrajetResourceTest {

    private static final UUID TRAJET_ID = UUID.randomUUID();
    private static final UUID RESERVATION_ID = UUID.randomUUID();
    private static final String CONDUCTEUR_ID = "conducteur-123";
    private static final String PASSAGER_ID = "passager-456";
    private static final String DEPART = "Québec";
    private static final String DESTINATION = "Montréal";
    private static final String DATE = "2026-04-01";

    private static final TrajetCreateRequest TRAJET_CREATE_REQUEST = new TrajetCreateRequest(
            DEPART, DESTINATION, LocalDate.of(2026, 4, 1), LocalTime.of(8, 30), 3, 20.0
    );
    private static final ReservationCreateRequest RESERVATION_CREATE_REQUEST =
            new ReservationCreateRequest(1);

    @Mock
    private TrajetService trajetService;
    @Mock
    private UriInfo uriInfo;
    @Mock
    private UriBuilder uriBuilder;

    @InjectMocks
    private TrajetResource trajetResource;

    private Response actualResponse;

    @AfterEach
    public void tearDown() {
        this.actualResponse.close();
    }

    @Test
    void givenTrajetsInService_whenFindAll_thenReturn200WithListOfTrajets() {
        final TrajetResponse trajet1 = createTrajetResponse(TRAJET_ID, DEPART, DESTINATION);
        final TrajetResponse trajet2 = createTrajetResponse(UUID.randomUUID(), "Québec", "Sherbrooke");
        final List<TrajetResponse> expectedTrajets = List.of(trajet1, trajet2);

        when(trajetService.findAll(null, null, null)).thenReturn(expectedTrajets);

        this.actualResponse = trajetResource.findAll(null, null, null);

        assertAll(
                () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
                () -> {
                    final List<TrajetResponse> trajets = (List<TrajetResponse>) this.actualResponse.getEntity();
                    assertAll(
                            () -> assertEquals(expectedTrajets.size(), trajets.size()),
                            () -> assertEquals(DEPART, trajets.getFirst().depart()),
                            () -> assertEquals("Sherbrooke", trajets.getLast().destination())
                    );
                }
        );
    }

    @Test
    void givenNoTrajetsInService_whenFindAll_thenReturn200WithEmptyList() {
        when(trajetService.findAll(null, null, null)).thenReturn(List.of());

        this.actualResponse = trajetResource.findAll(null, null, null);

        assertAll(
                () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
                () -> assertTrue(((List<TrajetResponse>) this.actualResponse.getEntity()).isEmpty())
        );
    }

    @Test
    void givenFilters_whenFindAll_thenReturn200WithFilteredTrajets() {
        final List<TrajetResponse> expected = List.of(createTrajetResponse(TRAJET_ID, DEPART, DESTINATION));

        when(trajetService.findAll(DEPART, DESTINATION, DATE)).thenReturn(expected);

        this.actualResponse = trajetResource.findAll(DEPART, DESTINATION, DATE);

        assertAll(
                () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
                () -> assertEquals(1, ((List<TrajetResponse>) this.actualResponse.getEntity()).size())
        );
    }

    @Test
    void givenExistingTrajetId_whenGetTrajet_thenReturn200WithTrajet() {
        final TrajetResponse expected = createTrajetResponse(TRAJET_ID, DEPART, DESTINATION);

        when(trajetService.findById(TRAJET_ID)).thenReturn(expected);

        this.actualResponse = trajetResource.getTrajet(TRAJET_ID);

        assertAll(
                () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
                () -> assertEquals(expected, this.actualResponse.getEntity())
        );
    }

    @Test
    void givenValidRequest_whenCreateTrajet_thenReturn201WithLocation() {
        final String uri = "http://localhost:8080/trajets/" + TRAJET_ID;

        when(trajetService.createTrajet(CONDUCTEUR_ID, TRAJET_CREATE_REQUEST)).thenReturn(TRAJET_ID);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
        when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
        when(uriBuilder.build()).thenReturn(URI.create(uri));

        this.actualResponse = trajetResource.createTrajet(uriInfo, CONDUCTEUR_ID, TRAJET_CREATE_REQUEST);

        assertAll(
                () -> assertEquals(Response.Status.CREATED.getStatusCode(), this.actualResponse.getStatus()),
                () -> assertEquals(uri, this.actualResponse.getHeaderString("Location"))
        );
        verify(trajetService).createTrajet(CONDUCTEUR_ID, TRAJET_CREATE_REQUEST);
    }

    @Test
    void givenExistingTrajet_whenDeleteTrajet_thenReturn204AndServiceCalled() {
        this.actualResponse = trajetResource.deleteTrajet(TRAJET_ID, CONDUCTEUR_ID);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), this.actualResponse.getStatus());
        verify(trajetService).delete(TRAJET_ID, CONDUCTEUR_ID);
    }

    @Test
    void givenValidRequest_whenCreateReservation_thenReturn201WithLocation() {
        final String uri = "http://localhost:8080/trajets/" + TRAJET_ID + "/reservations/" + RESERVATION_ID;

        when(trajetService.addReservation(TRAJET_ID, PASSAGER_ID, RESERVATION_CREATE_REQUEST))
                .thenReturn(RESERVATION_ID);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
        when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
        when(uriBuilder.build()).thenReturn(URI.create(uri));

        this.actualResponse = trajetResource.createReservation(
                uriInfo, TRAJET_ID, PASSAGER_ID, RESERVATION_CREATE_REQUEST);

        assertAll(
                () -> assertEquals(Response.Status.CREATED.getStatusCode(), this.actualResponse.getStatus()),
                () -> assertEquals(uri, this.actualResponse.getHeaderString("Location"))
        );
        verify(trajetService).addReservation(TRAJET_ID, PASSAGER_ID, RESERVATION_CREATE_REQUEST);
    }

    @Test
    void givenExistingReservation_whenCancelReservation_thenReturn204AndServiceCalled() {
        this.actualResponse = trajetResource.cancelReservation(TRAJET_ID, RESERVATION_ID, PASSAGER_ID);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), this.actualResponse.getStatus());
        verify(trajetService).cancelReservation(TRAJET_ID, RESERVATION_ID, PASSAGER_ID);
    }

    @Test
    void givenExistingTrajet_whenGetReservations_thenReturn200WithList() {
        final List<ReservationResponse> expected = List.of(
                new ReservationResponse(RESERVATION_ID, PASSAGER_ID, 1)
        );

        when(trajetService.getReservations(TRAJET_ID, CONDUCTEUR_ID)).thenReturn(expected);

        this.actualResponse = trajetResource.getReservations(TRAJET_ID, CONDUCTEUR_ID);

        assertAll(
                () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
                () -> assertEquals(expected, this.actualResponse.getEntity())
        );
        verify(trajetService).getReservations(TRAJET_ID, CONDUCTEUR_ID);
    }

    private TrajetResponse createTrajetResponse(final UUID id, final String depart, final String destination) {
        return new TrajetResponse(id, depart, destination,
                LocalDate.of(2026, 4, 1), LocalTime.of(8, 30), 3, 20.0, "Marc T.");
    }
}
