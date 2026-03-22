package com.dekxi.tainote.config;

import java.util.*;

public class AppState {
    private UUID currentNoteId;

    public UUID getCurrentNoteId() {return currentNoteId;}
    public void setCurrentNoteId(UUID noteId){this.currentNoteId = noteId;}
}
