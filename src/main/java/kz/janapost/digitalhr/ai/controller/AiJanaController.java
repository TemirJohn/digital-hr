package kz.janapost.digitalhr.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.janapost.digitalhr.ai.dto.AiAnswerResponse;
import kz.janapost.digitalhr.ai.dto.AiQuestionRequest;
import kz.janapost.digitalhr.ai.service.AiJanaService;
import kz.janapost.digitalhr.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Jana", description = "AI Corporate HR assistant answering strictly based on verified company knowledge base")
public class AiJanaController {

    private final AiJanaService aiJanaService;

    @PostMapping("/ask")
    @Operation(summary = "Ask question to AI Jana corporate assistant (returns answer, source regulation citation and confidence)")
    public ApiResponse<AiAnswerResponse> askQuestion(@Valid @RequestBody AiQuestionRequest request) {
        return ApiResponse.ok(aiJanaService.askQuestion(request));
    }
}
