package com.example.church.model;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Component
@Entity
public class Ulogin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	private String email;
	private String username;
	private String password;
	private String resetToken;
    private LocalDateTime tokenExpiry;
    private String otp;
    private Instant expiredAt;
    private boolean verified;
    
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getResetToken() {
		return resetToken;
	}
	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}
	public LocalDateTime getTokenExpiry() {
		return tokenExpiry;
	}
	public void setTokenExpiry(LocalDateTime tokenExpiry) {
		this.tokenExpiry = tokenExpiry;
	}
	
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public Instant getOtpExpiresAt() {
		return expiredAt;
	}
	public void setExpiredAt(Instant expiredAt) {
		this.expiredAt = expiredAt;
	}
	public boolean getVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public Ulogin()
	{
		
	}

}
