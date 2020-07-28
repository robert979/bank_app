package com.proiectfinal.bankapp.repository;

import com.proiectfinal.bankapp.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedNativeQuery;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query(
            value = "select c.id from card_number c where c.id = ?1",
            nativeQuery = true)
    long findIdByCardNumber(long card_number);



}

