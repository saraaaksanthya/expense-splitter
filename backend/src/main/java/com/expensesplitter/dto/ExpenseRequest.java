package com.expensesplitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class ExpenseRequest {

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @NotNull(message = "paidById is required")
    private Long paidById;

    // list of personIds who share this expense.
    // If splits (with explicit shareAmount) is not provided, the amount is split equally.
    private List<Long> participantIds;

    private List<SplitInput> splits;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getPaidById() {
        return paidById;
    }

    public void setPaidById(Long paidById) {
        this.paidById = paidById;
    }

    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }

    public List<SplitInput> getSplits() {
        return splits;
    }

    public void setSplits(List<SplitInput> splits) {
        this.splits = splits;
    }
}
