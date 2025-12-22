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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일입니다: " + email));
    }

    // 사용자 생성
    @Transactional
    public UserResponseDto createUser(UserCreateRequestDto dto) {
        if (isEmailDuplicate(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        User user = new User();
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

    // email로 사용자 DTO 조회
    public UserResponseDto findUserDtoByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일입니다: " + email));
        return new UserResponseDto(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일입니다: " + email));
    }

    public String findEmailById(Long id) {
        return userRepository.findById(id)
                .map(User::getEmail)
                .orElse(null);
    }

    // 사용자 정보 수정
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        // 이메일은 고유 식별자이므로 변경하지 않는다고 가정합니다.
        // DTO에 담긴 정보로 기존 엔티티의 값을 변경
        user.setNickname(dto.getNickname());

        // 트랜잭션이 끝날 때 변경된 내용을 감지하여 자동으로 UPDATE 쿼리 실행
        return new UserResponseDto(user);
    }

    // 사용자 삭제
    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }
    
    // 이메일 중복 검사
    public boolean isEmailDuplicate(String email) {
        return userRepository.findByEmail(email).isPresent();
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
    
    // 관리자 또는 로그인 사용자와 대상이 동일한 경우 true
    public boolean isAuthorized(String targetEmail, UserDetails currentUser) {
        if (currentUser == null) {
            return false;
        }

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || currentUser.getUsername().equals(targetEmail);
    }
}
