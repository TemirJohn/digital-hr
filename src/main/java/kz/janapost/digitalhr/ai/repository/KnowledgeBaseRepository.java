package kz.janapost.digitalhr.ai.repository;

import kz.janapost.digitalhr.ai.entity.KnowledgeArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeArticle, Long> {

    @Query(value = "SELECT * FROM knowledge_base WHERE " +
            "LOWER(keywords) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(question) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "LIMIT 5", nativeQuery = true)
    List<KnowledgeArticle> searchByKeyword(String keyword);

    List<KnowledgeArticle> findByCategoryIgnoreCase(String category);
}
