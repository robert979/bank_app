package com.proiectfinal.bankapp.repository;

import com.proiectfinal.bankapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository

public interface UserRepository extends JpaRepository<User,Long> {


}
