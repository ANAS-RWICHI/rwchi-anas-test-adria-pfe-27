package org.example.accountservice.repository;

import org.example.accountservice.domain.Account;
import org.example.accountservice.domain.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {


}
