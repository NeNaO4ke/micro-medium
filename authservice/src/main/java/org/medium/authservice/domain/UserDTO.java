package org.medium.authservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties(value = "password", allowGetters = true, ignoreUnknown = true)
public class UserDTO {
    private String id;
    private String firstName;
    private String password;
    private String token;

    private Set<String> roles;

    public UserDTO(String id, String firstName, Set<String> roles) {
        this.id = id;
        this.firstName = firstName;
        this.roles = roles;
    }
}
