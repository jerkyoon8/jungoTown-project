package com.juwon.springcommunity.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component("timeTrace")
public class TimeTrace {

    private static final int SEC = 60;
    private static final int MIN = 60;
    private static final int HOUR = 24;
    private static final int DAY = 30;
    private static final int MONTH = 12;

    public String calculateTime(LocalDateTime date) {
        if (date == null) {
            return null;
        }

        long diffTime = ChronoUnit.SECONDS.between(date, LocalDateTime.now());
        String msg = null;

        if (diffTime < SEC) {
            msg = "방금 전";
        } else if ((diffTime /= SEC) < MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= MIN) < HOUR) {
            msg = diffTime + "시간 전";
        } else if ((diffTime /= HOUR) < DAY) {
            msg = diffTime + "일 전";
        } else if ((diffTime /= DAY) < MONTH) {
            msg = diffTime + "달 전";
        } else {
            msg = diffTime + "년 전";
        }

        return msg;
    }
}
