package com.expensesplitter.controller;

import com.expensesplitter.dto.BalanceDto;
import com.expensesplitter.dto.SettlementDto;
import com.expensesplitter.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}")
@CrossOrigin(origins = "*")
public class SettlementController {

    private final SettlementService settlementService;

    @Autowired
    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping("/balances")
    public List<BalanceDto> getBalances(@PathVariable Long groupId) {
        return settlementService.getBalances(groupId);
    }

    @GetMapping("/settle")
    public List<SettlementDto> settle(@PathVariable Long groupId) {
        return settlementService.settleGroup(groupId);
    }
}
