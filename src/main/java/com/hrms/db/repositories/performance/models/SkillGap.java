package com.hrms.db.repositories.performance.models;

public class SkillGap {
    private int skillId;
    private String skillName;
    private int currentRating;
    private int requiredRating;
    private int gapSize; // requiredRating - currentRating

    public SkillGap() {}

    public SkillGap(int skillId, String skillName, int currentRating, int requiredRating) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.currentRating = currentRating;
        this.requiredRating = requiredRating;
        this.gapSize = requiredRating - currentRating;
    }

    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public int getCurrentRating() { return currentRating; }
    public void setCurrentRating(int currentRating) { this.currentRating = currentRating; this.gapSize = requiredRating - currentRating; }
    public int getRequiredRating() { return requiredRating; }
    public void setRequiredRating(int requiredRating) { this.requiredRating = requiredRating; this.gapSize = requiredRating - currentRating; }
    public int getGapSize() { return gapSize; }

    @Override
    public String toString() {
        return "SkillGap{skill='" + skillName + "', current=" + currentRating + ", required=" + requiredRating + ", gap=" + gapSize + "}";
    }
}
