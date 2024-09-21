package com.example.church.controller;

import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.church.model.Ulogin;
import com.example.church.repo.ChurchLoginRepo;
import com.example.church.service.JwtService;
import com.example.church.service.LoginService;

@RestController
public class LoginController {
	
	@Autowired
	private LoginService service;
	
	@Autowired
	private JwtService jwtservice;

	@Autowired
	AuthenticationManager authenticationManager;

	
	@GetMapping({"/home","/"})
	public String greet()
	{
		return "hello world";
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> requestBody) {
		String usernameOrEmail = requestBody.get("usernameOrEmail");
		String password = requestBody.get("password");
		 Map<String, Object> response = new HashMap<>();

	    try {
	        Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
	           	        );

	        if (authentication.isAuthenticated()) {
	        	 String token = jwtservice.generateToken(usernameOrEmail);
	        	 response.put("token", token);
	             response.put("status", "Logged in Successfully!");
	             return ResponseEntity.ok(response);
	        } else {
	        	response.put("status", "Authentication failed");
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	        }
	    } catch (AuthenticationException e) {
	    	 response.put("status", "Invalid username or password");
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	    }
	}
	
	@PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Ulogin ulogin) {

		if (service.emailExists(ulogin.getEmail())) 
        {
            return ResponseEntity.badRequest().body("Email already in use ");
        }
        else if(service.unameExists(ulogin.getUsername()))
        {
        	return ResponseEntity.badRequest().body("username already in use ");
        }	
        service.registerUser(ulogin);
        return ResponseEntity.ok("User registered successfully please Check Email for Verification");
    }
	
	   @PostMapping("/verify-otp")
	    public String verifyOtp(@RequestBody Map<String, String> requestBody) {
	    	String email = requestBody.get("email");
	    	String otp = requestBody.get("otp");
	        boolean verified = service.verifyOtp(email, otp);
	        return verified ? "OTP verified successfully" : "OTP verification failed";
	    }
	
	@PostMapping("forget-password")
	public ResponseEntity<Ulogin> forgetPassword(@RequestBody Map<String, String> requestBody)
	{
		 String email = requestBody.get("email");
		 try
		 { 
			 service.generateResetToken(email);
			 return new ResponseEntity<>(HttpStatus.OK); 
		 }
		 catch (RuntimeException e) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	}
	
	@GetMapping("/reset-password")
	public ResponseEntity<String> validateResetToken(@RequestParam("token") String token) {
		try {
            service.validateResetToken(token);
            return new ResponseEntity<>("Token is valid. You can now reset your password.", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
	}
	
	@PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> requestBody) {
		String token = requestBody.get("token");
        String newPassword = requestBody.get("password");
        try {
            Ulogin user = service.validateResetToken(token);
            service.updatePassword(user, newPassword);
            return new ResponseEntity<>("Password updated successfully.", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
	}

}


//@PostMapping("signup")
//public ResponseEntity<String> addUserDetails(@RequestBody Ulogin ulogin)
//{
//	        if (service.emailExists(ulogin.getEmail())) 
//	        {
//	            return ResponseEntity.badRequest().body("Email already in use ");
//	        }
//	        else if(service.unameExists(ulogin.getUsername()))
//	        {
//	        	return ResponseEntity.badRequest().body("username already in use ");
//	        }
//		        service.addUserDetails(ulogin);
//		        return ResponseEntity.ok("User registered successfully");
//}	


