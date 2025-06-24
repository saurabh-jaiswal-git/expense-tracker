package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.entity.Goal;
import com.expensetracker.expensetracker.entity.GoalStatus;
import com.expensetracker.expensetracker.entity.GoalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserId(Long userId);
    List<Goal> findByUserIdAndStatus(Long userId, GoalStatus status);
    List<Goal> findByUserIdAndGoalType(Long userId, GoalType goalType);
} 