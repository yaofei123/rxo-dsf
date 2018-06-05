package com.refactoring.rxo.member.dao;

import com.refactoring.rxo.datasource.mysql.member.MemberMybatisMapper;
import com.refactoring.rxo.member.entity.Account;

public interface AccountDao extends MemberMybatisMapper {

    Account selectByUserName(String userName);

}
