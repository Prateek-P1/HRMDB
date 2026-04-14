package com.hrms.db.repositories.performance.models;

import java.util.Date;

public class KPIRecord {
    private int recordId;
    private int kpiId;
    private double actualValue;
    private Date recordedDate;

    public KPIRecord() {}

    public KPIRecord(int recordId, int kpiId, double actualValue, Date recordedDate) {
        this.recordId = recordId;
        this.kpiId = kpiId;
        this.actualValue = actualValue;
        this.recordedDate = recordedDate;
    }

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public int getKpiId() { return kpiId; }
    public void setKpiId(int kpiId) { this.kpiId = kpiId; }
    public double getActualValue() { return actualValue; }
    public void setActualValue(double actualValue) { this.actualValue = actualValue; }
    public Date getRecordedDate() { return recordedDate; }
    public void setRecordedDate(Date recordedDate) { this.recordedDate = recordedDate; }

    @Override
    public String toString() {
        return "KPIRecord{kpiId=" + kpiId + ", value=" + actualValue + ", date=" + recordedDate + "}";
    }
}
