package com.expensesplitter.service;

import com.expensesplitter.exception.InvalidExpenseException;
import com.expensesplitter.exception.ResourceNotFoundException;
import com.expensesplitter.model.Group;
import com.expensesplitter.model.Person;
import com.expensesplitter.repository.ExpenseRepository;
import com.expensesplitter.repository.ExpenseSplitRepository;
import com.expensesplitter.repository.GroupRepository;
import com.expensesplitter.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    @Autowired
    public PersonService(PersonRepository personRepository,
                          GroupRepository groupRepository,
                          ExpenseRepository expenseRepository,
                          ExpenseSplitRepository expenseSplitRepository) {
        this.personRepository = personRepository;
        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
    }

    public Person create(Person person) {
        return personRepository.save(person);
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person findById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        Person person = findById(id);

        boolean hasPaidExpenses = expenseRepository.existsByPaidById(id);
        boolean hasSplitHistory = expenseSplitRepository.existsByPersonId(id);

        if (hasPaidExpenses || hasSplitHistory) {
            throw new InvalidExpenseException(
                    "Cannot delete " + person.getName() +
                    ": they have expense history. Delete the related group/expenses first.");
        }

        for (Group group : new HashSet<>(person.getGroups())) {
            group.getMembers().remove(person);
            groupRepository.save(group);
        }

        personRepository.delete(person);
    }
}