package org.example.accountservice;

import org.example.accountservice.domain.AccountStatus;
import org.example.accountservice.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedAccounts(AccountService accountService) {
        return args -> {
            accountService.create("CUST-001", "EUR", 5000);
            accountService.create("CUST-001", "MAD", 20000);
            accountService.create("CUST-002", "EUR", 1200);
            var blocked = accountService.create("CUST-003", "USD", 300);
            accountService.changeStatus(blocked.getId(), AccountStatus.BLOCKED);
            accountService.findAll().forEach(System.out::println);
        };
    }
}
