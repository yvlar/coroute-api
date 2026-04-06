package ca.ulaval.coroute.api.mapper;

import ca.ulaval.coroute.domain.exception.DemandeDejaExistanteException;
import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DemandeDejaExistanteExceptionMapper implements ExceptionMapper<DemandeDejaExistanteException> {

    @Override
    public Response toResponse(final DemandeDejaExistanteException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
