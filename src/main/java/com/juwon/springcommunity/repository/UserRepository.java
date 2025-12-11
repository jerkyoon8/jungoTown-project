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

    Optional<User> findByUsername(String username);

    List<User> findByIds(@Param("ids") Set<Long> ids);

    void update(User user);

    void delete(Long id);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    String findUsernameById(Long id);
}

