package com.bank.ejb;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Singleton Session Bean with Timer Service for scheduled report generation.
 * 
 * Key Features:
 * - Automatic scheduling with @Schedule annotation
 * - Programmatic timers with TimerService
 * - Persistent or non-persistent timers
 * - Cron-like expressions for flexible scheduling
 * - Asynchronous execution
 */
@Singleton
public class ReportGeneratorBean {
    
    private static final Logger LOGGER = Logger.getLogger(ReportGeneratorBean.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;
    
    @Resource
    private TimerService timerService;
    
    private int dailyReportCount = 0;
    private int hourlyReportCount = 0;
    
    /**
     * Generate daily report at midnight.
     * Runs every day at 00:00.
     * 
     * @Schedule attributes:
     * - hour: Hour of day (0-23)
     * - minute: Minute of hour (0-59)
     * - persistent: false = timer doesn't survive server restart
     */
    @Schedule(hour = "0", minute = "0", persistent = false, info = "Daily Report")
    public void generateDailyReport() {
        LOGGER.info("=== Generating Daily Report ===");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterday = now.minusDays(1);
            
            // Get transaction statistics
            Long transactionCount = getTransactionCount(yesterday, now);
            BigDecimal totalDeposits = getTotalDeposits(yesterday, now);
            BigDecimal totalWithdrawals = getTotalWithdrawals(yesterday, now);
            Long accountCount = getAccountCount();
            BigDecimal totalBalance = getTotalBalance();
            
            // Generate report
            StringBuilder report = new StringBuilder();
            report.append("\n╔════════════════════════════════════════════════════════╗\n");
            report.append("║           DAILY BANKING REPORT                         ║\n");
            report.append("╠════════════════════════════════════════════════════════╣\n");
            report.append(String.format("║ Date: %-48s ║\n", now.format(DATE_FORMATTER)));
            report.append("╠════════════════════════════════════════════════════════╣\n");
            report.append(String.format("║ Total Accounts: %-38d ║\n", accountCount));
            report.append(String.format("║ Total Balance: $%-37s ║\n", totalBalance));
            report.append("╠════════════════════════════════════════════════════════╣\n");
            report.append(String.format("║ Transactions (24h): %-33d ║\n", transactionCount));
            report.append(String.format("║ Total Deposits: $%-36s ║\n", totalDeposits));
            report.append(String.format("║ Total Withdrawals: $%-33s ║\n", totalWithdrawals));
            report.append(String.format("║ Net Change: $%-39s ║\n", 
                                       totalDeposits.subtract(totalWithdrawals)));
            report.append("╚════════════════════════════════════════════════════════╝\n");
            
            LOGGER.info(report.toString());
            
            dailyReportCount++;
            LOGGER.info("Daily report #" + dailyReportCount + " generated successfully");
            
        } catch (Exception e) {
            LOGGER.severe("Error generating daily report: " + e.getMessage());
        }
    }
    
    /**
     * Generate hourly summary.
     * Runs every hour at minute 0.
     */
    @Schedule(hour = "*", minute = "0", persistent = false, info = "Hourly Summary")
    public void generateHourlySummary() {
        LOGGER.info("=== Generating Hourly Summary ===");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourAgo = now.minusHours(1);
            
            Long transactionCount = getTransactionCount(oneHourAgo, now);
            BigDecimal totalDeposits = getTotalDeposits(oneHourAgo, now);
            BigDecimal totalWithdrawals = getTotalWithdrawals(oneHourAgo, now);
            
            LOGGER.info(String.format(
                "Hourly Summary [%s]: Transactions=%d, Deposits=$%s, Withdrawals=$%s",
                now.format(DATE_FORMATTER),
                transactionCount,
                totalDeposits,
                totalWithdrawals
            ));
            
            hourlyReportCount++;
            
        } catch (Exception e) {
            LOGGER.severe("Error generating hourly summary: " + e.getMessage());
        }
    }
    
    /**
     * Generate weekly report.
     * Runs every Monday at 08:00.
     */
    @Schedule(dayOfWeek = "Mon", hour = "8", minute = "0", persistent = false, info = "Weekly Report")
    public void generateWeeklyReport() {
        LOGGER.info("=== Generating Weekly Report ===");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneWeekAgo = now.minusWeeks(1);
            
            Long transactionCount = getTransactionCount(oneWeekAgo, now);
            BigDecimal totalDeposits = getTotalDeposits(oneWeekAgo, now);
            BigDecimal totalWithdrawals = getTotalWithdrawals(oneWeekAgo, now);
            
            LOGGER.info(String.format(
                "Weekly Report [%s]: Transactions=%d, Deposits=$%s, Withdrawals=$%s",
                now.format(DATE_FORMATTER),
                transactionCount,
                totalDeposits,
                totalWithdrawals
            ));
            
        } catch (Exception e) {
            LOGGER.severe("Error generating weekly report: " + e.getMessage());
        }
    }
    
    /**
     * Create a custom programmatic timer.
     * 
     * @param duration Duration in milliseconds
     * @param info Timer information
     */
    public void createCustomTimer(long duration, String info) {
        LOGGER.info(String.format("Creating custom timer: duration=%dms, info=%s", duration, info));
        timerService.createTimer(duration, info);
    }
    
    /**
     * Create an interval timer.
     * 
     * @param initialDuration Initial delay in milliseconds
     * @param intervalDuration Interval between executions in milliseconds
     * @param info Timer information
     */
    public void createIntervalTimer(long initialDuration, long intervalDuration, String info) {
        LOGGER.info(String.format("Creating interval timer: initial=%dms, interval=%dms, info=%s",
                                 initialDuration, intervalDuration, info));
        TimerConfig timerConfig = new TimerConfig(info, true);
        timerService.createIntervalTimer(initialDuration, intervalDuration, timerConfig);
    }
    
    /**
     * Handle programmatic timer expiration.
     * 
     * @param timer The expired timer
     */
    @Timeout
    public void handleTimeout(Timer timer) {
        String info = (String) timer.getInfo();
        LOGGER.info("Timer expired: " + info);
        
        // Process based on timer info
        if (info != null && info.startsWith("CUSTOM_REPORT")) {
            generateCustomReport(info);
        }
    }
    
    /**
     * Generate a custom report.
     * 
     * @param reportType The report type
     */
    private void generateCustomReport(String reportType) {
        LOGGER.info("Generating custom report: " + reportType);
        
        try {
            Long accountCount = getAccountCount();
            BigDecimal totalBalance = getTotalBalance();
            
            LOGGER.info(String.format("Custom Report [%s]: Accounts=%d, Total Balance=$%s",
                                     reportType, accountCount, totalBalance));
            
        } catch (Exception e) {
            LOGGER.severe("Error generating custom report: " + e.getMessage());
        }
    }
    
    /**
     * Get all active timers.
     * 
     * @return List of active timers
     */
    public List<Timer> getActiveTimers() {
        return (List<Timer>) timerService.getTimers();
    }
    
    /**
     * Cancel all timers.
     */
    public void cancelAllTimers() {
        for (Timer timer : timerService.getTimers()) {
            timer.cancel();
            LOGGER.info("Timer cancelled: " + timer.getInfo());
        }
    }
    
    /**
     * Get report statistics.
     * 
     * @return Statistics string
     */
    public String getStatistics() {
        return String.format("Daily Reports: %d, Hourly Reports: %d", 
                           dailyReportCount, hourlyReportCount);
    }
    
    // Helper methods for database queries
    
    private Long getTransactionCount(LocalDateTime start, LocalDateTime end) {
        return em.createQuery(
            "SELECT COUNT(t) FROM Transaction t WHERE t.timestamp BETWEEN :start AND :end", 
            Long.class)
            .setParameter("start", start)
            .setParameter("end", end)
            .getSingleResult();
    }
    
    private BigDecimal getTotalDeposits(LocalDateTime start, LocalDateTime end) {
        BigDecimal total = em.createQuery(
            "SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.type = :type AND t.timestamp BETWEEN :start AND :end",
            BigDecimal.class)
            .setParameter("type", TransactionType.DEPOSIT)
            .setParameter("start", start)
            .setParameter("end", end)
            .getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    private BigDecimal getTotalWithdrawals(LocalDateTime start, LocalDateTime end) {
        BigDecimal total = em.createQuery(
            "SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.type = :type AND t.timestamp BETWEEN :start AND :end",
            BigDecimal.class)
            .setParameter("type", TransactionType.WITHDRAWAL)
            .setParameter("start", start)
            .setParameter("end", end)
            .getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    private Long getAccountCount() {
        return em.createQuery("SELECT COUNT(a) FROM Account a", Long.class)
            .getSingleResult();
    }
    
    private BigDecimal getTotalBalance() {
        BigDecimal total = em.createQuery(
            "SELECT SUM(a.balance) FROM Account a", 
            BigDecimal.class)
            .getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }
}

// Made with Bob