package vn.fshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username is required")
    private String username;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is required")
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "First name is required")
    private String firstname;

    @Column(nullable = false)
    @NotBlank(message = "Last name is required")
    private String lastname;

    @Column(nullable = false)
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    @Column(nullable = false, unique = true)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Column
    private String address;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "status_id")
    private int statusId = 1; // Default active status

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRoleEnum role = UserRoleEnum.USER; // Default role

    public User(String username, String password, String firstname, String lastname, String phone, String email, String address) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.deliveryAddress = address; // Default delivery address same as address
        this.statusId = 1; // Default active status
    }

    // Helper method for full name
    public String getFullName() {
        return firstname + " " + lastname;
    }
}
