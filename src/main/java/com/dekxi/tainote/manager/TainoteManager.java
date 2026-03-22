package com.dekxi.tainote.manager;

import com.dekxi.tainote.model.*;
import javafx.stage.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;

public class TainoteManager {
    public enum ImportResult {
        SUCCESS,
        ALREADY_EXISTS,
        INVALID,
        FAILED
    }
    public enum ExportResult {
        SUCCESS,
        NOT_FOUND,
        FAILED
    }
    private String tainoteFormat = """
            ---TAINOTE---
            id: %s
            title: %s
            author: %s
            status: %s
            created_at: %s
            modified_at: %s
            tags: %s
            ---TAINOTE---
            """;
    private File tainoteDir;

    public TainoteManager() {
        tainoteDir = new File("tainotes");
        ensureTainoteDir();
    }
    public UUID saveTainote(
            UUID uuid,
            String title,
            String[] tags,
            String content,
            String authorName,
            String status,
            String createdAt,
            String modifiedAt
    ) {
        if (tainoteExists(uuid)){
            updateTainote(
                    uuid,
                    title,
                    tags,
                    content,
                    authorName,
                    status,
                    modifiedAt
            );
            return uuid;
        }
        String localDateTime = getFormattedLocalDateTime();
        File file = new File(tainoteDir, uuid + ".tainote");
        String serializedTags = (tags.length > 0) ? joinTags(tags) : "Untagged";

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(String.format(
                    tainoteFormat,
                    uuid,
                    title,
                    authorName,
                    status,
                    localDateTime,
                    localDateTime,
                    serializedTags));
            fw.write(content);
        } catch (IOException e) {
            System.out.println("There is a problem writing on the tainote");
            e.printStackTrace();
        }
        return uuid;
    }

    public void deleteTainote(UUID id) {
        File file = getFilePath(id).toFile();
        if (file.exists())
            file.delete();
    }

    public Tainote readTainote(UUID id) {
        File file = getFilePath(id).toFile();
        if (!file.exists()) return null;
        Map<String, String> map = readRawTainote(id);
        return new Tainote(
                id.toString(),
                map.getOrDefault("title", "Untitled"),
                map.getOrDefault("author", "Anonymous"),
                map.getOrDefault("status", "Undecided"),
                map.getOrDefault("created_at", ""),
                map.getOrDefault("modified_at", ""),
                map.getOrDefault("content", ""),
                getTagsFromSerialized(map.getOrDefault("tags", "Untagged"))
        );
    }


    public void updateTainote(
            UUID id,
            String title,
            String[] tags,
            String content,
            String authorName,
            String status,
            String modifiedAt
    ) {
        Path path = getFilePath(id);
        Map<String, String> map = readRawTainote(id);
        String serializedTags = (tags.length > 0) ? joinTags(tags) : "Untagged";

        try (FileWriter fw = new FileWriter(path.toString())) {
            fw.write(String.format(
                    tainoteFormat,
                    id,
                    title,
                    authorName,
                    status,
                    map.getOrDefault("created", modifiedAt),
                    modifiedAt,
                    serializedTags));
            fw.write(content);
        } catch (IOException e) {
            System.out.println("There is a problem writing on the tainote");
            e.printStackTrace();
        }
    }
    public List<Tainote> getAllTainotes(){
        List<Tainote> notes = new ArrayList<>();
        File[] files = tainoteDir.listFiles((_, name) -> name.endsWith(".tainote"));
        if (files == null) return notes;

        for (File file : files) {
            UUID id = checkValidUUIDFromTainote(file.toPath());
            if (id == null) continue;
            Tainote note = readTainote(id);
            if (note != null) notes.add(note);
        }
        return notes;
    }
    public UUID checkFileIfTainote(File file) {
        return checkValidUUIDFromTainote(file.toPath());
    }
    public ImportResult importTainote(File selected) {
        if (selected == null) return ImportResult.INVALID;

        UUID id = checkValidUUIDFromTainote(selected.toPath());
        if (id == null) return ImportResult.INVALID;

        File dest = new File(tainoteDir, id + ".tainote");
        if (dest.exists()) return ImportResult.ALREADY_EXISTS;

        try {
            Files.copy(selected.toPath(), dest.toPath());
            return ImportResult.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return ImportResult.FAILED;
        }
    }

    public ImportResult forceImportTainote(File selected) {
        UUID id = checkValidUUIDFromTainote(selected.toPath());
        if (id == null) return ImportResult.INVALID;

        try {
            Files.copy(selected.toPath(),
                    new File(tainoteDir, id + ".tainote").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            return ImportResult.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return ImportResult.FAILED;
        }
    }
    public ExportResult exportTainote(UUID id, File dest) {
        Path source = getFilePath(id);
        if (!source.toFile().exists()) return ExportResult.NOT_FOUND;

        try {
            Files.copy(source, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return ExportResult.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return ExportResult.FAILED;
        }
    }
    public boolean tainoteExists(UUID id) {
        return getFilePath(id).toFile().exists();
    }

    private Path getFilePath(UUID id) {
        return Path.of(tainoteDir + File.separator + id + ".tainote");
    }

    private String getFormattedLocalDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    private String joinTags(String[] tags) {
        return Stream.of(tags).map(String::trim).map(String::toLowerCase).distinct().collect(Collectors.joining(","));
    }
    private List<String> getTagsFromSerialized(String joinedTags) {
        return Arrays.stream(joinedTags.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private Map<String, String> readRawTainote(UUID id) {
        Path path = getFilePath(id);
        var meta = new HashMap<String, String>();
        StringBuilder body = new StringBuilder();

        boolean inMeta = false;
        boolean metaDone = false;

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!metaDone && line.trim().equals("---TAINOTE---")) {
                    inMeta = !inMeta;
                    if (!inMeta) metaDone = true;
                    continue;
                }
                if (inMeta) {
                    if (line.isBlank()) continue;
                    String[] kv = line.split(":", 2);
                    if (kv.length == 2) meta.put(kv[0].trim(), kv[1].trim());
                } else if (metaDone) {
                    body.append(line).append('\n');
                }
            }
            meta.put("content", body.toString().stripTrailing());
        } catch (IOException e) {
            System.out.println("Unable to parse: " + id);
            return new HashMap<>();
        }
        return meta;
    }

    public UUID checkValidUUIDFromTainote(Path path) {
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            boolean inMeta = false;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equals("---TAINOTE---")) {
                    inMeta = !inMeta;
                    continue;
                }
                if (inMeta) {
                    if (line.isBlank()) continue;
                    String[] kv = line.split(":", 2);
                    if (kv[0].trim().equalsIgnoreCase("id"))
                        return checkIfValidUUID(kv[1].trim());
                }
            }
        } catch (IOException ignored) {}
        return null;
    }

    private UUID checkIfValidUUID(String s) {
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void ensureTainoteDir() {if (!tainoteDir.exists()) tainoteDir.mkdir();}
}