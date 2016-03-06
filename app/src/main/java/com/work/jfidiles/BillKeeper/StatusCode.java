package com.work.jfidiles.BillKeeper;

import org.springframework.http.HttpStatus;

public class StatusCode {

    public static boolean isOk(HttpStatus code) {
        return code == HttpStatus.OK;
    }

    public static boolean isUnauthorised(HttpStatus code) {
        return code == HttpStatus.UNAUTHORIZED;
    }

    public static boolean isBadRequest(HttpStatus code) {
        return code == HttpStatus.BAD_REQUEST;
    }

    public static boolean isForbidden(HttpStatus code) {
        return code == HttpStatus.FORBIDDEN;
    }

    public static boolean isCreated(HttpStatus code) {
        return code == HttpStatus.CREATED;
    }

    public static boolean isNoContent (HttpStatus code) {
        return code == HttpStatus.NO_CONTENT;
    }

    public static boolean isNotFound (HttpStatus code) {
        return code == HttpStatus.NOT_FOUND;
    }

}
