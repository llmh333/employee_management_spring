package com.hit.employee_management_spring.domain.entity;

import com.hit.employee_management_spring.audit.DateAuditing;
import com.hit.employee_management_spring.constant.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends DateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(columnDefinition = "varchar(100)", nullable = false, unique = true, updatable = false)
    private String username;

    @Column(columnDefinition = "varchar(255)", nullable = false)
    private String password;

    @Column(columnDefinition = "varchar(255)", nullable = false, unique = true, updatable = false)
    private String email;

    private String firstName;

    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", table = "users"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "roles")

    )
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSession> userSessions;
}
