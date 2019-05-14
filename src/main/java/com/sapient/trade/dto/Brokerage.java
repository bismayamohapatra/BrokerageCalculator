package com.sapient.trade.dto;

import com.sapient.trade.utils.TransactionType;

import java.io.Serializable;
import java.util.Date;

public class Brokerage implements Serializable{

    private static final long serialVersionUID = 4L;
    private String clientId;
    private TransactionType transactionType;
    private Date transactionDate;
    private String priority;
    private Long processingFee;

    public String getClientId() {
        return clientId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public String isPriority() {
        return priority;
    }

    public Long getProcessingFee() {
        return processingFee;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setProcessingFee(Long processingFee) {
        this.processingFee = processingFee;
    }
}
