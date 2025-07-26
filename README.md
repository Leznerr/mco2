# Fatal Fantasy: Tactics

This project implements a turn-based tactics game in Java using a Swing based GUI. The code is organized using the MVC pattern.

## Building

A JDK 21 or newer is required. The project uses Maven to manage
dependencies and run the unit tests. To compile and execute the test
suite run:

```bash
mvn test
```

## Running

After compiling, run the application entry point:

```bash
java app.Main
```

## Project Structure

```
app/         - Main application entry point
controller/  - Controllers for all views and game logic
model/       - Core game models, battle system and utilities
persistence/ - Save and load services
view/        - Swing UI classes and assets
src/test/java  - JUnit test suite
```

The game code was refactored from an earlier CLI version and all features are accessed through the GUI.

## Persistence

All persistent data is stored in simple serialized files relative to the working
directory. Player and character information is written to `ff_tactics_save.dat`
while Hall of Fame standings are kept in `save/hall_of_fame.dat`. The save
directory is created automatically on first run.
