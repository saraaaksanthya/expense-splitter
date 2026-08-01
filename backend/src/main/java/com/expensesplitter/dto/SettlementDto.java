package com.expensesplitter.dto;

public class SettlementDto {
    private Long fromPersonId;
    private String fromPersonName;
    private Long toPersonId;
    private String toPersonName;
    private Double amount;

    public SettlementDto(Long fromPersonId, String fromPersonName,
                          Long toPersonId, String toPersonName, Double amount) {
        this.fromPersonId = fromPersonId;
        this.fromPersonName = fromPersonName;
        this.toPersonId = toPersonId;
        this.toPersonName = toPersonName;
        this.amount = amount;
    }

    public Long getFromPersonId() {
        return fromPersonId;
    }

    public String getFromPersonName() {
        return fromPersonName;
    }

    public Long getToPersonId() {
        return toPersonId;
    }

    public String getToPersonName() {
        return toPersonName;
    }

    public Double getAmount() {
        return amount;
    }
}
