package com.amine.blog.model;

public class AccountRecoverInfo {

    private String username, oldPassword, newPassword, oldPhone, newPhone, countryDialCode;

    public AccountRecoverInfo(){}

    public AccountRecoverInfo(String username, String oldPassword,
                              String newPassword, String oldPhone, String newPhone, String countryDialCode) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.oldPhone = oldPhone;
        this.newPhone = newPhone;
        this.countryDialCode = countryDialCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPhone() {
        return oldPhone;
    }

    public void setOldPhone(String oldPhone) {
        this.oldPhone = oldPhone;
    }

    public String getNewPhone() {
        return newPhone;
    }

    public void setNewPhone(String newPhone) {
        this.newPhone = newPhone;
    }

    public String getCountryDialCode() {
        return countryDialCode;
    }

    public void setCountryDialCode(String countryDialCode) {
        this.countryDialCode = countryDialCode;
    }
}
