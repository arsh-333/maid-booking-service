package com.rupeek.maidbooking.dto;

import java.math.BigDecimal;

public class CancelResult {

    private final String bookingId;
    private final BigDecimal refundedAmount;

    public CancelResult(String bookingId, BigDecimal refundedAmount) {
        this.bookingId = bookingId;
        this.refundedAmount = refundedAmount;
    }

    public String getBookingId() {
        return bookingId;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }
}
