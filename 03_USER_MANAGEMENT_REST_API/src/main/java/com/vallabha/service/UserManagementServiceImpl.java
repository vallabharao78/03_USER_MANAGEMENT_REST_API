package com.vallabha.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.vallabha.bindings.ActivateAccount;
import com.vallabha.bindings.Login;
import com.vallabha.bindings.User;
import com.vallabha.entity.UserMaster;
import com.vallabha.repo.UserMasterRepo;
import com.vallabha.utils.EmailUtils;

@Service
public class UserManagementServiceImpl implements UserManagementService {

	@Autowired
	private UserMasterRepo userMasterRepo;

	@Autowired
	private EmailUtils emailUtils;

	@Override
	public boolean saveUser(User user) {
		UserMaster entity = new UserMaster(); // Binding to entity data transfer.
		BeanUtils.copyProperties(user, entity);

		entity.setPassword(generateRandomPwd());
		entity.setAccountStatus("In-Active");

		UserMaster savedEntity = userMasterRepo.save(entity);

		// Logic to Send Registration Email to the user
		String subject = "Your Registration Successfull...";
		String filename = "REG-EMAIL-BODY.txt";
		String body = readEmailBody(filename, entity.getFullName(), entity.getPassword());
		emailUtils.sendEmail(user.getEmail(), subject, body);

		return savedEntity.getUserId() != null;
	}

	@Override
	public boolean activateAccount(ActivateAccount activateAccount) {
		String email = activateAccount.getEmail();
		String tempPwd = activateAccount.getTempPwd();

		UserMaster entity = new UserMaster();
		entity.setEmail(email);
		entity.setPassword(tempPwd);

		// Query By Example (QBE)
		// select * from User_master where email=? and tempPwd=?
		Example<UserMaster> example = Example.of(entity);

		List<UserMaster> entities = userMasterRepo.findAll(example);
		if (entities.isEmpty()) {
			return false;
		} else {
			UserMaster userMaster = entities.get(0);
			userMaster.setPassword(activateAccount.getNewPwd());
			userMaster.setAccountStatus("Active");
			userMasterRepo.save(userMaster);
			return true;
		}
	}

	@Override
	public List<User> getAllUsers() {
		List<UserMaster> entities = userMasterRepo.findAll();
		List<User> usersList = new ArrayList<>();
		for (UserMaster entity : entities) {
			User user = new User();
			// Converting entity object to binding object
			BeanUtils.copyProperties(entity, user);
			usersList.add(user);
		}
		return usersList;
	}

	@Override
	public User getUserById(Integer userId) {
		Optional<UserMaster> entity = userMasterRepo.findById(userId);
		if (entity.isPresent()) {
			UserMaster userMaster = entity.get();
			User user = new User();
			BeanUtils.copyProperties(userMaster, user);
			return user;
		}
		return null;
	}

	@Override
	public boolean deleteByUserId(Integer userId) {
		try {
			userMasterRepo.deleteById(userId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String forgetPwd(String email) {
		UserMaster entity = userMasterRepo.findByEmail(email);
		if (entity == null) {
			return "Invalid Email";
		} else {

			String subject = "Forget Password";
			String filename = "RECOVER-EMAIL-BODY.txt";
			String Body = readEmailBody(filename, entity.getFullName(), entity.getPassword());

			boolean isEmailSent = emailUtils.sendEmail(filename, subject, Body);
			if (isEmailSent) {
				return "Password sent to your registered email";
			} else {
				return "Failed to sent email";
			}
		}
	}

	@Override
	public boolean changeAccountStatus(Integer userId, String status) {
		Optional<UserMaster> entity = userMasterRepo.findById(userId);
		if (entity.isPresent()) {
			UserMaster userMaster = entity.get();
			userMaster.setAccountStatus(status);
			userMasterRepo.save(userMaster);
			return true;
		}
		return false;
	}

	@Override
	public String login(Login login) {
		/*
		 * UserMaster userMaster = new UserMaster();
		 * userMaster.setEmail(login.getEmail());
		 * userMaster.setPassword(login.getPassword()); // Query By Example (QBE) //
		 * select * from user_master where email=? and pwd=? Example<UserMaster> example
		 * = Example.of(userMaster); List<UserMaster> entities =
		 * userMasterRepo.findAll(example);
		 */
		UserMaster entity = userMasterRepo.findByEmailAndPassword(login.getEmail(), login.getPassword());
		if (entity == null) {
			return "Invalid Credentials";
		} else {
			if (entity.getAccountStatus().equalsIgnoreCase("ACTIVE")) {
				return "Login Succesfull...";
			} else {
				return "Account Not Activated...";
			}
		}
	}

	private String generateRandomPwd() {
		String upperAlphas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerAlphas = "abcdefghijklmnopqrstuwxyz";
		String numbers = "1234567890";
		final String chars = upperAlphas + lowerAlphas + numbers;
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < 8; i++) {
			int randomIndex = random.nextInt(chars.length());
			sb.append(chars.charAt(randomIndex));
		}
		return sb.toString();
	}

	private String readEmailBody(String filename, String fullname, String pwd) {
		StringBuffer sb = new StringBuffer();
		String url = "";
		String mailBody = null;

		try {
			FileReader fileReader = new FileReader(filename);
			BufferedReader br = new BufferedReader(fileReader);

			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			br.close();
			mailBody = sb.toString();
			mailBody = mailBody.replace("{FULLNAME}", fullname);
			mailBody = mailBody.replace("{TEMP-PWD}", pwd);
			mailBody = mailBody.replace("{URL}", url);
			mailBody = mailBody.replace("{PWD}", pwd);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mailBody;
	}
}
