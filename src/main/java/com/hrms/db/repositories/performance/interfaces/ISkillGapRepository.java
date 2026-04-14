package com.hrms.db.repositories.performance.interfaces;

import com.hrms.db.repositories.performance.models.Skill;
import com.hrms.db.repositories.performance.models.SkillGap;
import com.hrms.db.repositories.performance.models.SkillProfile;
import java.util.List;

/**
 * ISkillGapRepository
 * Component: Skill Gap Analysis Component
 *
 * Stores employee skill assessments and required competency benchmarks.
 * DB Team must implement this interface and provide the concrete class.
 */
public interface ISkillGapRepository {

    /**
     * Retrieve the current skill profile (all skill ratings) of an employee.
     * @param employeeId employee whose skill profile is needed
     * @return SkillProfile containing a map of skillId -> rating; null if not found
     */
    SkillProfile getSkillProfile(int employeeId);

    /**
     * Update an employee's rating for a specific skill.
     * If no rating exists for that skill, one should be created.
     * @param employeeId employee to update
     * @param skillId    skill being rated
     * @param rating     rating value (1 = beginner, 5 = expert)
     * @return true if updated successfully, false otherwise
     */
    boolean updateSkillRating(int employeeId, int skillId, int rating);

    /**
     * Fetch the list of skills required for a given role/job profile.
     * @param roleId role identifier (maps to a job profile in the DB)
     * @return list of required Skill objects with their minimum required ratings; empty list if none
     */
    List<Skill> getRequiredSkills(String roleId);

    /**
     * Compute and return the skill gaps for an employee vs a target role.
     * Only returns skills where currentRating < requiredRating.
     * @param employeeId employee to evaluate
     * @param roleId     target role to compare against
     * @return list of SkillGap objects (skills the employee is lacking); empty list if no gaps
     */
    List<SkillGap> getSkillGaps(int employeeId, String roleId);

    /**
     * Retrieve the full skills taxonomy catalog available in the system.
     * @return list of all Skill objects; empty list if none
     */
    List<Skill> getAllSkills();
}
