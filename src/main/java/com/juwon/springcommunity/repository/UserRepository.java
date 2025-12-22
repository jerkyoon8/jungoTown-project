package com.juwon.springcommunity.repository;

import com.juwon.springcommunity.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper
public interface UserRepository {

    void save(User user);

    List<User> findAll();

    Optional<User> findById(Long id);

    List<User> findByIds(Set<Long> ids);

    void delete(Long id);
    
    boolean existsByNickname(String nickname);
    
    Optional<User> findByEmail(String email);
}

