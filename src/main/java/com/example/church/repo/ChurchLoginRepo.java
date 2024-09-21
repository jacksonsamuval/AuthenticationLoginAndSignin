package com.example.church.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.church.model.Ulogin;

@Repository
public interface ChurchLoginRepo extends JpaRepository<Ulogin,Integer>
{	
//	Ulogin findByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	Optional<Ulogin> findByEmail(String email);

	Optional<Ulogin> findByResetToken(String token);

	Ulogin findByUsernameOrEmail(String usernameOrEmail, String usernameOrEmail1);

	
}
