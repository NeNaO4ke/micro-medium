package org.medium.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private String id;
    private String username;
    private String token;

    private Set<String> roles;

    public UserDTO(String id, String username, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}