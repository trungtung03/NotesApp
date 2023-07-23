package com.example.notepad.model;

public class NotesModel {
    private int takeNoteID;
    private String title;
    private String image;
    private String timeNote;
    private String notes;
    private int milliSeconds;
    private String timeSet;
    private String timeOld;
    private String passwordNote;

    public String getPasswordNote() {
        return passwordNote;
    }

    public void setPasswordNote(String passwordNote) {
        this.passwordNote = passwordNote;
    }

    public String getTimeOld() {
        return timeOld;
    }

    public void setTimeOld(String timeOld) {
        this.timeOld = timeOld;
    }

    public String getTimeSet() {
        return timeSet;
    }

    public void setTimeSet(String timeSet) {
        this.timeSet = timeSet;
    }

    public int getMilliSeconds() {
        return milliSeconds;
    }

    public void setMilliSeconds(int milliSeconds) {
        this.milliSeconds = milliSeconds;
    }

    public int getTakeNoteID() {
        return takeNoteID;
    }

    public void setTakeNoteID(int takeNoteID) {
        this.takeNoteID = takeNoteID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeNote() {
        return timeNote;
    }

    public void setTimeNote(String timeNote) {
        this.timeNote = timeNote;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public NotesModel(int takeNoteID, String title, String image, String timeNote, String notes, int milliSeconds, String timeSet, String timeOld, String passwordNote) {
        this.takeNoteID = takeNoteID;
        this.title = title;
        this.image = image;
        this.timeNote = timeNote;
        this.notes = notes;
        this.milliSeconds = milliSeconds;
        this.timeSet = timeSet;
        this.timeOld = timeOld;
        this.passwordNote = passwordNote;
    }

    public NotesModel() {
    }
}
