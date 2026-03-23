package ca.ulaval.coroute.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record TrajetResponse(
        UUID id,
        String depart,
        String destination,
        LocalDate date,
        LocalTime heure,
        int placesRestantes,
        double prixParPassager,
        String conducteurNom
) {}
