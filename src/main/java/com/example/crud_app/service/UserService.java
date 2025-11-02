package com.example.crud_app.service;

import com.example.crud_app.entity.User;
import com.example.crud_app.exception.DuplicateEmailException;
import com.example.crud_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    // CREATE
    public User create(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("User ID must be null for creation");
        }
        validateEmailUniqueness(user.getEmail(), null);
        return repo.save(user);
    }

    // READ ALL
    public List<User> findAll() {
        return repo.findAll();
    }

    // READ BY ID
    public User findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    // READ BY NAME (partial, case-insensitive)
    public List<User> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        return repo.findByNameContainingIgnoreCase(name.trim());
    }

    // UPDATE
    public User update(Long id, User updated) {
        return repo.findById(id)
                .map(existing -> {
                    // Only check email uniqueness if it changed
                    if (!existing.getEmail().equalsIgnoreCase(updated.getEmail())) {
                        validateEmailUniqueness(updated.getEmail(), id);
                    }
                    existing.setName(updated.getName());
                    existing.setEmail(updated.getEmail());
                    return repo.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    // DELETE
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("User not found: " + id);
        }
        repo.deleteById(id);
    }

    // --- Helper: Email uniqueness ---
    private void validateEmailUniqueness(String email, Long excludeId) {
        boolean exists = repo.findByEmail(email)
                .map(u -> excludeId == null || !u.getId().equals(excludeId))
                .orElse(false);

        if (exists) {
            throw new DuplicateEmailException("Email already in use: " + email);
        }
    }
}
