package com.vallabha.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vallabha.entity.UserMaster;

public interface UserMasterRepo extends JpaRepository<UserMaster,Serializable>{
	
	// findBy method
	public UserMaster findByEmailAndPassword(String email, String Password);
	
	// findBy method
	public UserMaster findByEmail(String email);
}
