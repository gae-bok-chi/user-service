package com.gaebokchi.userservice;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class test {
    public static void main(String[] args) {
        Date now = Date.from(Instant.now());
        Date date = Date.from(Instant.now().plus(5, ChronoUnit.MINUTES));

        System.out.println(now);
        System.out.println(date);
        System.out.println(date.after(now));
    }
}
