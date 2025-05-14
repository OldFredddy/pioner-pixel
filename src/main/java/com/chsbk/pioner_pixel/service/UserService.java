package com.chsbk.pioner_pixel.service;

import com.chsbk.pioner_pixel.dto.UserDto;
import com.chsbk.pioner_pixel.entities.*;
import com.chsbk.pioner_pixel.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#id")
    public UserDto get(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return toDto(user);
    }

    @Cacheable(
            value = "users",
            key = "T(java.util.Objects).hash(#name,#email,#phone,#dob,#page.pageNumber,#page.pageSize)"
    )
    public Page<UserDto> search(String   name, String   email, String   phone,
                                LocalDate dob, Pageable page) {
        return userRepository.search(name, email, phone, dob, page)
                .map(this::toDto);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void updateEmails(Long userId, Set<String> emails) {
        User user = getUserOrThrow(userId);
        for (String email : emails) {
            ensureEmailIsFreeForOtherUser(userId, email);
        }
        user.getEmails().clear();
        emails.forEach(e -> user.addEmail(EmailData.builder().email(e).build()));
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void addEmail(Long userId, String email) {
        ensureEmailIsFreeForOtherUser(userId, email);
        User user = getUserOrThrow(userId);
        user.addEmail(EmailData.builder().email(email).build());
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void deleteEmail(Long userId, String email) {
        User user = getUserOrThrow(userId);
        boolean removed = user.getEmails()
                .removeIf(e -> e.getEmail().equals(email));

        if (!removed) {
            throw new EntityNotFoundException("email not found");
        }
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void updateEmail(Long   userId, String oldEmail, String newEmail) {
        ensureEmailIsFreeForOtherUser(userId, newEmail);
        User user = getUserOrThrow(userId);
        EmailData old =
                user.getEmails()
                        .stream()
                        .filter(e -> e.getEmail().equals(oldEmail))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("email not found"));
        user.getEmails().remove(old);
        user.addEmail(EmailData.builder().email(newEmail).build());
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void updatePhones(Long userId, Set<String> phones) {
        User user = getUserOrThrow(userId);
        for (String phone : phones) {
            ensurePhoneIsFreeForOtherUser(userId, phone);
        }
        user.getPhones().clear();
        phones.forEach(p -> user.addPhone(PhoneData.builder().phone(p).build()));
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void addPhone(Long userId, String phone) {
        ensurePhoneIsFreeForOtherUser(userId, phone);
        User user = getUserOrThrow(userId);
        user.addPhone(PhoneData.builder().phone(phone).build());
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void deletePhone(Long userId, String phone) {
        User user = getUserOrThrow(userId);
        boolean removed = user.getPhones()
                .removeIf(p -> p.getPhone().equals(phone));
        if (!removed) {
            throw new EntityNotFoundException("phone not found");
        }
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void updatePhone(Long   userId, String oldPhone, String newPhone) {
        ensurePhoneIsFreeForOtherUser(userId, newPhone);
        User user = getUserOrThrow(userId);
        PhoneData old = user.getPhones()
                        .stream()
                        .filter(p -> p.getPhone().equals(oldPhone))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("phone not found"));
        user.getPhones().remove(old);
        user.addPhone(PhoneData.builder().phone(newPhone).build());
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
    }

    private void ensureEmailIsFreeForOtherUser(Long userId, String email) {
        userRepository.findByEmails_Email(email)
                .filter(u -> !u.getId().equals(userId))
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Email занят: " + email);
                });
    }

    private void ensurePhoneIsFreeForOtherUser(Long userId, String phone) {
        userRepository.findByPhones_Phone(phone)
                .filter(u -> !u.getId().equals(userId))
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Phone занят: " + phone);
                });
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .dateOfBirth(user.getDateOfBirth())
                .emails(user.getEmails()
                        .stream()
                        .map(EmailData::getEmail)
                        .collect(Collectors.toSet()))
                .phones(user.getPhones()
                        .stream()
                        .map(PhoneData::getPhone)
                        .collect(Collectors.toSet()))
                .balance(user.getAccount().getBalance())
                .initialBalance(user.getAccount().getInitialBalance())
                .build();
    }
}
