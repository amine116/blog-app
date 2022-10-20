package com.amine.blog.model;

import java.util.Map;

public class ReportingContext {

    private Map<String, String> contexts;

    public ReportingContext(){}

    public ReportingContext(Map<String, String> contexts) {
        this.contexts = contexts;
    }


    public Map<String, String> getContexts() {
        return contexts;
    }

    public void setContexts(Map<String, String> contexts) {
        this.contexts = contexts;
    }
}
