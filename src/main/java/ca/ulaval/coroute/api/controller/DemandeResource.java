package ca.ulaval.coroute.api.controller;

import ca.ulaval.coroute.config.AuthenticationFilter;
import ca.ulaval.coroute.domain.service.DemandeService;
import ca.ulaval.coroute.dto.request.DemandeCreateRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.UUID;

@Path("/trajets")
public class DemandeResource {

    @Inject
    private DemandeService demandeService;

    /**
     * Passager envoie une demande pour rejoindre un trajet.
     * POST /trajets/{trajetId}/demandes
     */
    @POST
    @Path("/{trajetId}/demandes")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response envoyerDemande(
            @Context
            final UriInfo uriInfo,
            @PathParam("trajetId")
            final UUID trajetId,
            @HeaderParam(AuthenticationFilter.USER_AUTH_HEADER)
            final String passagerId,
            final DemandeCreateRequest request) {

        final UUID demandeId = demandeService.envoyerDemande(trajetId, passagerId, request);
        final URI location = uriInfo.getBaseUriBuilder()
                .path("demandes")
                .path(String.valueOf(demandeId))
                .build();
        return Response.created(location).build();
    }

    /**
     * Conducteur consulte les demandes reçues pour son trajet.
     * GET /trajets/{trajetId}/demandes
     */
    @GET
    @Path("/{trajetId}/demandes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDemandes(
            @PathParam("trajetId")
            final UUID trajetId,
            @HeaderParam(AuthenticationFilter.USER_AUTH_HEADER)
            final String conducteurId) {

        return Response.ok(demandeService.findByTrajetId(trajetId, conducteurId)).build();
    }
}
