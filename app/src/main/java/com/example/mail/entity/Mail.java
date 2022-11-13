package com.example.mail.entity;

import java.sql.Timestamp;

public class Mail {
    private Integer mid;
    private String senderEmail;
    private String receiverEmail;
    private Timestamp sendTime;
    private String subject;
    private String body;
    private Boolean read;
    private Boolean deleted;
    private Boolean tag;
    private Boolean send;
    private String annexUrl;
    private String summary;
    private Integer size;

    public Mail() {
    }

    public Mail(String senderAddress, String reciverAddress, Timestamp mailDate, String subject, String content) {
        this.senderEmail = senderAddress;
        this.receiverEmail = reciverAddress;
        this.sendTime = mailDate;
        this.subject = subject;
        this.body = content;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getTag() {
        return tag;
    }

    public void setTag(Boolean tag) {
        this.tag = tag;
    }

    public Boolean getSend() {
        return send;
    }

    public void setSend(Boolean send) {
        this.send = send;
    }

    public String getAnnexUrl() {
        return annexUrl;
    }

    public void setAnnexUrl(String annexUrl) {
        this.annexUrl = annexUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "mid=" + mid +
                ", senderEmail='" + senderEmail + '\'' +
                ", receiverEmail='" + receiverEmail + '\'' +
                ", sendTime=" + sendTime +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", read=" + read +
                ", deleted=" + deleted +
                ", tag=" + tag +
                ", send=" + send +
                ", annexUrl='" + annexUrl + '\'' +
                ", summary='" + summary + '\'' +
                ", size=" + size +
                '}';
    }
}
