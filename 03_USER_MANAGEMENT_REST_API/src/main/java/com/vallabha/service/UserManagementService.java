package com.vallabha.service;

import java.util.List;

import com.vallabha.bindings.ActivateAccount;
import com.vallabha.bindings.Login;
import com.vallabha.bindings.User;

public interface UserManagementService 
{
	public boolean saveUser(User user);
	
	public boolean activateAccount(ActivateAccount activateAccount);
	
	public List<User> getAllUsers();
	
	public User getUserById(Integer userId);
	
	public boolean deleteByUserId(Integer userId);
	
	public String forgetPwd(String email);
	
	public boolean changeAccountStatus(Integer userId, String status);
	
	public String login(Login login);
}
