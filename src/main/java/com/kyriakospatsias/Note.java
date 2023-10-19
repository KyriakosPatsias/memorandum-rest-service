package com.kyriakospatsias;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.time.LocalDateTime;


@DynamoDBTable(tableName = "Memorandum-notes")
public class Note {

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String noteId;
    private String title;
    private String body;
    @DynamoDBTypeConverted( converter = LocalDateTimeConverter.class )
    private LocalDateTime createdOn;
    @DynamoDBTypeConverted( converter = LocalDateTimeConverter.class )
    private LocalDateTime updatedOn;

    public Note(){

    }

    public Note(String title, String body, LocalDateTime createdOn, LocalDateTime updatedOn) {
        this.title = title;
        this.body = body;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    @DynamoDBAttribute
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBAttribute
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + noteId +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }

    static public class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

        @Override
        public String convert( final LocalDateTime time ) {
            return time.toString();
        }

        @Override
        public LocalDateTime unconvert( final String stringValue ) {
            return LocalDateTime.parse(stringValue);
        }
    }
}
