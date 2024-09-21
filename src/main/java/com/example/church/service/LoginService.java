package com.example.church.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.church.UserPrincipal;
import com.example.church.model.Ulogin;
import com.example.church.repo.ChurchLoginRepo;

@Service
@Primary
public class LoginService implements UserDetailsService{
	
	@Autowired
	private ChurchLoginRepo repo;
	
	@Autowired
	private JavaMailSender mailSender;


	public void addUserDetails(Ulogin ulogin) {
	    repo.save(ulogin);
	}

	 public boolean emailExists(String email) {
	        return repo.existsByEmail(email);
	    }
	
	public boolean unameExists(String username) {
		return repo.existsByUsername(username);
	}
	
	public Optional<Ulogin> findUserByEmail(String email) {
        return repo.findByEmail(email);
    }
	
	public void generateResetToken(String email)
	{
		Optional<Ulogin> user = repo.findByEmail(email);
		if(user.isPresent())
		{
			String token = UUID.randomUUID().toString();
			Ulogin ulogin = user.get();
			ulogin.setResetToken(token);
			ulogin.setTokenExpiry(LocalDateTime.now().plusHours(1));
			repo.save(ulogin);
			
			String resetLink = "http://localhost:8080/forget-password?token=" + token;
			sendPasswordResetEmail(ulogin.getEmail(), resetLink);
		}
		else
		{
			throw new RuntimeException("user not found "+ email);
		}
	}



	public void updatePassword(Ulogin user, String newPassword)
	{
		user.setPassword(newPassword);
		user.setResetToken(null);
		user.setTokenExpiry(null);
		repo.save(user);
	}
	
	private void sendPasswordResetEmail(String email, String resetLink) {
		System.out.println("Sending email to: " + email + " with link: " + resetLink);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email); 
	    message.setSubject("Password Reset Request");
	    message.setText("Click the following link to reset your password: " + resetLink);
	    mailSender.send(message);
		
	}

	public Ulogin validateResetToken(String token) {
		{
			Optional<Ulogin> user = repo.findByResetToken(token);
			if (user.isPresent()) {
	            Ulogin ulogin = user.get();
	            if (ulogin.getTokenExpiry().isAfter(LocalDateTime.now())) {
	            	return ulogin;
	            }
	            else
	            {
	            	throw new RuntimeException("Token has expired");
	            }
			 } else {
		            throw new RuntimeException("Invalid token");
	            }
		}
	}
	
	 public void sendOtpEmail(Ulogin ulogin, String otp) {
		    String email = ulogin.getEmail();
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(email);
	        message.setSubject("Your OTP Code");
	        message.setText("Your OTP code is: " + otp);

	        mailSender.send(message);
	    }
	 

	 public boolean verifyOtp(String email, String otp) {
		    Optional<Ulogin> optionalUser = repo.findByEmail(email);

		    if (optionalUser.isPresent()) {
		        Ulogin user = optionalUser.get();
		        if (otp.equals(user.getOtp()) && Instant.now().isBefore(user.getOtpExpiresAt())) {
		            user.setOtp(null); 
		            user.setVerified(true); 
		            repo.save(user);
		            return true;
		        } else {
		            System.out.println("OTP is invalid or expired.");
		        }
		    } else {
		        System.out.println("No user found with email: " + email);
		    }
		    return false;
		}


	    private String generateOtp() {
	        SecureRandom random = new SecureRandom();
	        byte[] otpBytes = new byte[4]; 
	        random.nextBytes(otpBytes);
	        return Base64.getUrlEncoder().withoutPadding().encodeToString(otpBytes);
	    }


		public void registerUser(Ulogin ulogin) {
			String otp = generateOtp();
	        Instant otpExpiresAt = Instant.now().plusSeconds(600); 

	   
	        ulogin.setOtp(otp);
	        ulogin.setExpiredAt(otpExpiresAt); 
	        ulogin.setVerified(false);

	        repo.save(ulogin); 

	        
	        sendOtpEmail(ulogin, otp);
		}
		
		
		 @Override
		 public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
			 	Ulogin user = repo.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		        if (user == null) {
		            throw new UsernameNotFoundException("User not found");
		        }
		        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
		    }

}


//@Override
//public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//    Ulogin user = repo.findByUsername(username);
//    if(user == null) {
//        throw new UsernameNotFoundException("User not found with username: " + username);
//    }
//    return new UserPrincipal(user);
//}
//
