package model;

import java.sql.Date;
import java.time.LocalDateTime;

public class Log {

    private int logId;              // Log'un benzersiz kimliği
    private int customerId;         // İlgili müşteri ID'si
    private int orderId;            // İlgili sipariş ID'si (Opsiyonel)
    private Date logDate;  // Log'un oluşturulma tarihi ve saati
    private String logType;         // Log türü (Hata, Bilgi, Uyarı, vb.)
    private String logDetails;

    private String customerType;      // Müşteri Türü (Standart, Premium vb.)
    private String productName;           // Ürün adı
    private int purchasedQuantity;    // Satın alınan miktar
    private LocalDateTime transactionTime; // İşlem zamanı
    private String transactionResult; // İşlem sonucu (Başarılı, Hata vb.)
    private Double logPriorities;
    private double waitingTime;

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Log(){

    }

    public Log(int logId, int customerId, int orderId, Date logDate, String logType,
               String logDetails, String customerType, String productName, int purchasedQuantity,
               LocalDateTime transactionTime, String transactionResult) {
        this.logId = logId;
        this.customerId = customerId;
        this.orderId = orderId;
        this.logDate = logDate;
        this.logType = logType;
        this.logDetails = logDetails;
        this.customerType = customerType;
        this.productName = productName;
        this.purchasedQuantity = purchasedQuantity;
        this.transactionTime = transactionTime;
        this.transactionResult = transactionResult;
    }

    public Double getLogPriorities() {
        return logPriorities;
    }

    public void setLogPriorities(Double logPriorities) {
        this.logPriorities = logPriorities;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public String getLogDetails() {
        return logDetails;
    }

    public void setLogDetails(String logDetails) {
        this.logDetails = logDetails;
    }

    // Getters and Setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPurchasedQuantity() {
        return purchasedQuantity;
    }

    public void setPurchasedQuantity(int purchasedQuantity) {
        this.purchasedQuantity = purchasedQuantity;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionResult() {
        return transactionResult;
    }

    public void setTransactionResult(String transactionResult) {
        this.transactionResult = transactionResult;
    }

    // toString Method for Debugging

    @Override
    public String toString() {
        return "Log{" +
                "logId=" + logId +
                ", customerId=" + customerId +
                ", orderId=" + orderId +
                ", logDate=" + logDate +
                ", logType='" + logType + '\'' +
                ", logDetails='" + logDetails + '\'' +
                ", customerType='" + customerType + '\'' +
                ", productName='" + productName + '\'' +
                ", purchasedQuantity=" + purchasedQuantity +
                ", transactionTime=" + transactionTime +
                ", transactionResult='" + transactionResult + '\'' +
                '}';
    }
}
