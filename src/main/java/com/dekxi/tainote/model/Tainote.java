package com.dekxi.tainote.model;

import java.util.*;

public record Tainote(
        String id,
        String title,
        String author,
        String status,
        String created,
        String modified,
        String content,
        List<String> tags
) {}