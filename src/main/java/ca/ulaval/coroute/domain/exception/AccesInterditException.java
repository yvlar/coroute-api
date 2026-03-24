package ca.ulaval.coroute.domain.exception;

public class AccesInterditException extends RuntimeException {
  public AccesInterditException() {
    super("Accès interdit : vous n'êtes pas autorisé à effectuer cette action.");
  }
}
