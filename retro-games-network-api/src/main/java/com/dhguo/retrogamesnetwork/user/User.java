package com.dhguo.retrogamesnetwork.user;

import com.dhguo.retrogamesnetwork.game.Game;
import com.dhguo.retrogamesnetwork.history.GameTransactionHistory;
import com.dhguo.retrogamesnetwork.role.Role;
import jakarta.persistence.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {

  @Id @GeneratedValue private Integer id;

  private String firstName;

  private String lastName;

  @Column(unique = true)
  private String email;

  private String password;

  private boolean accountLocked;

  private boolean enabled;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
  private List<Role> roles;

  @OneToMany(mappedBy = "owner")
  private List<Game> games;

  @OneToMany(mappedBy = "user")
  private List<GameTransactionHistory> histories;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(insertable = false)
  private LocalDateTime lastModifiedDate;

  @Override
  public String getName() {
    return email;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).toList();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !accountLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public String fullName() {
    return firstName + " " + lastName;
  }
}
