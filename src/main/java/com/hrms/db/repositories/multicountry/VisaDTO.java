package com.hrms.db.repositories.multicountry;

import java.time.LocalDate;

public class VisaDTO {
    private String visaType;
    private String visaNumber;
    private LocalDate expiryDate;
    private String status;

    public VisaDTO() {
    }

    public String getVisaType() { return visaType; }
    public void setVisaType(String visaType) { this.visaType = visaType; }

    public String getVisaNumber() { return visaNumber; }
    public void setVisaNumber(String visaNumber) { this.visaNumber = visaNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
