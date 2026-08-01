package com.expensesplitter.service;

import com.expensesplitter.dto.SettlementDto;
import com.expensesplitter.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the settle-up (debt simplification) algorithm.
 * Demonstrates TDD-style testing with Mockito mocks so we don't need a real DB.
 */
@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private SettlementService settlementService;

    private Person alice, bob, charlie;
    private Group group;

    @BeforeEach
    void setUp() {
        alice = new Person("Alice", "alice@test.com");
        alice.setId(1L);
        bob = new Person("Bob", "bob@test.com");
        bob.setId(2L);
        charlie = new Person("Charlie", "charlie@test.com");
        charlie.setId(3L);

        Set<Person> members = new HashSet<>(Arrays.asList(alice, bob, charlie));
        group = new Group("Trip to Goa");
        group.setId(100L);
        group.setMembers(members);
    }

    @Test
    void settleGroup_withSingleEqualExpense_producesOneCorrectTransaction() {
        // Alice pays 300, split equally 3 ways (100 each) among Alice, Bob, Charlie
        Expense expense = new Expense();
        expense.setAmount(300.0);
        expense.setPaidBy(alice);
        expense.setGroup(group);
        expense.setSplits(Arrays.asList(
                new ExpenseSplit(expense, alice, 100.0),
                new ExpenseSplit(expense, bob, 100.0),
                new ExpenseSplit(expense, charlie, 100.0)
        ));

        when(groupService.findById(100L)).thenReturn(group);
        when(expenseService.getExpensesForGroup(100L)).thenReturn(List.of(expense));

        List<SettlementDto> settlements = settlementService.settleGroup(100L);

        // Bob owes Alice 100, Charlie owes Alice 100 -> 2 transactions expected
        assertEquals(2, settlements.size());
        double totalSettled = settlements.stream().mapToDouble(SettlementDto::getAmount).sum();
        assertEquals(200.0, totalSettled, 0.01);
        settlements.forEach(s -> assertEquals(alice.getId(), s.getToPersonId()));
    }

    @Test
    void settleGroup_withMultipleExpenses_minimizesNumberOfTransactions() {
        // Expense 1: Alice pays 90, split equally among all 3 (30 each)
        Expense e1 = new Expense();
        e1.setAmount(90.0);
        e1.setPaidBy(alice);
        e1.setGroup(group);
        e1.setSplits(Arrays.asList(
                new ExpenseSplit(e1, alice, 30.0),
                new ExpenseSplit(e1, bob, 30.0),
                new ExpenseSplit(e1, charlie, 30.0)
        ));

        // Expense 2: Bob pays 60, split equally among all 3 (20 each)
        Expense e2 = new Expense();
        e2.setAmount(60.0);
        e2.setPaidBy(bob);
        e2.setGroup(group);
        e2.setSplits(Arrays.asList(
                new ExpenseSplit(e2, alice, 20.0),
                new ExpenseSplit(e2, bob, 20.0),
                new ExpenseSplit(e2, charlie, 20.0)
        ));

        when(groupService.findById(100L)).thenReturn(group);
        when(expenseService.getExpensesForGroup(100L)).thenReturn(Arrays.asList(e1, e2));

        // Net balances: Alice = +90-30-20 = +40, Bob = +60-30-20 = +10, Charlie = -30-20 = -50
        List<SettlementDto> settlements = settlementService.settleGroup(100L);

        double totalSettled = settlements.stream().mapToDouble(SettlementDto::getAmount).sum();
        assertEquals(50.0, totalSettled, 0.01);
        // Greedy algorithm should settle this in at most 2 transactions instead of naively doing 1 per expense
        assertTrue(settlements.size() <= 2);
    }

    @Test
    void settleGroup_whenAlreadyBalanced_producesNoTransactions() {
        // E1: Alice pays 90, split equally among all 3 (30 each)
        // -> after E1: alice +60, bob -30, charlie -30
        Expense e1 = new Expense();
        e1.setAmount(90.0);
        e1.setPaidBy(alice);
        e1.setGroup(group);
        e1.setSplits(Arrays.asList(
                new ExpenseSplit(e1, alice, 30.0),
                new ExpenseSplit(e1, bob, 30.0),
                new ExpenseSplit(e1, charlie, 30.0)
        ));

        // E2: Bob "pays back" his 30 share directly to Alice
        // -> after E2: alice +30 (60-30), bob 0 (-30+30)
        Expense e2 = new Expense();
        e2.setAmount(30.0);
        e2.setPaidBy(bob);
        e2.setGroup(group);
        e2.setSplits(List.of(new ExpenseSplit(e2, alice, 30.0)));

        // E3: Charlie "pays back" his 30 share directly to Alice
        // -> after E3: alice 0 (30-30), charlie 0 (-30+30)
        Expense e3 = new Expense();
        e3.setAmount(30.0);
        e3.setPaidBy(charlie);
        e3.setGroup(group);
        e3.setSplits(List.of(new ExpenseSplit(e3, alice, 30.0)));

        when(groupService.findById(100L)).thenReturn(group);
        when(expenseService.getExpensesForGroup(100L)).thenReturn(Arrays.asList(e1, e2, e3));

        List<SettlementDto> settlements = settlementService.settleGroup(100L);

        assertTrue(settlements.isEmpty());
    }
}
