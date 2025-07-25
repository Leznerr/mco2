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
