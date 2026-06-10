package com.skylark.controller;

import com.skylark.dto.AccountVO;
import com.skylark.dto.BillingRecordVO;
import com.skylark.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<AccountVO> getAll() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public AccountVO getById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @GetMapping("/group/{groupName}")
    public AccountVO getByGroupName(@PathVariable String groupName) {
        return accountService.getAccountByGroupName(groupName);
    }

    @GetMapping("/{id}/billing-records")
    public List<BillingRecordVO> getBillingRecords(@PathVariable Long id) {
        return accountService.getBillingRecordsByAccount(id);
    }
}
