package com.chsbk.pioner_pixel.service;

import com.chsbk.pioner_pixel.entities.Account;
import com.chsbk.pioner_pixel.logging.TransferAuditLogger;
import com.chsbk.pioner_pixel.repository.AccountRepository;
import com.chsbk.pioner_pixel.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @InjectMocks TransferService service;

    @Mock AccountRepository     accounts;
    @Mock CacheManager          cacheManager;
    @Mock TransferAuditLogger   audit;

    private Account acc1, acc2;

    @BeforeEach
    void setUp() {
        acc1 = Account.builder().id(1L)
                .balance(new BigDecimal("100"))
                .initialBalance(new BigDecimal("100")).build();
        acc2 = Account.builder().id(2L)
                .balance(new BigDecimal("50"))
                .initialBalance(new BigDecimal("50")).build();

        when(accounts.findById(1L)).thenReturn(Optional.of(acc1));
        when(accounts.findById(2L)).thenReturn(Optional.of(acc2));

        lenient().when(cacheManager.getCache(anyString()))
                .thenReturn(mock(org.springframework.cache.Cache.class));
    }

    @Test
    void transfer_success() {
        service.transferUniversal(1L, "2", new BigDecimal("40"));

        assertThat(acc1.getBalance()).isEqualByComparingTo("60");
        assertThat(acc2.getBalance()).isEqualByComparingTo("90");
        verify(audit).success(1L, 2L, new BigDecimal("40"));
    }

    @Disabled
    @Test
    void transfer_insufficientFunds() {
        assertThatThrownBy(() ->
                service.transferUniversal(1L, "2", new BigDecimal("400"))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("недостаточно средств");
    }

    @Disabled
    @Test
    void transfer_self() {
        assertThatThrownBy(() ->
                service.transferUniversal(1L, "1", new BigDecimal("10"))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("self-transfer");
    }
}
