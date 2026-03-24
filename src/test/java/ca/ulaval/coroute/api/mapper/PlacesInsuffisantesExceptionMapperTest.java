package ca.ulaval.coroute.api.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.ulaval.coroute.domain.exception.PlacesInsuffisantesException;
import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlacesInsuffisantesExceptionMapperTest {

  private PlacesInsuffisantesExceptionMapper mapper;
  private Response actualResponse;

  @BeforeEach
  void setUp() {
    this.mapper = new PlacesInsuffisantesExceptionMapper();
  }

  @Test
  void givenPlacesInsuffisantesException_whenToResponse_thenReturn409() {
    this.actualResponse = mapper.toResponse(new PlacesInsuffisantesException());
    assertEquals(Response.Status.CONFLICT.getStatusCode(), this.actualResponse.getStatus());
  }

  @Test
  void givenPlacesInsuffisantesException_whenToResponse_thenBodyContainsMessage() {
    this.actualResponse = mapper.toResponse(new PlacesInsuffisantesException());
    assertAll(
        () -> {
          final ErrorResponse error = (ErrorResponse) this.actualResponse.getEntity();
          assertNotNull(error.message());
        });
  }
}
