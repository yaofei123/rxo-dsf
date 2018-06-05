package com.refactoring.rxo.member.service.impl;

import com.refactoring.rxo.member.dao.AccountDao;
import com.refactoring.rxo.member.entity.Account;
import com.refactoring.rxo.member.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("accountService")
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;

    @Override
    public Account getAccountInfoByUserName(String userName) {
        return accountDao.selectByUserName(userName);
    }
}
