package ca.ulaval.coroute.domain.model;

import ca.ulaval.coroute.domain.exception.AccesInterditException;
import ca.ulaval.coroute.domain.exception.DemandeDejaDecideeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemandeTest {

    private static final UUID TRAJET_ID = UUID.randomUUID();
    private static final String PASSAGER_ID = "passager-123";
    private static final String CONDUCTEUR_ID = "conducteur-456";
    private static final String AUTRE_ID = "intrus-789";

    private Demande demande;

    @BeforeEach
    void setUp() {
        demande = new Demande(TRAJET_ID, PASSAGER_ID, CONDUCTEUR_ID, "Bonjour !");
    }

    @Test
    void nouvelledemande_aStatutEnAttente() {
        assertEquals(StatutDemande.EN_ATTENTE, demande.getStatut());
        assertTrue(demande.estEnAttente());
    }

    @Test
    void accepter_parLeConducteur_changesStatutAAcceptee() {
        demande.accepter(CONDUCTEUR_ID);

        assertEquals(StatutDemande.ACCEPTEE, demande.getStatut());
        assertTrue(demande.estAcceptee());
        assertNotNull(demande.getDecideLe());
    }

    @Test
    void refuser_parLeConducteur_changesStatutARefusee() {
        demande.refuser(CONDUCTEUR_ID);

        assertEquals(StatutDemande.REFUSEE, demande.getStatut());
        assertNotNull(demande.getDecideLe());
    }

    @Test
    void accepter_parUnAutreUtilisateur_leveAccesInterditException() {
        assertThrows(AccesInterditException.class, () -> demande.accepter(AUTRE_ID));
    }

    @Test
    void refuser_parUnAutreUtilisateur_leveAccesInterditException() {
        assertThrows(AccesInterditException.class, () -> demande.refuser(AUTRE_ID));
    }

    @Test
    void accepter_demandeDejaDecidee_leveDemandeDejaDecideeException() {
        demande.accepter(CONDUCTEUR_ID);

        assertThrows(DemandeDejaDecideeException.class, () -> demande.accepter(CONDUCTEUR_ID));
    }

    @Test
    void refuser_demandeDejaDecidee_leveDemandeDejaDecideeException() {
        demande.refuser(CONDUCTEUR_ID);

        assertThrows(DemandeDejaDecideeException.class, () -> demande.refuser(CONDUCTEUR_ID));
    }
}
