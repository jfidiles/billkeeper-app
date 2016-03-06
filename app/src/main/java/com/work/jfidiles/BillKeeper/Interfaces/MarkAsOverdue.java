package com.work.jfidiles.BillKeeper.Interfaces;

import com.work.jfidiles.BillKeeper.Response.BillTaskResponse;

public interface MarkAsOverdue {
    void setBillToOverdue(BillTaskResponse updateToOverdue);
}
