package ca.ulaval.coroute.dto.request;

import ca.ulaval.coroute.domain.model.JourSemaine;

import java.util.List;

public record MatchingRequest(
        String depart,
        String destination,
        List<JourSemaine> jours) {
}
