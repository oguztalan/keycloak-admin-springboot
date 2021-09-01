package com.talan.keycloakrest.controller;

import com.talan.keycloakrest.model.AccountDto;
import com.talan.keycloakrest.model.RealmRoleModel;
import com.talan.keycloakrest.model.UserCredentials;
import com.talan.keycloakrest.model.UserDto;
import com.talan.keycloakrest.service.KeycloakAdminService;
import io.swagger.annotations.ApiOperation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class KeycloakAdminController {

    @Autowired
    private KeycloakAdminService keyCloakService;

    @PostMapping("/create")
    @ApiOperation(value = "Create User", notes = "Yeni bir user kaydı oluşturur.")
    public ResponseEntity<Object> createUser(@RequestBody AccountDto accountDto){
        this.keyCloakService.createUser(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/list")
    @ApiOperation(value = "User List", notes = "Keycloak'a kayıtlı userları listeler.")
    public ResponseEntity<List<UserDto>> userList(){
        return ResponseEntity.ok(this.keyCloakService.getUserList());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserRepresentation> userById(@PathVariable("userId") String userId){
        return ResponseEntity.ok(this.keyCloakService.getUserById(userId));
    }

    @PutMapping("/update")
    @ApiOperation(value = "Update User", notes = "Requestte gelen userı günceller")
    public ResponseEntity<Void> updateUser(@RequestBody UserDto userDto){
        this.keyCloakService.updateUser(userDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{userId}")
    @ApiOperation(value = "Delete User", notes = "userId parametresi ile ilgili User ı siler.")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") String userId){
        this.keyCloakService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/status")
    @ApiOperation(value = "User Status", notes = "User'ı enable yada disable eder.")
    public ResponseEntity<Void> userStatus(@RequestParam(value = "userId") String userId,@RequestParam("isEnable") Boolean isEnable){
        this.keyCloakService.toggleUser(userId,isEnable);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    @ApiOperation(value = "User Roles", notes = "Mevcut olan user rollerinin listesini döner.")
    public ResponseEntity<List<RoleRepresentation>> getRoles(){
        return ResponseEntity.ok(this.keyCloakService.getRoles());
    }

    @GetMapping("/assigned-roles/{userId}")
    @ApiOperation(value = "Assigned User Roles", notes = "User'a tanımlanmış olan rolleri döner.")
    public ResponseEntity<List<RealmRoleModel>> getAssignedRoles(@PathVariable("userId") String userId){
        return ResponseEntity.ok(this.keyCloakService.getAssignedRoles(userId));
    }

    @GetMapping("/available-roles/{userId}")
    @ApiOperation(value = "Available Roles", notes = "User'a tanımlanabilecek uygun rollerin listesini döner.")
    public ResponseEntity<List<RealmRoleModel>> getAvailableRoles(@PathVariable("userId") String userId){
        return ResponseEntity.ok(this.keyCloakService.getAvailableRoles(userId));
    }

    @PostMapping("/update/password")
    @ApiOperation(value = "Update Password User", notes = "Password günceller.")
    public ResponseEntity<Void> updatePassword(@RequestBody UserCredentials userCredentials){
        this.keyCloakService.resetPassword(userCredentials);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assignee/realm-role")
    @ApiOperation(value = "Assignee Role to User", notes = "Seçilen rolü user a tanımlar.")
    public ResponseEntity<Void> assigneeRole(@RequestParam("userId") String userId,@RequestParam("role") String role){
        this.keyCloakService.assigneeRole(userId,role);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete/realm-role")
    @ApiOperation(value = "Delete Role", notes = "Seçilen rolü userdan kaldırır.")
    public ResponseEntity<Void> removeRole(@RequestParam("userId") String userId, @RequestParam("role") String role){
        this.keyCloakService.removeRole(userId, role);
        return ResponseEntity.noContent().build();
    }
}
