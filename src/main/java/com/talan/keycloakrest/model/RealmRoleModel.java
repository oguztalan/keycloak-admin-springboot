package com.talan.keycloakrest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RealmRoleModel {

    private String id;
    private String name;
    private String containerId;

}
