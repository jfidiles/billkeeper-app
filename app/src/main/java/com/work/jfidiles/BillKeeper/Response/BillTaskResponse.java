package com.work.jfidiles.BillKeeper.Response;

import com.work.jfidiles.BillKeeper.Bill;

import org.springframework.http.HttpStatus;

/**
 * Created by Jimmy on 1/14/2016.
 */
public class BillTaskResponse {
        public Bill[] bills;
        public String error;
        public HttpStatus code;
}
