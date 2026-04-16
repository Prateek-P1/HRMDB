package com.hrms.db.repositories.performance.interfaces;

import com.hrms.db.repositories.performance.models.DeptReport;
import com.hrms.db.repositories.performance.models.Employee;
import com.hrms.db.repositories.performance.models.ProgressReport;
import com.hrms.db.repositories.performance.models.SkillGapSummary;
import java.util.List;

/**
 * IReportRepository
 * Component: Analytics & Reporting Component
 *
 * Provides aggregated query methods for dashboards and performance reports.
 * DB Team must implement this interface and provide the concrete class.
 * NOTE: These methods may involve joining multiple tables — please optimise queries.
 */
public interface IReportRepository {

    /**
     * Aggregate and return a performance summary report for an entire department.
     * Includes average rating, appraisal completion count, and top performers.
     * @param deptId  department to report on
     * @param cycleId the performance cycle to report on
     * @return DeptReport with aggregated data, or null if no data found
     */
    DeptReport getDeptPerformanceSummary(int deptId, int cycleId);

    /**
     * Return the top N performers in a department for a cycle, ranked by appraisal rating.
     * @param deptId  department to query
     * @param cycleId performance cycle
     * @param n       number of top performers to return
     * @return list of up to N Employee objects sorted by rating descending; empty list if none
     */
    List<Employee> getTopPerformers(int deptId, int cycleId, int n);

    /**
     * Fetch a combined goal and KPI progress summary for an employee in a cycle.
     * @param employeeId employee to report on
     * @param cycleId    performance cycle
     * @return ProgressReport with goal completion stats and KPI list; null if not found
     */
    ProgressReport getEmployeeProgressReport(int employeeId, int cycleId);

    /**
     * Calculate the percentage of appraisals that have been completed (status = APPROVED)
     * out of all appraisals created in a cycle.
     * @param cycleId the performance cycle
     * @return completion rate as a double between 0.0 and 1.0 (e.g. 0.75 = 75%)
     */
    double getAppraisalCompletionRate(int cycleId);

    /**
     * Aggregate skill gaps across all employees in a department.
     * @param deptId department to analyse
     * @return list of SkillGapSummary objects, one per employee; empty list if none
     */
    List<SkillGapSummary> getSkillGapSummary(int deptId);
}
