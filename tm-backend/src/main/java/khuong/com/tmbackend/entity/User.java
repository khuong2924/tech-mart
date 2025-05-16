package khuong.com.tmbackend.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.HashSet;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username; // Hoặc email

    @NotBlank
    private String password;

    private String fullName;
    private String email; // Nếu username không phải email
    private String phoneNumber;
    private String address;
    private boolean enabled = true; // Để quản lý khóa/mở tài khoản

    @ManyToMany(fetch = FetchType.EAGER) // EAGER để load Role cùng User
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    private Instant createdAt;
    private Instant updatedAt;

}
