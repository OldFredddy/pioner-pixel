package com.chsbk.pioner_pixel.controller;

import com.chsbk.pioner_pixel.dto.UserDto;
import com.chsbk.pioner_pixel.service.UserService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final CacheManager cacheManager;

    @GetMapping
    public Page<UserDto> findUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(defaultValue = "0")  @Min(0)   int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        return userService.search(name,
                email,
                phone,
                dateOfBirth,
                pageRequest);
    }

    @GetMapping("/{id}")
    public UserDto getOne(@PathVariable @Positive Long id) {
        return userService.get(id);
    }


    @PostMapping("/{id}/update-emails")
    public void replaceEmails(@PathVariable @Positive Long id,
                              @RequestBody @NotEmpty Set<@Email String> emails) {

        userService.updateEmails(id, emails);
    }

    @PostMapping("/{id}/update-phones")
    public void replacePhones(@PathVariable @Positive Long id,
                              @RequestBody Set<@Pattern(regexp = "\\d{11,13}") String> phones) {

        userService.updatePhones(id, phones);
    }

    @GetMapping("/me")
    @CacheEvict(value = "users",
            key   = "T(java.lang.Long).valueOf(#principal.username)")
    public UserDto me(@AuthenticationPrincipal UserDetails principal) {

        long id = Long.parseLong(principal.getUsername());
        return userService.get(id);
    }

    @PostMapping("/{id}/emails")
    public void addEmail(@PathVariable @Positive Long id,
                         @AuthenticationPrincipal UserDetails principal,
                         @RequestBody @NotBlank @Email String email) {

        checkOwner(id, principal);
        userService.addEmail(id, email);
    }

    @DeleteMapping("/{id}/emails/{email}")
    public void deleteEmail(@PathVariable @Positive Long id,
                            @AuthenticationPrincipal UserDetails principal,
                            @PathVariable @Email String email) {

        checkOwner(id, principal);
        userService.deleteEmail(id, email);
    }

    @PostMapping("/{id}/emails/{oldEmail}")
    public void updateEmail(@PathVariable @Positive Long id,
                            @AuthenticationPrincipal UserDetails principal,
                            @PathVariable @Email String oldEmail,
                            @RequestBody  @NotBlank @Email String newEmail) {

        checkOwner(id, principal);
        userService.updateEmail(id, oldEmail, newEmail);
    }

    @PostMapping("/{id}/phones")
    public void addPhone(@PathVariable @Positive Long id,
                         @AuthenticationPrincipal UserDetails principal,
                         @RequestBody @Pattern(regexp = "\\d{11,13}") String phone) {

        checkOwner(id, principal);
        userService.addPhone(id, phone);
    }

    @DeleteMapping("/{id}/phones/{phone}")
    public void deletePhone(@PathVariable @Positive Long id,
                            @AuthenticationPrincipal UserDetails principal,
                            @PathVariable @Pattern(regexp = "\\d{11,13}") String phone) {

        checkOwner(id, principal);
        userService.deletePhone(id, phone);
    }

    @PostMapping("/{id}/phones/{oldPhone}")
    public void updatePhone(@PathVariable @Positive Long id,
                            @AuthenticationPrincipal UserDetails principal,
                            @PathVariable @Pattern(regexp = "\\d{11,13}") String oldPhone,
                            @RequestBody  @Pattern(regexp = "\\d{11,13}") String newPhone) {

        checkOwner(id, principal);
        userService.updatePhone(id, oldPhone, newPhone);
    }

    private void checkOwner(Long pathId, UserDetails principal) {

        long currentId = Long.parseLong(principal.getUsername());

        if (!pathId.equals(currentId)) {
            throw new SecurityException("Access denied");
        }
    }
}
