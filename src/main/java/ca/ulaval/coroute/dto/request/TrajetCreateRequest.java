package ca.ulaval.coroute.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

public record TrajetCreateRequest(
        @NotBlank String depart,
        @NotBlank String destination,
        @NotNull LocalDate date,
        @NotNull LocalTime heure,
        @Min(1) int placesDisponibles,
        @Positive double prixParPassager
) {}
