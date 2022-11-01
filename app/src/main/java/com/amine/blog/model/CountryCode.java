package com.amine.blog.model;

public class CountryCode {

    private String name, isoCode, dialCode, flagUrl;

    public CountryCode(){}

    public CountryCode(String name, String isoCode, String dialCode, String flagUrl) {
        this.name = name;
        this.isoCode = isoCode;
        this.dialCode = dialCode;
        this.flagUrl = flagUrl;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }
}
