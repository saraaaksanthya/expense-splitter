package com.expensesplitter.dto;

public class BalanceDto {
    private Long personId;
    private String personName;
    private Double netBalance; // positive = is owed money, negative = owes money

    public BalanceDto(Long personId, String personName, Double netBalance) {
        this.personId = personId;
        this.personName = personName;
        this.netBalance = netBalance;
    }

    public Long getPersonId() {
        return personId;
    }

    public String getPersonName() {
        return personName;
    }

    public Double getNetBalance() {
        return netBalance;
    }
}
