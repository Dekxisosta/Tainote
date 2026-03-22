package com.dekxi.tainote.db;

import com.dekxi.tainote.model.*;

import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:tainote.db";
    private Connection conn;
    private static final boolean IS_DEBUG = true;

    public DatabaseManager(){
        connect();
        initSchema();
    }
    public void insertNote(
            String id,
            String title,
            String content,
            String authorName,
            String status,
            String createdAt,
            String modifiedAt
    ){
        if (conn == null) return;
        if (noteExists(id)){
            modifyNote(
                    id,
                    title,
                    content,
                    authorName,
                    status,
                    createdAt,
                    modifiedAt
            );
            System.out.println("Attempted to modify note instead of insertion...");
            return;
        }
        String sql = """
                INSERT INTO notes(
                    id,
                    title,
                    author,
                    status,
                    created_at,
                    modified_at,
                    content
                )
                VALUES
                    (?,?,?,?,?,?,?);
                """;
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, authorName);
            pstmt.setString(4, status);
            pstmt.setString(5, createdAt);
            pstmt.setString(6, modifiedAt);
            pstmt.setString(7, content);
            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void deleteNote(
            String id
    ){
        if(conn == null) return;
        if(!noteExists(id)) return;
        String sql = """
                DELETE FROM notes WHERE id = ?;
        """;
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            System.out.println("SQL: " + id);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public List<TainotePreview> getAllNotes() {
        if (conn == null) return new ArrayList<>();
        String sql = "SELECT id, title, author, status, created_at, modified_at FROM notes ORDER BY modified_at DESC";
        List<TainotePreview> notes = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                notes.add(new TainotePreview(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("modified_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public void modifyNote(
            String id,
            String title,
            String content,
            String authorName,
            String status,
            String createdAt,
            String modifiedAt
    ) {
        if (conn == null) return;
        String sql = """
            UPDATE notes SET
                title = ?,
                author = ?,
                status = ?,
                created_at = ?,
                modified_at = ?,
                content = ?
            WHERE id = ?;
            """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, authorName);
            pstmt.setString(3, status);
            pstmt.setString(4, createdAt);
            pstmt.setString(5, modifiedAt);
            pstmt.setString(6, content);
            pstmt.setString(7, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<TainotePreview> searchAll(String query) {
        if (conn == null) return new ArrayList<>();
        String sql = """
            SELECT DISTINCT n.id, n.title, n.author, n.modified_at
            FROM notes n
            LEFT JOIN tags t ON n.id = t.note_id
            WHERE n.title LIKE ? OR n.content LIKE ? OR n.author LIKE ? OR t.tag LIKE ?
            ORDER BY n.modified_at DESC
            """;
        List<TainotePreview> notes = new ArrayList<>();
        String q = "%" + query + "%";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, q);
            pstmt.setString(2, q);
            pstmt.setString(3, q);
            pstmt.setString(4, q);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                notes.add(new TainotePreview(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("modified_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public List<TainotePreview> searchByTitle(String query) {
        if (conn == null) return new ArrayList<>();
        String sql = "SELECT id, title, author, modified_at FROM notes WHERE title LIKE ? ORDER BY modified_at DESC";
        return searchSingle(sql, query);
    }

    public List<TainotePreview> searchByContent(String query) {
        if (conn == null) return new ArrayList<>();
        String sql = "SELECT id, title, author, modified_at FROM notes WHERE content LIKE ? ORDER BY modified_at DESC";
        return searchSingle(sql, query);
    }

    public List<TainotePreview> searchByAuthor(String query) {
        if (conn == null) return new ArrayList<>();
        String sql = "SELECT id, title, author, modified_at FROM notes WHERE author LIKE ? ORDER BY modified_at DESC";
        return searchSingle(sql, query);
    }

    public List<TainotePreview> searchByTag(String query) {
        if (conn == null) return new ArrayList<>();
        String sql = """
            SELECT DISTINCT n.id, n.title, n.author, n.modified_at
            FROM notes n
            JOIN tags t ON n.id = t.note_id
            WHERE t.tag LIKE ?
            ORDER BY n.modified_at DESC
            """;
        return searchSingle(sql, query);
    }

    private List<TainotePreview> searchSingle(String sql, String query) {
        List<TainotePreview> notes = new ArrayList<>();
        String q = "%" + query + "%";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, q);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                notes.add(new TainotePreview(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("modified_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }
    public boolean noteExists(String id) {
        if (conn == null) return false;
        String sql = "SELECT 1 FROM notes WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void connect(){
        try{
            conn = DriverManager.getConnection(URL);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    private void initSchema(){
        if (conn == null) return;
        try(Statement statement = conn.createStatement()){
            statement.setQueryTimeout(30);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS notes(
                        id TEXT PRIMARY KEY,
                        title TEXT NOT NULL,
                        author TEXT NOT NULL,
                        status TEXT NOT NULL,
                        created_at TEXT NOT NULL,
                        modified_at TEXT NOT NULL,
                        content TEXT
                    );
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS tags (
                        note_id TEXT NOT NULL,
                        tag TEXT NOT NULL,
                        PRIMARY KEY (note_id, tag),
                        FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE
                    );
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS words (
                        note_id TEXT NOT NULL,
                        word TEXT NOT NULL,
                        frequency INTEGER NOT NULL DEFAULT 1,
                        PRIMARY KEY (note_id, word),
                        FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE
                    );
                    """);

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    /* ======================================================================
     * SIMPLE DEBUGS
     ========================================================================*/
    public void printNotesTable() {
        if (conn == null || !IS_DEBUG) return;
        String sql = "SELECT * FROM notes";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("\n=== NOTES ===");
            System.out.printf("%-36s | %-20s | %-15s | %-10s | %-15s | %-15s%n",
                    "ID", "TITLE", "AUTHOR", "STATUS", "CREATED", "MODIFIED");
            System.out.println("-".repeat(115));
            while (rs.next()) {
                System.out.println(
                        String.format("%-36s | %-20s | %-15s | %-10s | %-15s | %-15s | %s",
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("status"),
                        rs.getString("created_at"),
                        rs.getString("modified_at"),
                        rs.getString("content"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printTagsTable() {
        if (conn == null || !IS_DEBUG) return;
        String sql = "SELECT * FROM tags";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("\n=== TAGS ===");
            System.out.printf("%-36s | %-15s%n", "NOTE_ID", "TAG");
            System.out.println("-".repeat(55));
            while (rs.next()) {
                System.out.println(
                        String.format("%-36s | %-15s%n",
                        rs.getString("note_id"),
                        rs.getString("tag")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printWordsTable() {
        if (conn == null || !IS_DEBUG) return;
        String sql = "SELECT * FROM words";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("\n=== WORDS ===");
            System.out.printf("%-36s | %-20s | %-10s%n", "NOTE_ID", "WORD", "FREQUENCY");
            System.out.println("-".repeat(72));
            while (rs.next()) {
                System.out.println(
                        String.format("%-36s | %-20s | %-10d%n",
                        rs.getString("note_id"),
                        rs.getString("word"),
                        rs.getInt("frequency"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getAllNoteIds() {
        if (conn == null) return new HashSet<>();
        String sql = "SELECT id FROM notes";
        Set<String> ids = new HashSet<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) ids.add(rs.getString("id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}
