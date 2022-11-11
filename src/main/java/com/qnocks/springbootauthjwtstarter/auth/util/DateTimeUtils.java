package com.qnocks.springbootauthjwtstarter.auth.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtils {

    public static Date toDate(@NotNull LocalDateTime value) {
        return Date.from(value.toInstant(ZoneOffset.UTC));
    }

    public static long toSeconds(@NotNull LocalDateTime value) {
        return value.toEpochSecond(ZoneOffset.UTC);
    }
}
