package ca.ulaval.coroute.dto.request;

import jakarta.validation.constraints.NotNull;

public record DecisionRequest(@NotNull
Boolean accepter) {
}
