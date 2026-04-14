package com.hrms.db.repositories.performance.models;

import java.util.List;
import java.util.Map;

public class Skill {
    private int skillId;
    private String name;
    private String category;

    public Skill() {}

    public Skill(int skillId, String name, String category) {
        this.skillId = skillId;
        this.name = name;
        this.category = category;
    }

    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return "Skill{id=" + skillId + ", name='" + name + "', category='" + category + "'}";
    }
}
