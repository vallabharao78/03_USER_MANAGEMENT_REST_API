package com.vallabha.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.vallabha.bindings.ActivateAccount;
import com.vallabha.bindings.Login;
import com.vallabha.bindings.User;
import com.vallabha.service.UserManagementService;

@RestController
public class UserRestController {
	@Autowired
	private UserManagementService userManagementService;

	@PostMapping("/user")
	public ResponseEntity<String> saveUser(@RequestBody User user) {
		boolean isSaved = userManagementService.saveUser(user);
		if (isSaved) {
			return new ResponseEntity<>("Registration successful...", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("Registration is not success...", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/active-account")
	public ResponseEntity<String> activateUserAcc(@RequestBody ActivateAccount activateAccount) {
		boolean isActivated = userManagementService.activateAccount(activateAccount);
		if (isActivated) {
			return new ResponseEntity<>("Account activated successfully.", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Account not activated.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> allUsers = userManagementService.getAllUsers();
		return new ResponseEntity<>(allUsers, HttpStatus.OK);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable Integer userId) {
		User user = userManagementService.getUserById(userId);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@DeleteMapping("/user/{userId}")
	public ResponseEntity<String> deleteUserById(@PathVariable Integer userId) {
		boolean isDeleted = userManagementService.deleteByUserId(userId);
		if (isDeleted) {
			return new ResponseEntity<>("User deleted succesfully", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("User not deleted", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/password/{email}")
	public ResponseEntity<String> forgetPwd(@PathVariable String email) {
		String status = userManagementService.forgetPwd(email);
		return new ResponseEntity<>(status, HttpStatus.OK);
	}

	@GetMapping("/account-status/{userId}/{status}")
	public ResponseEntity<String> changeAccStatus(@PathVariable Integer userId, @PathVariable String status) {
		boolean isStatusChanged = userManagementService.changeAccountStatus(userId, status);
		if (isStatusChanged) {
			return new ResponseEntity<>("status changed succesfully", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("status not changed..", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("login")
	public ResponseEntity<String> loginAccount(@RequestBody Login login) {
		String status = userManagementService.login(login);
		return new ResponseEntity<>(status, HttpStatus.OK);
	}
  
}
