package ca.ulaval.coroute.api.controller;

import ca.ulaval.coroute.config.AuthenticationFilter;
import ca.ulaval.coroute.domain.service.DemandeService;
import ca.ulaval.coroute.dto.request.DecisionRequest;
import ca.ulaval.coroute.dto.response.DemandeResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/demandes")
public class DecisionResource {

    @Inject
    private DemandeService demandeService;

    /**
     * Conducteur accepte ou refuse une demande.
     * PUT /demandes/{demandeId}/decision
     */
    @PUT
    @Path("/{demandeId}/decision")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response decider(
            @PathParam("demandeId")
            final UUID demandeId,
            @HeaderParam(AuthenticationFilter.USER_AUTH_HEADER)
            final String conducteurId,
            @Valid
            final DecisionRequest request) {

        final DemandeResponse response = demandeService.decider(demandeId, conducteurId, request);
        return Response.ok(response).build();
    }

    /**
     * Passager consulte ses propres demandes (pour voir les notifications de décision).
     * GET /demandes/mes-demandes
     */
    @GET
    @Path("/mes-demandes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response mesDemandes(
            @HeaderParam(AuthenticationFilter.USER_AUTH_HEADER)
            final String passagerId) {

        return Response.ok(demandeService.findByPassagerId(passagerId)).build();
    }
}
