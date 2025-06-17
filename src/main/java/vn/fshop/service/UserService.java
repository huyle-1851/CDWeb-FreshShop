package vn.fshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fshop.model.User;
import vn.fshop.model.UserRoleEnum;
import vn.fshop.repository.UserRepository;


import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public User login(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User register(String username, String password, String firstname, String lastname,
                        String phone, String email, String address) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User(username, password, firstname, lastname, phone, email, address);
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRoleEnum.ADMIN;
    }

    public void promoteToAdmin(String username) {
        User user = findByUsername(username).orElseThrow(() ->
            new RuntimeException("User not found: " + username));
        user.setRole(UserRoleEnum.ADMIN);
        saveUser(user);
    }

    // Create admin user if none exists
    public void createDefaultAdminIfNotExists() {
        // Check if any admin user exists
        List<User> allUsers = getAllUsers();
        boolean hasAdmin = allUsers.stream().anyMatch(this::isAdmin);

        if (!hasAdmin) {
            // Create default admin user
            try {
                register("admin", "admin123", "Admin", "User", "0123456789", "admin@fshop.vn", "Admin Address");
                User adminUser = findByUsername("admin").orElse(null);
                if (adminUser != null) {
                    adminUser.setRole(UserRoleEnum.ADMIN);
                    saveUser(adminUser);
                }
            } catch (Exception e) {
                // Failed to create default admin user - continue startup
            }
        }
    }


}
