package com.amine.blog.model;

public class ReportToBlog {
    private String reporterUserName, articleId, commentId, reportId, type, explanation;
    private MyTime time;
    private ReportingContext reportingContext;

    public ReportToBlog(){}

    public ReportToBlog(String reporterUserName, String articleId,
                        String commentId, String reportId, String type, String explanation, MyTime time,
                        ReportingContext reportingContext) {
        this.reporterUserName = reporterUserName;
        this.articleId = articleId;
        this.commentId = commentId;
        this.reportId = reportId;
        this.type = type;
        this.explanation = explanation;
        this.time = time;
        this.reportingContext = reportingContext;
    }

    public String getReporterUserName() {
        return reporterUserName;
    }

    public void setReporterUserName(String reporterUserName) {
        this.reporterUserName = reporterUserName;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public MyTime getTime() {
        return time;
    }

    public void setTime(MyTime time) {
        this.time = time;
    }

    public ReportingContext getReportingContext() {
        return reportingContext;
    }

    public void setReportingContext(ReportingContext reportingContext) {
        this.reportingContext = reportingContext;
    }
}
