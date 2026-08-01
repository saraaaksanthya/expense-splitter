package com.expensesplitter.service;

import com.expensesplitter.dto.ExpenseRequest;
import com.expensesplitter.dto.SplitInput;
import com.expensesplitter.exception.InvalidExpenseException;
import com.expensesplitter.model.Expense;
import com.expensesplitter.model.ExpenseSplit;
import com.expensesplitter.model.Group;
import com.expensesplitter.model.Person;
import com.expensesplitter.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseService {

    private static final double EPSILON = 0.01;

    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;
    private final PersonService personService;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, GroupService groupService, PersonService personService) {
        this.expenseRepository = expenseRepository;
        this.groupService = groupService;
        this.personService = personService;
    }

    public Expense addExpense(Long groupId, ExpenseRequest request) {
        Group group = groupService.findById(groupId);
        Person paidBy = personService.findById(request.getPaidById());

        Expense expense = new Expense();
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setGroup(group);
        expense.setPaidBy(paidBy);

        List<ExpenseSplit> splits = buildSplits(expense, request);
        validateSplitsSumToAmount(splits, request.getAmount());
        expense.setSplits(splits);

        return expenseRepository.save(expense);
    }

    /**
     * Builds the list of splits for an expense.
     * - If explicit `splits` (with shareAmount) are given, use them as-is.
     * - Otherwise split the amount equally among `participantIds`.
     */
    private List<ExpenseSplit> buildSplits(Expense expense, ExpenseRequest request) {
        List<ExpenseSplit> splits = new ArrayList<>();

        if (request.getSplits() != null && !request.getSplits().isEmpty()) {
            for (SplitInput s : request.getSplits()) {
                Person person = personService.findById(s.getPersonId());
                splits.add(new ExpenseSplit(expense, person, s.getShareAmount()));
            }
            return splits;
        }

        if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
            throw new InvalidExpenseException("Either participantIds or splits must be provided");
        }

        int n = request.getParticipantIds().size();
        // Split equally, but adjust the last share so the total exactly matches (avoids rounding drift)
        double equalShare = round2(request.getAmount() / n);
        double runningTotal = 0.0;

        for (int i = 0; i < n; i++) {
            Person person = personService.findById(request.getParticipantIds().get(i));
            double share;
            if (i == n - 1) {
                share = round2(request.getAmount() - runningTotal);
            } else {
                share = equalShare;
                runningTotal += share;
            }
            splits.add(new ExpenseSplit(expense, person, share));
        }
        return splits;
    }

    private void validateSplitsSumToAmount(List<ExpenseSplit> splits, Double amount) {
        double sum = splits.stream().mapToDouble(ExpenseSplit::getShareAmount).sum();
        if (Math.abs(sum - amount) > EPSILON) {
            throw new InvalidExpenseException(
                    String.format("Split amounts (%.2f) do not add up to the expense amount (%.2f)", sum, amount));
        }
    }

    public List<Expense> getExpensesForGroup(Long groupId) {
        return expenseRepository.findByGroupId(groupId);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
