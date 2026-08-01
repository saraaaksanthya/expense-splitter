package com.expensesplitter.repository;

import com.expensesplitter.model.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
    boolean existsByPersonId(Long personId);
}