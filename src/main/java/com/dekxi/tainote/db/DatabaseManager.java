package com.dekxi.tainote.db;

import java.sql.*;
import java.util.*;

public class TainoteDB {
    private static final String URL = "jdbc:sqlite:tainote.db";
    private Connection conn;

    public TainoteDB(){
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
        if(!noteExists(id)) return;
        String sql = """
                DELETE FROM notes WHERE id = ?;
        """;
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
                created = ?,
                modified = ?,
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
}
