package com.cinemaabyss.events.model;

public class PaymentEvent extends BaseEvent {
    private Integer paymentId;
    private Integer userId;
    private Double amount;
    private String status;
    private String methodType;

    public PaymentEvent() {
        super();
    }

    public PaymentEvent(Integer paymentId, Integer userId, Double amount, String status, String methodType) {
        super(determineEventType(status));
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.methodType = methodType;
    }

    private static EventType determineEventType(String status) {
        switch (status) {
            case "completed": return EventType.PAYMENT_SUCCESS;
            case "failed": return EventType.PAYMENT_FAILURE;
            default: throw new IllegalArgumentException("Unknown payment status: " + status);
        }
    }

    // Геттеры и сеттеры
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        setType(determineEventType(status));
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }
}