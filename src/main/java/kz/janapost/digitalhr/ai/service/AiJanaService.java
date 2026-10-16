package kz.janapost.digitalhr.ai.service;

import kz.janapost.digitalhr.ai.dto.AiAnswerResponse;
import kz.janapost.digitalhr.ai.dto.AiQuestionRequest;
import kz.janapost.digitalhr.ai.entity.KnowledgeArticle;
import kz.janapost.digitalhr.ai.repository.KnowledgeBaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AiJanaService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    private static final String DEFAULT_NOT_FOUND_MESSAGE =
            "Я не нашел информации по вашему вопросу в официальных регламентах JanaPost. " +
            "Пожалуйста, обратитесь в HR-отдел: hr@janapost.kz или по телефону +7 (700) 123-45-12.";

    public AiAnswerResponse askQuestion(AiQuestionRequest request) {
        String query = request.getQuestion().trim().toLowerCase();

        List<KnowledgeArticle> allArticles = knowledgeBaseRepository.findAll();
        if (allArticles.isEmpty()) {
            return notFoundResponse();
        }

        String[] tokens = query.replaceAll("[^a-zA-Zа-яА-Я0-9\\s]", " ").split("\\s+");
        Set<String> queryWords = new HashSet<>();
        for (String t : tokens) {
            if (t.length() > 2) {
                queryWords.add(t);
            }
        }

        KnowledgeArticle bestMatch = null;
        int maxScore = 0;

        for (KnowledgeArticle article : allArticles) {
            int score = 0;
            String qText = article.getQuestion().toLowerCase();
            String kwText = article.getKeywords() != null ? article.getKeywords().toLowerCase() : "";
            String aText = article.getAnswer().toLowerCase();
            String catText = article.getCategory() != null ? article.getCategory().toLowerCase() : "";

            if (qText.contains(query) || query.contains(qText)) {
                score += 15;
            }

            for (String word : queryWords) {
                if (kwText.contains(word)) {
                    score += 5;
                }
                if (qText.contains(word)) {
                    score += 4;
                }
                if (catText.contains(word)) {
                    score += 3;
                }
                if (aText.contains(word)) {
                    score += 1;
                }
            }

            if (score > maxScore) {
                maxScore = score;
                bestMatch = article;
            }
        }

        if (bestMatch == null || maxScore < 3) {
            log.info("AI Jana: No reliable answer found for query: '{}'", request.getQuestion());
            return notFoundResponse();
        }

        double confidence = Math.min(1.0, 0.60 + (maxScore * 0.04));

        String source = "База знаний JanaPost — Раздел «" +
                (bestMatch.getCategory() != null ? bestMatch.getCategory() : "Общие регламенты") +
                "» (Трудовой кодекс РК / Внутренний трудовой распорядок)";

        return AiAnswerResponse.builder()
                .answer(bestMatch.getAnswer())
                .source(source)
                .category(bestMatch.getCategory())
                .found(true)
                .confidence(Math.round(confidence * 100.0) / 100.0)
                .build();
    }

    private AiAnswerResponse notFoundResponse() {
        return AiAnswerResponse.builder()
                .answer(DEFAULT_NOT_FOUND_MESSAGE)
                .source(null)
                .category(null)
                .found(false)
                .confidence(0.0)
                .build();
    }
}
