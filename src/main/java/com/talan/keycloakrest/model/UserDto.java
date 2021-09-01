package com.talan.keycloakrest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean enabled;
    private Long createdTimestamp;

}
