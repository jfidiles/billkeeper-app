package com.example.jimmy.navigationdrawer.Response;

import com.example.jimmy.navigationdrawer.Bill;

import org.springframework.http.HttpStatus;

/**
 * Created by Jimmy on 1/14/2016.
 */
public class BillTaskResponse {
        public Bill[] bills;
        public String error;
        public HttpStatus code;
}
