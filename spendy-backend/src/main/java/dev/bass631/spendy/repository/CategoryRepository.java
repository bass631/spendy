package dev.bass631.spendy.repository;

import dev.bass631.spendy.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByName(String name);

    List<Category> findAllByOrderByNameAsc();

    @Query("""
            SELECT c FROM Category c
            LEFT JOIN Expense e ON e.category.id = c.id
                AND e.createdAt >= :since
            GROUP BY c.id, c.name, c.createdAt, c.version
            ORDER BY COUNT(e.id) DESC, c.name ASC
            """)
    List<Category> findAllSortedByFrequencySince(@Param("since") LocalDateTime since);
}
