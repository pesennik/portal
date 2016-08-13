package com.github.pesennik.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransposeUtils {

    private static final Pattern BRACE_PATTERN = Pattern.compile("\\([^\\(]*\\)");

    private static final String[] NOTES = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};

    private static final int N_NOTES = NOTES.length;

    @NotNull
    public static String transposeText(@NotNull String text, int n) {
        StringBuffer res = new StringBuffer();
        Matcher matcher = BRACE_PATTERN.matcher(text);
        while (matcher.find()) {
            String val = matcher.group();
            String newChord = "(" + transposeNote(val.substring(1, val.length() - 1), n) + ")";
            matcher.appendReplacement(res, newChord);
        }
        matcher.appendTail(res);
        return res.toString();
    }

    @NotNull
    private static String transposeNote(@NotNull String chord, int n) {
        String note = getNote(chord);
        int oldNoteIdx = getNoteIdx(note);
        if (oldNoteIdx < 0) {
            return chord;
        }
        boolean isLower = note.toLowerCase().equals(note);
        boolean isH = note.toUpperCase().equals("H");

        String suffix = chord.substring(note.length());
        int newNoteIdx = (oldNoteIdx + n + N_NOTES) % N_NOTES;
        String newNote = NOTES[newNoteIdx];
        if (isH) {
            newNote = "H" + newNote.substring(1);
        }
        if (isLower) {
            newNote = newNote.substring(0, 1).toLowerCase() + newNote.substring(1);
        }
        return newNote + suffix;
    }

    private static String getNote(String chord) {
        if (chord.length() < 2) {
            return chord;
        }
        if (chord.charAt(1) == '#') {
            return chord.substring(0, 2);
        }
        return chord.substring(0, 1);
    }

    private static int getNoteIdx(@NotNull String note) {
        String n = note.toUpperCase();
        for (int i = 0; i < NOTES.length; i++) {
            if (NOTES[i].equals(n)) {
                return i;
            }
        }
        return -1;
    }
}
