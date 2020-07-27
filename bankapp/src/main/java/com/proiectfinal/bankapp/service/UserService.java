package com.proiectfinal.bankapp.service;

import com.proiectfinal.bankapp.domain.User;
import com.proiectfinal.bankapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor

public class UserService {
    private final UserRepository userRepository;

    public User findUserById (long id){
        return userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("User with the id " + id + " does not exists"));
    }

    public Long findCnpById (long id){
        return findUserById(id).getCnp();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void updateFields(long userId, User user) {
        User userToBeUpdated = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with the id " + userId + " does not exists"));
        userToBeUpdated.setLastName(user.getLastName());
        userToBeUpdated.setFirstName(user.getFirstName());
        userToBeUpdated.setCnp(user.getCnp());
        ;
    }
}


