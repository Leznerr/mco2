# Fatal Fantasy: Tactics

This project is a simple Java-based tactics game used for demonstrations.

## Building

This project now uses **Maven** for compilation and testing. Ensure Maven and JDK&nbsp;21+ are installed then run:

```bash
mvn package
```

This command compiles the sources under `src/main/java` and executes any JUnit tests.

## Running

There is currently no packaged executable, but unit tests can be run separately:

```bash
mvn test
```

See `src/test/java` for example tests.

# Fatal Fantasy: Tactics — MCO2
**CCPROG3 | Group 17 | Member B (Core Logic, Controllers, Persistence)**

---

## 1. Project Overview

This project is the **MCO2 (GUI) phase** of the two-part Fatal Fantasy: Tactics tactical RPG.  
It evolves our MCO1 code (text-based CLI) into a fully **GUI-driven Java application** using the MVC pattern, persistent storage, and additional advanced features.

- **MCO1:** CLI, OOP, core battle logic, no GUI, no file saving
- **MCO2:** Modern GUI (JavaFX/Swing), MVC, persistence, inventory, Hall of Fame, AI, leveling, trading, and more.

---

## 2. Core Requirements

- **Strict MVC pattern**: Each package (model, view, controller) must be cleanly separated.
- **All logic in controllers/models, never in views**
- **GUI:** All user interaction through JavaFX or Swing views.
- **No CLI remains.**
- **Persistence:** All game/player/character/inventory data is saved and loaded from file (see `persistence/`).
- **Inventory & Magic Item system:**  
  - Each character owns an inventory (can equip 1 magic item).
  - Magic items: `SINGLE_USE` (free action) or `PASSIVE` (auto effect).
  - New item awarded every 3rd cumulative win.
- **Race selection** during character creation (before class selection).
- **BONUS:**  
  - Hall of Fame screen (persistent)  
  - Player vs. Bot (AI opponent)  
  - Auto Character Generator  
  - Status Effects (poison, stun, immunity, etc.)  
  - Leveling system (XP, unlocks)  
  - At least 4 races and 4 classes with unique mechanics  
  - Magic Item Trading (GUI)  
  - Robust Git practices (branching, commits, PRs, merges)

---

## 3. Team Roles

- **Member A:** All GUI (`view/`, `app/`), Main class, UI event handling
- **Member B:** All logic, data, controller, persistence, util, and test packages  
  (This repo is for Member B's responsibilities.)

---

## 4. Architecture & File Structure

src/
├── app/
│ └── Main.java
├── controller/
│ ├── GameManagerController.java
│ ├── CharacterController.java
│ ├── BattleController.java
│ ├── SceneManager.java
│ └── HallOfFameController.java
├── model/
│ ├── core/
│ │ ├── Player.java
│ │ └── Character.java
│ ├── item/
│ │ ├── Inventory.java
│ │ └── MagicItem.java
│ └── battle/
│ ├── Battle.java
│ └── CombatLog.java
├── view/
│ ├── mainmenu/
│ │ └── MainMenuView.java
│ ├── character/
│ │ └── CharacterCreationView.java
│ ├── battle/
│ │ └── BattleView.java
│ └── HallOfFameManagementView.java
├── persistence/
│ ├── GameData.java
│ └── SaveLoadService.java
├── util/
│ ├── Constants.java
│ ├── InputValidator.java
│ └── GameException.java
└── test/



---

## 5. Workflow & Git Best Practices

- **Work only in the original MCO1 repo/directory.**
- Create a `develop` branch from the final MCO1 main branch.
- Refactor into package structure (see above), commit as a *single* "refactor" commit.
- All new MCO2 code and features go into this structure.
- Use feature branches, pull requests, descriptive commit messages.
- Always pull and merge changes; avoid force pushes and main branch work.

---

## 6. Key Behaviors & User Flows

**Main Menu:** (MainMenuView)  
1. **Register Players:**  
    - Opens PlayerRegistrationView.  
    - Requires two unique, non-empty player names (validated via `InputValidator`).  
    - Persists players using `SaveLoadService`.  
    - Shows success/error dialogs appropriately.
2. **Manage Characters:**  
    - Only enabled if players exist.  
    - Opens character management view.
3. **Hall of Fame:**  
    - Opens HallOfFameManagementView, loading data from file.
4. **Start Battle:**  
    - (To be implemented.)
5. **Exit:**  
    - Triggers save and closes app.

**Other views:**  
- Race and class selection, character creation, inventory/trading screens, and battle all follow MVC.
- All file saving/loading handled via `persistence/SaveLoadService.java`.
- All user input validated for correctness and uniqueness.
- No logic leaks into view classes.

---

## 7. Error Handling

- **InputValidator** handles:  
  - Null/empty names  
  - Duplicate player names  
  - Disallowed characters (if any)  
- **GameException** wraps all file I/O and persistence errors for safe display.

---

## 8. Persistence Layer

- **`GameData.java`**: Single DTO aggregating all players and Hall of Fame.
- **`SaveLoadService.java`**: Handles reading/writing all persistent data to disk.
    - `saveGame(GameData)`
    - `loadGame()`
    - `saveHallOfFame(List<HallOfFameEntry>)`
    - `loadHallOfFame()`
    - All data serialized via Java I/O.
    - All methods throw `GameException` on failure.

---

## 9. Common Bugs & Debugging

- **Duplicate windows**:  
  - *Always* use one JFrame (in SceneManager).  
  - Never instantiate new JFrames in controllers or views.
- **Persistence errors**:  
  - Check for proper serialization of all model classes.  
  - Ensure file paths are correct and readable/writable.
- **NullPointerExceptions**:  
  - Initialize controllers and views before usage.
  - Don’t pass nulls into controller constructors.

---

## 10. How to Use This Repo in Codex

- Codex uses this README for all context.
- Codex tasks:  
  - Refactor code to match structure  
  - Implement/integrate any listed feature  
  - Add/fix persistence, error handling  
  - Add/modify JUnit tests  
  - Debug errors with code and stack trace
- When in doubt, **re-read this README before asking Codex for a new task**.

---

## 11. Project References

- [MCO1 Final Codebase]  
- [MCO2 Specs PDF/Docx]  
- [CCPROG3 Rubrics & Slides]  
- [Official Java API docs]

---

## 12. Changelog and Progress

- **Main Menu to Player Registration**: Clicking "Register Players" shows the PlayerRegistrationView. Two players must be registered (unique, not empty). Data is saved, and confirmation is shown. Fields are reset after registration.
- **Player data persistence**: Uses SaveLoadService and GameData.
- **Manage Characters**: Enabled after registration.
- **Hall of Fame**: Loads from file.  
- **Ongoing**: Integration of inventory, trading, AI, and battle screens.

---

## 13. Troubleshooting Steps

- If duplicate windows open:  
  - Check that only SceneManager creates/shows JFrame.
- If registration fails to persist:  
  - Check SaveLoadService and serialization of Player class.
- If NullPointerException on controller/view:  
  - Make sure you pass real, fully constructed views/controllers.
- For any new features:  
  - Update this README as needed.

---

**All contributors, Codex included:  
Follow this README strictly. All requirements, technical principles, and architectural decisions here are mandatory for this project.**

---

---

### **How this helps Codex and new devs**

- All context (requirements, structure, rules, workflow) is preserved
- Codex will know the project history, design, and active issues
- You can ask Codex for anything ("Implement Hall of Fame sorting", "Debug SaveLoadService", "Generate tests for PlayerRegistration") and it will have full context

---

**Let me know if you want this personalized further (e.g., more team details, extra technical constraints, etc.)!**


# Fatal Fantasy: Tactics (MCO2 Project)

## 🏰 Project Overview

Fatal Fantasy: Tactics is a turn-based tactical RPG being developed for CCPROG3 MCO2.  
- **MCO1:** Text-based CLI version (core logic complete)
- **MCO2:** Transition to Java GUI (JavaFX or Swing) with MVC architecture, inventory system, persistent save/load, more features.

## 🛠️ Architecture & File Structure

- **src/**
  - **app/**: Main.java
  - **controller/**: SceneManager, GameManagerController, HallOfFameController, etc.
  - **model/**
    - **core/**: Player, Character, etc.
    - **item/**: MagicItem, Inventory
    - **battle/**: Battle, CombatLog
  - **view/**: All GUI screens (MainMenuView, PlayerRegistrationView, etc.)
  - **persistence/**: SaveLoadService, GameData
  - **util/**: Constants, InputValidator, GameException

## 🧑‍💻 Roles

- **Member A:** All GUI/view classes, Main.java
- **Member B (me):** Core logic (model, controller, persistence, util), game logic, file save/load, AI, unit tests, Git management

## 🚦 Project Status & Main Workflows

- Refactored from CLI to package-based MVC
- Single JFrame (no duplicate windows!), navigation handled by `SceneManager`
- Data persistence via `SaveLoadService`/`GameData`
- Input validation handled via `InputValidator` (ensures non-empty, unique player names, etc.)
- Current issue: [Describe any current bug here, e.g. duplicate windows, registration logic, etc.]

## ✅ Key Requirements & Specs

- **GUI only** (no CLI): JavaFX or Swing
- **MVC** enforced
- **Player Registration:** Two unique names required, error handling for empty/duplicate names, confirmation shown
- **Persistence:** Players and Hall of Fame saved to file, reloaded at startup
- **Magic Item Inventory:** One active item per character, inventory managed per player
- **Extensible:** Leveling system, status effects, AI, Hall of Fame screen
- **No forbidden Java patterns:** No global vars, no `System.exit()`, etc.

## 💾 Saving & Loading

- All players stored in `GameData` (see `persistence/GameData.java`)
- Use `SaveLoadService.saveGame()` and `SaveLoadService.loadGame()` for file operations
- Hall of Fame via `saveHallOfFame()` and `loadHallOfFame()`

## ⚡ Codex Usage

- **How to get started:**  
  1. Clone this repo or connect your GitHub to Codex
  2. Create a new Codex environment with this repo
  3. Use the "Describe a task" box to ask for:  
      - Bug fixes (e.g., "Fix duplicate windows on register")  
      - New features (e.g., "Implement inventory trading GUI")  
      - Refactors (e.g., "Refactor registration logic to prevent blank player names")  
      - Unit test generation (e.g., "Generate JUnit tests for Player class")
  4. Codex only "sees" the code and documentation—**so keep this README up to date with all context and rules**

- **Tips:**  
  - Write *clear*, *complete* prompts (e.g., "Enforce unique player names on registration and show an error dialog if duplicate")
  - When possible, reference file/class names
  - Keep all game rules and architectural constraints documented here for Codex and all collaborators

## 📝 Notes / Context (From ChatGPT tutoring)

- Member B is responsible for all code in model, controller, persistence, util
- We have discussed:  
  - MVC patterns  
  - Avoiding duplicate JFrames  
  - Proper error handling  
  - File-based persistence  
  - All class relationships must be OOP-correct (no anti-patterns)
- **For a full project summary, review ChatGPT conversation transcript!**

## 🗂️ Future To-Do

- [ ] Fix any outstanding bugs (see Issues or notes above)
- [ ] Implement remaining bonus features (leveling, auto character generator, etc.)
- [ ] Write/Update unit tests for new logic

---

> This README is designed to help Codex, AI agents, and all future developers understand every detail and expectation for this project!

---

**You can add, edit, or expand as you wish—this is just a complete starting point!**  
If you want, I can auto-generate an even more detailed section for a specific feature or bug, just tell me what you want Codex to know!
