package com.example.reportingapi.repository;

import com.example.common.entity.Transaction;
import com.example.common.entity.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportingTransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.user.id = :userId " +
           "AND t.type = :type " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal sumAmountByUserIdAndTypeAndDateBetween(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.id = :accountId " +
           "AND t.type = :type " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal sumAmountByAccountIdAndTypeAndDateBetween(
            @Param("accountId") Long accountId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(t) FROM Transaction t " +
           "WHERE t.account.user.id = :userId " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    Integer countByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(t) FROM Transaction t " +
           "WHERE t.account.id = :accountId " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    Integer countByAccountIdAndDateBetween(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(t) FROM Transaction t " +
           "WHERE t.account.user.id = :userId " +
           "AND t.type = :type " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    Integer countByUserIdAndTypeAndDateBetween(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(t) FROM Transaction t " +
           "WHERE t.account.id = :accountId " +
           "AND t.type = :type " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    Integer countByAccountIdAndTypeAndDateBetween(
            @Param("accountId") Long accountId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.user.id = :userId " +
           "AND t.category.id = :categoryId " +
           "AND t.type = :type " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal sumAmountByUserIdAndCategoryAndTypeAndDateBetween(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t.category.id, t.category.name, SUM(t.amount) FROM Transaction t " +
           "WHERE t.account.user.id = :userId " +
           "AND t.type = 'EXPENSE' " +
           "AND t.date >= :startDate AND t.date <= :endDate " +
           "GROUP BY t.category.id, t.category.name")
    List<Object[]> sumExpenseGroupedByCategory(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t.category.id, t.category.name, SUM(t.amount), COUNT(t) FROM Transaction t " +
           "WHERE t.account.user.id = :userId " +
           "AND t.type = :type " +
           "AND t.date >= :startDate AND t.date <= :endDate " +
           "GROUP BY t.category.id, t.category.name " +
           "ORDER BY SUM(t.amount) DESC")
    List<Object[]> sumGroupedByCategory(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t.category.id, t.category.name, SUM(t.amount), COUNT(t) FROM Transaction t " +
           "WHERE t.account.id = :accountId " +
           "AND t.type = :type " +
           "AND t.date >= :startDate AND t.date <= :endDate " +
           "GROUP BY t.category.id, t.category.name " +
           "ORDER BY SUM(t.amount) DESC")
    List<Object[]> sumGroupedByCategoryAndAccount(
            @Param("accountId") Long accountId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.category " +
           "WHERE t.account.id = :accountId " +
           "AND t.date >= :startDate AND t.date <= :endDate " +
           "ORDER BY t.date ASC, t.id ASC")
    List<Transaction> findByAccountIdAndDateBetweenOrderByDateAscIdAsc(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
