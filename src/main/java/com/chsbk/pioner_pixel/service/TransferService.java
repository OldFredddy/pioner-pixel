package com.chsbk.pioner_pixel.service;

import com.chsbk.pioner_pixel.entities.Account;
import com.chsbk.pioner_pixel.logging.TransferAuditLogger;
import com.chsbk.pioner_pixel.repository.AccountRepository;
import com.chsbk.pioner_pixel.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TransferService {

    private static final Pattern ID_PATTERN    = Pattern.compile("\\d{1,10}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{11,13}");

    private final AccountRepository   accountRepository;
    private final UserRepository      userRepository;
    private final CacheManager        cacheManager;
    private final TransferAuditLogger auditLogger;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transferUniversal(Long fromId, String to, BigDecimal amount) {
        auditLogger.start(fromId, null, amount);
        Long toId = resolveReceiverId(to)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Получатель должен быть: ID (1–10 цифр) или телефон (11–13 цифр)"
                ));

        try {
            transfer(fromId, toId, amount);
            auditLogger.success(fromId, toId, amount);

        } catch (IllegalArgumentException ex) {
            auditLogger.rejected(ex.getMessage(), fromId, toId);
            throw ex;

        } catch (Exception ex) {
            auditLogger.error(ex, fromId, toId);
            throw ex;
        }

        cacheManager.getCache("users").evict(fromId);
        cacheManager.getCache("users").evict(toId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Нельзя перевести средства самому себе");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной");
        }

        Account sender   = accountRepository.findById(fromId)
                .orElseThrow(() -> new EntityNotFoundException("sender"));
        Account receiver = accountRepository.findById(toId)
                .orElseThrow(() -> new EntityNotFoundException("receiver"));
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Недостаточно средств");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
    }

    private Optional<Long> resolveReceiverId(String to) {
        if (ID_PATTERN.matcher(to).matches()) {
            return Optional.of(Long.parseLong(to));
        }
        if (PHONE_PATTERN.matcher(to).matches()) {
            return userRepository.findByPhones_Phone(to)
                    .map(u -> u.getId());
        }

        return Optional.empty();
    }
}
