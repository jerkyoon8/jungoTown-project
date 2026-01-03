package com.juwon.springcommunity.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate redisTemplate;

    // 인증 번호 유효 시간 (3분)
    private static final long VERIFICATION_CODE_TTL = 60 * 3;

    /**
     * 이메일로 인증 번호를 전송하고 Redis에 저장합니다.
     * @param email 수신자 이메일
     */
    public void sendVerificationCode(String email) {
        String code = createRandomCode();
        
        // 1. Redis에 인증 번호 저장 (유효기간 3분)
        saveVerificationCodeToRedis(email, code);

        // 2. 이메일 발송
        sendMail(email, code);
    }

    /**
     * 사용자가 입력한 인증 번호를 검증합니다.
     * @param email 이메일
     * @param code 사용자가 입력한 인증 번호
     * @return 검증 성공 여부
     */
    public boolean verifyCode(String email, String code) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String savedCode = valueOperations.get(email);

        if (savedCode == null) {
            log.info("인증 번호가 만료되었거나 존재하지 않습니다. Email: {}", email);
            return false;
        }

        if (savedCode.equals(code)) {
            log.info("인증 성공. Email: {}", email);
            // 인증 성공 후 Redis에서 삭제 (선택 사항, 재사용 방지)
            redisTemplate.delete(email);
            return true;
        }

        log.info("인증 번호 불일치. Email: {}, Input: {}, Saved: {}", email, code, savedCode);
        return false;
    }

    // 6자리 랜덤 숫자 생성
    private String createRandomCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10));
        }
        return key.toString();
    }

    // Redis 저장 로직
    private void saveVerificationCodeToRedis(String email, String code) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(email, code, Duration.ofSeconds(VERIFICATION_CODE_TTL));
        log.info("Redis 저장 완료 - Email: {}, Code: {}", email, code);
    }

    // 실제 메일 발송 로직
    private void sendMail(String email, String code) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[Spring Community] 회원가입 인증 번호입니다.");
            helper.setText("<div>이메일 인증 번호: <strong>" + code + "</strong></div>", true);
            
            javaMailSender.send(mimeMessage);
            log.info("이메일 발송 성공 - To: {}", email);

        } catch (MessagingException e) {
            log.error("이메일 발송 실패", e);
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.");
        }
    }
}
