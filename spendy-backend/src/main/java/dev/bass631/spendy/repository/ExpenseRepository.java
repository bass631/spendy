package dev.bass631.spendy.repository;

import dev.bass631.spendy.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    Page<Expense> findByCategoryIdOrderByCreatedAtDesc(UUID categoryId, Pageable pageable);

    List<Expense> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime from, LocalDateTime to
    );

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0) FROM Expense e
            WHERE e.createdAt >= :from
                AND e.createdAt <= :to
            """)
    BigDecimal getTotalByPeriod(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
            SELECT c.name AS category,
                   COALESCE(SUM(e.amount), 0) AS total,
                   COUNT(e.id) AS count
            FROM Category c
            LEFT JOIN Expense e ON e.category.id = c.id
                AND e.createdAt >= :from
                AND e.createdAt <= :to
            GROUP BY c.id, c.name
            ORDER BY total DESC
            """)
    List<CategoryStatProjection> getCategoryStatsByPeriod(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    void deleteByCategoryId(UUID categoryId);

    interface CategoryStatProjection {
        String getCategory();
        BigDecimal getTotal();
        Long getCount();
    }
}
