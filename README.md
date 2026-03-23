# Tainote

A lightweight, distraction-free note-taking desktop app for writers.

![Tainote Screenshot](./assets/images/Screenshot_v1.png)

---

## Features

### Writing
- **Distraction-free editor** — Clean, minimal UI that stays out of your way
- **Font & style controls** — Font family, size, and color picker in the toolbar
- **Author & status tagging** — Attach metadata to every note

### Stats
- **Document stats** — Live word count, character count, and unique word count
- **Session stats** — WPM, session start time, elapsed time, and minutes active

### Data
- **Persistent local database** — Notes are saved and indexed via SQLite
- **Note syncing** — Startup reconciliation between local files and the database
- **Export & import** — Move your notes in and out of the app

---

## In the Works

### Editor
- **Tabbing** — Open multiple notes simultaneously
- **Find & replace** — Search and replace within the editor
- **Key configuration** — Rebind shortcuts to your preference

### Stats & Visualizer
- **Overall statistics** — Accumulated word count, average WPM, total time, session count, longest session
- **Visualizer** — Daily, weekly, and monthly writing stats

### Organization
- **Registered tags** — Organize and browse notes by tag
- **Filtering** — Filter the notes list by tag, status, or author

### Theming
- **Themes** — Light, Chaos, and Sakura themes

---

## Getting Started

### Prerequisites
- Java 21+

### Running from source

```bash
git clone https://github.com/Relay-RBX/tainote.git
cd tainote
mvn javafx:run
```

---

## Built With
- Java 21
- JavaFX
- SQLite (local database)
---

## Author
Made by dekxi
