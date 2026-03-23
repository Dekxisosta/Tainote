package com.dekxi.tainote.app;

import com.dekxi.tainote.model.*;

import java.util.*;

public class AppState {
    private UUID currentNoteId;
    private boolean hasUnsavedChanges;
    private Tainote lastSavedTainote;
    private boolean isLoadingNote;
    private boolean hasTyped;

    public UUID getCurrentNoteId() { return currentNoteId; }
    public boolean hasUnsavedChanges() { return hasUnsavedChanges; }
    public Tainote getLastSavedTainote() { return lastSavedTainote; }
    public boolean hasTyped(){return hasTyped;}

    public void setCurrentNoteId(UUID noteId) { this.currentNoteId = noteId; }
    public void setHasUnsavedChanges(boolean flag) { this.hasUnsavedChanges = flag; }
    public void setLastSavedTainote(Tainote tainote) { this.lastSavedTainote = tainote; }
    public void setIsLoadingNote(boolean flag){this.isLoadingNote = flag;}
    public void setHasTyped(boolean flag) { this.hasTyped = flag; }

    public boolean isLoadingNote(){return isLoadingNote;}
    public boolean isNewNote() { return lastSavedTainote == null; }
    public boolean hasOpenNote() { return currentNoteId != null; }
}
