package vn.fshop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private int id;
    @Column(nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String email;
    @Column
    private String address;
    @Column(name = "delivery_address")
    private String deliveryAddress;
    @Column(name = "status_id")
    private int statusId;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> user_roles;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String username, String password, String firstname, String lastname, String phone, String email, String address, String deliveryAddress, int statusId) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.deliveryAddress = deliveryAddress;
        this.statusId = statusId;
    }
}
