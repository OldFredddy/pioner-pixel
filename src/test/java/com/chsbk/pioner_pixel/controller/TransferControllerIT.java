// src/test/java/com/chsbk/pioner_pixel/controller/TransferControllerIT.java
package com.chsbk.pioner_pixel.controller;

import com.chsbk.pioner_pixel.entities.*;
import com.chsbk.pioner_pixel.repository.AccountRepository;
import com.chsbk.pioner_pixel.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers(disabledWithoutDocker = true)        // ⚠ не рушим билд, если Docker не доступен
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TransferControllerIT {

    @Container
    static PostgreSQLContainer<?> db =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("pioner")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",      db::getJdbcUrl);
        r.add("spring.datasource.username", db::getUsername);
        r.add("spring.datasource.password", db::getPassword);
    }

    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired AccountRepository accounts;
    @Autowired BCryptPasswordEncoder enc;

    Long idFrom, idTo;

    @BeforeEach
    void seed() {
        if (users.count() > 0) return;

        User u1 = User.builder()
                .name("Alina")
                .dateOfBirth(LocalDate.of(1990,1,1))
                .password(enc.encode("pwd1"))
                .build();
        u1.addEmail(EmailData.builder().email("alina@mail.ru").build());
        Account a1 = Account.builder()
                .balance(new BigDecimal("200"))
                .initialBalance(new BigDecimal("200"))
                .build();
        u1.setAccount(a1); a1.setUser(u1);
        users.save(u1);

        User u2 = User.builder()
                .name("Anton")
                .dateOfBirth(LocalDate.of(1992,2,2))
                .password(enc.encode("pwd2"))
                .build();
        u2.addPhone(PhoneData.builder().phone("70000000000").build());
        Account a2 = Account.builder()
                .balance(new BigDecimal("50"))
                .initialBalance(new BigDecimal("50"))
                .build();
        u2.setAccount(a2); a2.setUser(u2);
        users.save(u2);

        idFrom = u1.getId();
        idTo   = u2.getId();
    }

    @Test
    void transfer_ok_changesBalances() throws Exception {

        String cookie = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {"login":"alina@mail","password":"pwd1"}
                                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getHeader("Set-Cookie");

        mvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Cookie", cookie)
                        .content("""
                                 {"to":"70000000000","amount":40}
                                 """))
                .andExpect(status().isOk());

        BigDecimal fromBal = accounts.findById(idFrom).orElseThrow().getBalance();
        BigDecimal toBal   = accounts.findById(idTo).orElseThrow().getBalance();

        org.assertj.core.api.Assertions.assertThat(fromBal).isEqualByComparingTo("160");
        org.assertj.core.api.Assertions.assertThat(toBal)  .isEqualByComparingTo("90");
    }
}
