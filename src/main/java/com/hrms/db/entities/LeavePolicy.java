package com.hrms.db.entities;

import jakarta.persistence.*;

/**
 * Leave policies per office/grade — defines max days, carry forward rules, etc.
 * Required by: Leave Management.
 */
@Entity
@Table(name = "leave_policies")
public class LeavePolicy {

    @Id
    @Column(name = "policy_id", length = 36)
    private String policyId;

    @Column(name = "policy_applies_to", length = 100)
    private String policyAppliesTo; // e.g. "ALL", department name, grade

    @Column(name = "effective_year")
    private Integer effectiveYear;

    @Column(name = "annual_leave_max_days")
    private Integer annualLeaveMaxDays;

    @Column(name = "annual_leave_max_carry_forward")
    private Integer annualLeaveMaxCarryForward;

    @Column(name = "annual_leave_min_notice_days")
    private Integer annualLeaveMinNoticeDays;

    @Column(name = "annual_leave_max_consecutive")
    private Integer annualLeaveMaxConsecutive;

    @Column(name = "sick_leave_max_days")
    private Integer sickLeaveMaxDays;

    @Column(name = "sick_leave_carry_forward_allowed")
    private Boolean sickLeaveCarryForwardAllowed = false;

    @Column(name = "sick_leave_doc_required_after")
    private Integer sickLeaveDocRequiredAfter;

    @Column(name = "casual_leave_max_days")
    private Integer casualLeaveMaxDays;

    @Column(name = "casual_leave_carry_forward_allowed")
    private Boolean casualLeaveCarryForwardAllowed = false;

    @Column(name = "casual_leave_half_day_allowed")
    private Boolean casualLeaveHalfDayAllowed = true;

    // --- Getters & Setters ---

    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }

    public String getPolicyAppliesTo() { return policyAppliesTo; }
    public void setPolicyAppliesTo(String policyAppliesTo) { this.policyAppliesTo = policyAppliesTo; }

    public Integer getEffectiveYear() { return effectiveYear; }
    public void setEffectiveYear(Integer effectiveYear) { this.effectiveYear = effectiveYear; }

    public Integer getAnnualLeaveMaxDays() { return annualLeaveMaxDays; }
    public void setAnnualLeaveMaxDays(Integer annualLeaveMaxDays) { this.annualLeaveMaxDays = annualLeaveMaxDays; }

    public Integer getAnnualLeaveMaxCarryForward() { return annualLeaveMaxCarryForward; }
    public void setAnnualLeaveMaxCarryForward(Integer v) { this.annualLeaveMaxCarryForward = v; }

    public Integer getAnnualLeaveMinNoticeDays() { return annualLeaveMinNoticeDays; }
    public void setAnnualLeaveMinNoticeDays(Integer v) { this.annualLeaveMinNoticeDays = v; }

    public Integer getAnnualLeaveMaxConsecutive() { return annualLeaveMaxConsecutive; }
    public void setAnnualLeaveMaxConsecutive(Integer v) { this.annualLeaveMaxConsecutive = v; }

    public Integer getSickLeaveMaxDays() { return sickLeaveMaxDays; }
    public void setSickLeaveMaxDays(Integer sickLeaveMaxDays) { this.sickLeaveMaxDays = sickLeaveMaxDays; }

    public Boolean getSickLeaveCarryForwardAllowed() { return sickLeaveCarryForwardAllowed; }
    public void setSickLeaveCarryForwardAllowed(Boolean v) { this.sickLeaveCarryForwardAllowed = v; }

    public Integer getSickLeaveDocRequiredAfter() { return sickLeaveDocRequiredAfter; }
    public void setSickLeaveDocRequiredAfter(Integer v) { this.sickLeaveDocRequiredAfter = v; }

    public Integer getCasualLeaveMaxDays() { return casualLeaveMaxDays; }
    public void setCasualLeaveMaxDays(Integer casualLeaveMaxDays) { this.casualLeaveMaxDays = casualLeaveMaxDays; }

    public Boolean getCasualLeaveCarryForwardAllowed() { return casualLeaveCarryForwardAllowed; }
    public void setCasualLeaveCarryForwardAllowed(Boolean v) { this.casualLeaveCarryForwardAllowed = v; }

    public Boolean getCasualLeaveHalfDayAllowed() { return casualLeaveHalfDayAllowed; }
    public void setCasualLeaveHalfDayAllowed(Boolean v) { this.casualLeaveHalfDayAllowed = v; }
}
