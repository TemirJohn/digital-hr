package kz.janapost.digitalhr.hiring.dto;

import jakarta.validation.constraints.NotNull;
import kz.janapost.digitalhr.common.enums.CandidateStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageChangeRequest {

    @NotNull(message = "Candidate stage is required")
    private CandidateStage stage;
}
