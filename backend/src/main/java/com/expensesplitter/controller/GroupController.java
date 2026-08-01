package com.expensesplitter.controller;

import com.expensesplitter.model.Group;
import com.expensesplitter.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    public static class CreateGroupRequest {
        public String name;
        public List<Long> memberIds;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Group create(@RequestBody CreateGroupRequest request) {
        return groupService.create(request.name, request.memberIds);
    }

    @GetMapping
    public List<Group> getAll() {
        return groupService.findAll();
    }

    @GetMapping("/{id}")
    public Group getById(@PathVariable Long id) {
        return groupService.findById(id);
    }

    @PostMapping("/{id}/members/{personId}")
    public Group addMember(@PathVariable Long id, @PathVariable Long personId) {
        return groupService.addMember(id, personId);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        groupService.delete(id);
    }
}
