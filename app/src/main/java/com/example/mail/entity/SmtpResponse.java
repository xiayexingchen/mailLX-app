package com.example.mail.entity;

public class SmtpResponse {
    private String response;
    private boolean isSuccess;

    public SmtpResponse(String response) {
        this.response = response;
        this.isSuccess = false;
    }

    public String getResponse() {
        return response;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public void parseResponse() {
        if (response.contains("250 OK")) {
            setSuccess(true);
        } else if (response.contains("354")) {
            setSuccess(true);
        } else if (response.contains("250 Send email Successful")) {
            setSuccess(true);
        }else if(response.contains("221 Bye"))
            setSuccess(true);
        // 可以根据需要继续增加其他SMTP状态码的解析
    }

    @Override
    public String toString() {
        return "SmtpResponse{" +
                "response='" + response + '\'' +
                ", isSuccess=" + isSuccess +
                '}';
    }
}
