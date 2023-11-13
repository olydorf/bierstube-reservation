package bierstubeReservationTool.model.user;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A user.
 */
@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "email")
       })
public class UserR {
//extends User{ TODO: find out if it works with Hibernate

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;


    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", 
             joinColumns = @JoinColumn(name = "user_id"),
             inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    // You must have a default constructor for Hibernate to work properly
    public UserR() {}

    /*public UserR(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }*/



    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    //TODO: what is this name? Replace with Username
    public String getName() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public Set<Role> getRoles() {
    return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
    return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}