package com.hrms.db.repositories.multicountry;

public class EmploymentTypeDTO {
    private String code;
    private String name;

    public EmploymentTypeDTO() {
    }

    public EmploymentTypeDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
