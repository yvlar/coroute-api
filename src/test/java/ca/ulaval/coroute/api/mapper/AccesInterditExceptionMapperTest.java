package ca.ulaval.coroute.api.mapper;

import ca.ulaval.coroute.domain.exception.AccesInterditException;
import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccesInterditExceptionMapperTest {

    private AccesInterditExceptionMapper mapper;
    private Response actualResponse;

    @BeforeEach
    void setUp() {
        this.mapper = new AccesInterditExceptionMapper();
    }

    @Test
    void givenAccesInterditException_whenToResponse_thenReturn403() {
        this.actualResponse = mapper.toResponse(new AccesInterditException());
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), this.actualResponse.getStatus());
    }

    @Test
    void givenAccesInterditException_whenToResponse_thenBodyContainsMessage() {
        this.actualResponse = mapper.toResponse(new AccesInterditException());
        assertAll(() -> {
            final ErrorResponse error = (ErrorResponse) this.actualResponse.getEntity();
            assertNotNull(error.message());
        });
    }
}
