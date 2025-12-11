package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.Role;
import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.dto.UserCreateRequestDto;
import com.juwon.springcommunity.dto.UserResponseDto;
import com.juwon.springcommunity.dto.UserUpdateRequestDto;
import com.juwon.springcommunity.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인터페이스

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    // 사용자 생성
    @Transactional
    public UserResponseDto createUser(UserCreateRequestDto dto) {
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // 비밀번호 암호화
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname()); // 닉네임 설정 추가
        user.setRole(Role.USER);

        userRepository.save(user);
        return new UserResponseDto(user);
    }

    // 모든 사용자 조회
    public List<UserResponseDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::new) // .map(user -> new UserResponseDto(user))
                .collect(Collectors.toList());
    }

    // id로 사용자 조회
    public UserResponseDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        return new UserResponseDto(user);
    }

    // 사용자 정보 수정
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        // DTO에 담긴 정보로 기존 엔티티의 값을 변경
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        // userRepository.update(user) 호출이 필요 없음
        // 트랜잭션이 끝날 때 변경된 내용을 감지하여 자동으로 UPDATE 쿼리 실행

        return new UserResponseDto(user);
    }

    // 사용자 삭제
    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    // 사용자 ID 중복 \검사
    public boolean isUsernameDuplicate(String username) {
        
        // repository 에서 존재여부를 검사하는 쿼리를 쏜다
        return userRepository.existsByUsername(username);
    }

    // userId 정보를 기반으로 user 를 map 으로 반환.
    public Map<Long, User> findUserMapByIds(Set<Long> userIds){
        if( userIds == null || userIds.isEmpty()){
            return Collections.emptyMap();
        }
        List<User> users = userRepository.findByIds(userIds);
        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    // 닉네임 중복 검사.
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public String findUsernameById(Long id) {
        return userRepository.findUsernameById(id);
    }


    // 관리자 또는 로그인 사용자와 대상이 동일한 경우 true
    public boolean isAuthorized(String targetUsername, UserDetails currentUser) {
        if (currentUser == null) {
            return false;
        }

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));


        return isAdmin || currentUser.getUsername().equals(targetUsername);
    }
}
