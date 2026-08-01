package com.expensesplitter.service;

import com.expensesplitter.exception.ResourceNotFoundException;
import com.expensesplitter.model.Expense;
import com.expensesplitter.model.Group;
import com.expensesplitter.model.Person;
import com.expensesplitter.repository.ExpenseRepository;
import com.expensesplitter.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final PersonService personService;
    private final ExpenseRepository expenseRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, PersonService personService, ExpenseRepository expenseRepository) {
        this.groupRepository = groupRepository;
        this.personService = personService;
        this.expenseRepository = expenseRepository;
    }

    public Group create(String name, List<Long> memberIds) {
        Group group = new Group(name);
        Set<Person> members = new HashSet<>();
        for (Long id : memberIds) {
            members.add(personService.findById(id));
        }
        group.setMembers(members);
        return groupRepository.save(group);
    }

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public Group findById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
    }

    public Group addMember(Long groupId, Long personId) {
        Group group = findById(groupId);
        Person person = personService.findById(personId);
        group.getMembers().add(person);
        return groupRepository.save(group);
    }

    @Transactional
    public void delete(Long groupId) {
        Group group = findById(groupId);

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        expenseRepository.deleteAll(expenses);

        group.getMembers().clear();
        groupRepository.save(group);

        groupRepository.delete(group);
    }
}