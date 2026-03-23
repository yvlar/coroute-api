package ca.ulaval.coroute.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InscriptionRequest(
        @NotBlank String nom,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6) String motDePasse
) {}