package com.jonathan.firebase_custom_token.Repository;

import com.jonathan.firebase_custom_token.Entity.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

}
