package ca.ulaval.coroute.domain.exception;

public class PlacesInsuffisantesException extends RuntimeException {
    public PlacesInsuffisantesException() {
        super("Plus assez de places disponibles pour ce trajet.");
    }
}
