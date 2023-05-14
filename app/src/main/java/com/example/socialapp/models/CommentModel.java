package com.example.socialapp.models;

public class CommentModel {
    private String commentBody;
    private long commentedAt;
    private String commentedBy;

    public CommentModel() {
    }

    public CommentModel(String commentBody, long commentedAt, String commentedBy) {
        this.commentBody = commentBody;
        this.commentedAt = commentedAt;
        this.commentedBy = commentedBy;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public long getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(long commentedAt) {
        this.commentedAt = commentedAt;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }
}
