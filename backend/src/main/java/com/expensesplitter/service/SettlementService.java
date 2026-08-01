package com.expensesplitter.service;

import com.expensesplitter.dto.BalanceDto;
import com.expensesplitter.dto.SettlementDto;
import com.expensesplitter.model.Expense;
import com.expensesplitter.model.ExpenseSplit;
import com.expensesplitter.model.Group;
import com.expensesplitter.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Computes "who owes whom" for a group using a greedy debt-simplification
 * algorithm (a common variant of the "min cash flow" problem).
 *
 * Step 1: For every person, compute a net balance:
 *         netBalance = totalPaid - totalOwed
 *         (positive => is owed money, negative => owes money)
 *
 * Step 2: Repeatedly match the person who is owed the MOST money with the
 *         person who owes the MOST money, and settle the smaller of the two
 *         amounts between them. This greedy approach minimizes the number
 *         of transactions needed to settle the whole group.
 *
 * Complexity: O(n log n) per iteration using two max-heaps, O(n) iterations
 * in the worst case => O(n^2 log n) overall, which is more than fine for
 * typical group sizes (a handful to a few dozen people).
 */
@Service
public class SettlementService {

    private static final double EPSILON = 0.01;

    private final ExpenseService expenseService;
    private final GroupService groupService;

    @Autowired
    public SettlementService(ExpenseService expenseService, GroupService groupService) {
        this.expenseService = expenseService;
        this.groupService = groupService;
    }

    public List<BalanceDto> getBalances(Long groupId) {
        Map<Person, Double> netBalances = computeNetBalances(groupId);
        List<BalanceDto> result = new ArrayList<>();
        for (Map.Entry<Person, Double> entry : netBalances.entrySet()) {
            result.add(new BalanceDto(entry.getKey().getId(), entry.getKey().getName(), round2(entry.getValue())));
        }
        return result;
    }

    public List<SettlementDto> settleGroup(Long groupId) {
        Map<Person, Double> netBalances = computeNetBalances(groupId);
        return simplifyDebts(netBalances);
    }

    /**
     * netBalance[person] = sum(amount they paid across all expenses)
     *                     - sum(their share across all expense splits)
     */
    private Map<Person, Double> computeNetBalances(Long groupId) {
        Group group = groupService.findById(groupId);
        List<Expense> expenses = expenseService.getExpensesForGroup(groupId);

        Map<Person, Double> balances = new HashMap<>();
        for (Person p : group.getMembers()) {
            balances.put(p, 0.0);
        }

        for (Expense expense : expenses) {
            // the payer is credited the full amount they fronted
            balances.merge(expense.getPaidBy(), expense.getAmount(), Double::sum);

            // each participant is debited their share
            for (ExpenseSplit split : expense.getSplits()) {
                balances.merge(split.getPerson(), -split.getShareAmount(), Double::sum);
            }
        }
        return balances;
    }

    /**
     * Greedy debt simplification: repeatedly settle the largest creditor
     * against the largest debtor until everyone's balance is ~0.
     */
    private List<SettlementDto> simplifyDebts(Map<Person, Double> netBalances) {
        // Max-heap of creditors (people owed money) ordered by amount owed to them, descending
        PriorityQueue<Map.Entry<Person, Double>> creditors =
                new PriorityQueue<>((a, b) -> Double.compare(b.getValue(), a.getValue()));
        // Max-heap of debtors (people who owe money) ordered by amount owed, descending
        PriorityQueue<Map.Entry<Person, Double>> debtors =
                new PriorityQueue<>((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (Map.Entry<Person, Double> entry : netBalances.entrySet()) {
            double balance = entry.getValue();
            if (balance > EPSILON) {
                creditors.add(new AbstractMap.SimpleEntry<>(entry.getKey(), balance));
            } else if (balance < -EPSILON) {
                debtors.add(new AbstractMap.SimpleEntry<>(entry.getKey(), -balance));
            }
        }

        List<SettlementDto> settlements = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Map.Entry<Person, Double> creditor = creditors.poll();
            Map.Entry<Person, Double> debtor = debtors.poll();

            double settledAmount = Math.min(creditor.getValue(), debtor.getValue());
            settlements.add(new SettlementDto(
                    debtor.getKey().getId(), debtor.getKey().getName(),
                    creditor.getKey().getId(), creditor.getKey().getName(),
                    round2(settledAmount)
            ));

            double remainingCredit = creditor.getValue() - settledAmount;
            double remainingDebt = debtor.getValue() - settledAmount;

            if (remainingCredit > EPSILON) {
                creditors.add(new AbstractMap.SimpleEntry<>(creditor.getKey(), remainingCredit));
            }
            if (remainingDebt > EPSILON) {
                debtors.add(new AbstractMap.SimpleEntry<>(debtor.getKey(), remainingDebt));
            }
        }

        return settlements;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
