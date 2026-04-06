package ca.ulaval.coroute.domain.service;

import ca.ulaval.coroute.domain.exception.AccesInterditException;
import ca.ulaval.coroute.domain.exception.DemandeDejaExistanteException;
import ca.ulaval.coroute.domain.exception.DemandeNotFoundException;
import ca.ulaval.coroute.domain.exception.TrajetNotFoundException;
import ca.ulaval.coroute.domain.model.Demande;
import ca.ulaval.coroute.domain.model.DemandeFactory;
import ca.ulaval.coroute.domain.model.StatutDemande;
import ca.ulaval.coroute.domain.model.Trajet;
import ca.ulaval.coroute.dto.request.DemandeCreateRequest;
import ca.ulaval.coroute.dto.request.DecisionRequest;
import ca.ulaval.coroute.dto.response.DemandeResponse;
import ca.ulaval.coroute.repository.DemandeRepository;
import ca.ulaval.coroute.repository.TrajetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemandeServiceImplTest {

    private static final UUID TRAJET_ID = UUID.randomUUID();
    private static final UUID DEMANDE_ID = UUID.randomUUID();
    private static final String PASSAGER_ID = "passager-123";
    private static final String CONDUCTEUR_ID = "conducteur-456";
    private static final String MESSAGE = "Bonjour, puis-je rejoindre votre trajet ?";

    @Mock
    private DemandeRepository demandeRepository;
    @Mock
    private TrajetRepository trajetRepository;
    @Mock
    private DemandeFactory demandeFactory;
    @Mock
    private Trajet trajet;
    @Mock
    private Demande demande;

    private DemandeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DemandeServiceImpl(demandeRepository, trajetRepository, demandeFactory);
    }

    // ── envoyerDemande ──────────────────────────────────────────────────────

    @Test
    void envoyerDemande_quandTrajetExisteEtAucuneDemandeEnAttente_sauvegardeEtRetourneId() {
        when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.of(trajet));
        when(trajet.getConducteurId()).thenReturn(CONDUCTEUR_ID);
        when(demandeRepository.existeDemandeEnAttente(TRAJET_ID, PASSAGER_ID)).thenReturn(false);
        when(demandeFactory.creer(TRAJET_ID, PASSAGER_ID, CONDUCTEUR_ID, MESSAGE)).thenReturn(demande);
        when(demande.getId()).thenReturn(DEMANDE_ID);

        final UUID result = service.envoyerDemande(TRAJET_ID, PASSAGER_ID, new DemandeCreateRequest(MESSAGE));

        assertEquals(DEMANDE_ID, result);
        verify(demandeRepository).save(demande);
    }

    @Test
    void envoyerDemande_quandTrajetInexistant_leveTrajetNotFoundException() {
        when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.empty());

        assertThrows(TrajetNotFoundException.class,
                () -> service.envoyerDemande(TRAJET_ID, PASSAGER_ID, new DemandeCreateRequest(MESSAGE)));
    }

    @Test
    void envoyerDemande_quandPassagerEstConducteur_leveAccesInterditException() {
        when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.of(trajet));
        when(trajet.getConducteurId()).thenReturn(PASSAGER_ID); // même ID

        assertThrows(AccesInterditException.class,
                () -> service.envoyerDemande(TRAJET_ID, PASSAGER_ID, new DemandeCreateRequest(MESSAGE)));
    }

    @Test
    void envoyerDemande_quandDemandeDejaEnAttente_leveDemandeDejaExistanteException() {
        when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.of(trajet));
        when(trajet.getConducteurId()).thenReturn(CONDUCTEUR_ID);
        when(demandeRepository.existeDemandeEnAttente(TRAJET_ID, PASSAGER_ID)).thenReturn(true);

        assertThrows(DemandeDejaExistanteException.class,
                () -> service.envoyerDemande(TRAJET_ID, PASSAGER_ID, new DemandeCreateRequest(MESSAGE)));
    }

    // ── decider ─────────────────────────────────────────────────────────────

    @Test
    void decider_quandAcceptee_appelleAccepterEtAjouteReservation() {
        when(demandeRepository.findById(DEMANDE_ID)).thenReturn(Optional.of(demande));
        when(demande.getTrajetId()).thenReturn(TRAJET_ID);
        when(demande.getPassagerId()).thenReturn(PASSAGER_ID);
        when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.of(trajet));

        stubDemandeResponse();

        service.decider(DEMANDE_ID, CONDUCTEUR_ID, new DecisionRequest(true));

        verify(demande).accepter(CONDUCTEUR_ID);
        verify(trajet).ajouterReservation(PASSAGER_ID, 1);
        verify(trajetRepository).save(trajet);
        verify(demandeRepository).save(demande);
    }

    @Test
    void decider_quandRefusee_appelleRefuserSansAjouterReservation() {
        when(demandeRepository.findById(DEMANDE_ID)).thenReturn(Optional.of(demande));
        stubDemandeResponse();

        service.decider(DEMANDE_ID, CONDUCTEUR_ID, new DecisionRequest(false));

        verify(demande).refuser(CONDUCTEUR_ID);
        verify(trajetRepository, never()).save(any());
        verify(demandeRepository).save(demande);
    }

    @Test
    void decider_quandDemandeInexistante_leveDemandeNotFoundException() {
        when(demandeRepository.findById(DEMANDE_ID)).thenReturn(Optional.empty());

        assertThrows(DemandeNotFoundException.class,
                () -> service.decider(DEMANDE_ID, CONDUCTEUR_ID, new DecisionRequest(true)));
    }

    // ── findByTrajetId ───────────────────────────────────────────────────────

    @Test
    void findByTrajetId_quandConducteurProprietaire_retourneLaDemande() {
        when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.of(trajet));
        doNothing().when(trajet).verifierProprietaire(CONDUCTEUR_ID);
        when(demandeRepository.findByTrajetId(TRAJET_ID)).thenReturn(List.of(demande));
        stubDemandeResponse();

        final List<DemandeResponse> result = service.findByTrajetId(TRAJET_ID, CONDUCTEUR_ID);

        assertEquals(1, result.size());
    }

    // ── findByPassagerId ─────────────────────────────────────────────────────

    @Test
    void findByPassagerId_retourneDemandesduPassager() {
        when(demandeRepository.findByPassagerId(PASSAGER_ID)).thenReturn(List.of(demande));
        stubDemandeResponse();

        final List<DemandeResponse> result = service.findByPassagerId(PASSAGER_ID);

        assertEquals(1, result.size());
        assertEquals(DEMANDE_ID, result.get(0).id());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void stubDemandeResponse() {
        when(demande.getId()).thenReturn(DEMANDE_ID);
        when(demande.getTrajetId()).thenReturn(TRAJET_ID);
        when(demande.getPassagerId()).thenReturn(PASSAGER_ID);
        when(demande.getConducteurId()).thenReturn(CONDUCTEUR_ID);
        when(demande.getStatut()).thenReturn(StatutDemande.EN_ATTENTE);
        when(demande.getMessage()).thenReturn(MESSAGE);
        when(demande.getCreeLe()).thenReturn(null);
        when(demande.getDecideLe()).thenReturn(null);
    }
}
