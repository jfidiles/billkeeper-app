package com.example.jimmy.BillKeeper.Interfaces;

import com.example.jimmy.BillKeeper.Response.BillTaskResponse;

/**
 * Created by Jimmy on 2/14/2016.
 */
public interface MarkAsOverdue {
    void setBillToOverdue(BillTaskResponse updateToOverdue);
}
