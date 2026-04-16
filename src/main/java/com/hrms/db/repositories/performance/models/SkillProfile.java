package com.hrms.db.repositories.performance.models;

import java.util.HashMap;
import java.util.Map;

public class SkillProfile {
    private int employeeId;
    // skillId -> rating (1-5)
    private Map<Integer, Integer> skillRatings;

    public SkillProfile() {
        this.skillRatings = new HashMap<>();
    }

    public SkillProfile(int employeeId) {
        this.employeeId = employeeId;
        this.skillRatings = new HashMap<>();
    }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public Map<Integer, Integer> getSkillRatings() { return skillRatings; }
    public void setSkillRatings(Map<Integer, Integer> skillRatings) { this.skillRatings = skillRatings; }
    public void addSkillRating(int skillId, int rating) { this.skillRatings.put(skillId, rating); }

    @Override
    public String toString() {
        return "SkillProfile{employeeId=" + employeeId + ", skills=" + skillRatings + "}";
    }
}
