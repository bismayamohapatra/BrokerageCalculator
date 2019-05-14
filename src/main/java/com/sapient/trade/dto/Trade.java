package com.sapient.trade.dto;

import com.sapient.trade.utils.TransactionType;

import java.io.Serializable;
import java.util.Date;

/**
 * This is a DTO class to store Trade Object
 */
public class Trade implements Serializable {

    private static final long serialVersionUID = 4L;

    private String externalTransactionId;
    private String clientId;
    private String securityId;
    private TransactionType transactionType;
    private Date transactionDate;
    private Float marketValue;
    private String priority;

    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setMarketValue(Float marketValue) {
        this.marketValue = marketValue;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getExternalTransactionId() {
        return externalTransactionId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSecurityId() {
        return securityId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public Float getMarketValue() {
        return marketValue;
    }

    public String isPriority() {
        return priority;
    }


    public static boolean checkIntraday(Trade t1, Trade t2){

        if(t1.getClientId() == t2.getClientId() && t1.getSecurityId() == t2.getSecurityId() && t1.getTransactionDate() == t1.getTransactionDate()){
            if(t1.getTransactionType().toString().equalsIgnoreCase("BUY") && t2.getTransactionType().toString().equalsIgnoreCase("SELL")
                    || t1.getTransactionType().toString().equalsIgnoreCase("SELL") && t2.getTransactionType().toString().equalsIgnoreCase("BUY") ){
                return true;
            }
        }

        return false;
    }

}
