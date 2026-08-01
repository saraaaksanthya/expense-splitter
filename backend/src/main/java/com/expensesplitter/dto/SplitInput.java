package com.expensesplitter.dto;

public class SplitInput {
    private Long personId;
    private Double shareAmount; // if null, expense is split equally among participants

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Double getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(Double shareAmount) {
        this.shareAmount = shareAmount;
    }
}
