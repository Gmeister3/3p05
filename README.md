# Village War Strategy Game — COSC 3P91 Assignment 3

A console-based village war strategy game implemented in Java, fulfilling the requirements of COSC 3P91 Assignment 3. This assignment builds on Assignment 2 by redesigning the codebase to incorporate three key Design Patterns: **MVC**, **Factory**, and **Adapter** — plus optional XML persistence.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Package Structure](#package-structure)
3. [Compilation & Execution](#compilation--execution)
4. [Game Features](#game-features)
5. [Design Patterns Applied (Assignment 3)](#design-patterns-applied-assignment-3)
   - [i. MVC Architectural Pattern](#i-mvc-architectural-pattern)
   - [ii. Factory Creational Pattern](#ii-factory-creational-pattern)
   - [iii. Adapter Structural Pattern](#iii-adapter-structural-pattern)
   - [iv. XML Persistence (Bonus)](#iv-xml-persistence-bonus)
6. [OOP Concepts & Where They Appear](#oop-concepts--where-they-appear)
   - [a. Generics](#a-generics)
   - [b. Local and Anonymous Classes](#b-local-and-anonymous-classes)
   - [c. Lambda Expressions and Method References](#c-lambda-expressions-and-method-references)
   - [d. Custom Exceptions](#d-custom-exceptions)
   - [e. Java Utility Classes (Collections)](#e-java-utility-classes-collections)
   - [f. I/O and Streams](#f-io-and-streams)
7. [Design Decisions](#design-decisions)

---

## Project Overview

Each player manages a **Village** that contains buildings (farms, mines, defence towers, etc.) and inhabitants (fighters and workers). The **GameEngine** generates NPC opponent villages, coordinates the wall clock, and arbitrates combat results via a **ChallengeDecisionAdapter** that bridges the game's model to the provided external `ChallengeDecision` engine. The player can:

- Build new structures from seven building types
- Train military fighters (Soldier, Archer, Knight, Catapult) and civilian workers (GoldMiner, IronMiner, Lumberman)
- Upgrade buildings up to level 5 (constrained by VillageHall level)
- Explore a list of NPC villages sorted by defence difficulty
- Attack a chosen NPC village and loot resources on success
- Collect resources produced by workers and production buildings
- View the leaderboard ranked by player score
- **Save** the village state to `village_save.xml` (menu key `s`)
- **Load** the village state from `village_save.xml` (menu key `l`)

---

## Package Structure

```
src/
├── Main.java                         MVC wiring: creates Model, View, Controller, runs game loop
├── ChallengeDecision/                Provided external combat-resolution engine (DO NOT MODIFY)
│   ├── Arbitrer.java
│   ├── ChallengeAttack.java
│   ├── ChallengeDefense.java
│   ├── ChallengeEntity.java
│   ├── ChallengeEntitySet.java
│   ├── ChallengeResource.java
│   ├── ChallengeResult.java
│   └── FightResult.java
├── controller/                       MVC – Controller layer
│   └── GameController.java           Handles user input; coordinates Model ↔ View
├── factory/                          Creational Design Patterns
│   ├── BuildingFactory.java          Factory for Building objects
│   └── HabitantFactory.java          Factory for Habitant objects
├── persistence/                      XML persistence (bonus)
│   └── VillageSerializer.java        Save/load Village state to/from XML
├── exceptions/
│   ├── BuildingLimitExceededException.java
│   ├── InsufficientResourcesException.java
│   ├── InvalidOperationException.java
│   └── MaxLevelReachedException.java
├── game/                             MVC – Model layer (game state)
│   ├── Army.java
│   ├── CollectResources.java
│   ├── Defence.java
│   ├── GameEngine.java
│   ├── Player.java
│   ├── Repository.java
│   ├── Village.java
│   └── WallClock.java
├── gameelements/                     MVC – Model layer (game entities)
│   ├── Building.java / Fighter.java / Habitant.java / Peasant.java
│   ├── Damager.java / Updater.java
│   ├── Resource.java / Gold.java / Iron.java / Lumber.java
│   ├── Archer.java / Soldier.java / Knight.java / Catapult.java
│   ├── GoldMiner.java / IronMiner.java / Lumberman.java
│   ├── ArcherTower.java / Cannon.java / Farm.java
│   ├── GoldMine.java / IronMine.java / LumberMill.java
│   └── VillageHall.java
├── gui/                              MVC – View layer
│   └── GraphicalInterface.java       Console rendering (menus, village, army, targets)
└── utility/
    ├── Arbitrer.java                 Legacy combat helper (kept for reference)
    ├── AttackOutcome.java            Value object: success + loot fields
    ├── ChallengeDecisionAdapter.java Adapter bridging Army/Village → ChallengeDecision API
    ├── GameMap.java
    ├── Position.java
    └── Region.java
village_schema.xsd                    XSD schema for village save files
```

---

## Compilation & Execution

All commands are run from the repository root. No IDE is required.

### Compile

```bash
javac -d out $(find src -name "*.java")
```

Or using `sources.txt`:

```bash
javac -d out @sources.txt
```

### Run

```bash
java -cp out Main
```

---

## Game Features

| Menu Option | Feature |
|---|---|
| 1 | View village status |
| 2 | **Build** a building (via BuildingFactory) |
| 3 | **Train** a unit (via HabitantFactory) |
| 4 | **Upgrade** a building |
| 5 | **Explore** NPC targets |
| 6 | **Attack** a village (via ChallengeDecisionAdapter → external Arbitrer) |
| 7 | **Collect** resources |
| 8 | View ranking / leaderboard |
| 9 | View army composition |
| s | **Save** village to XML |
| l | **Load** village from XML |
| 0 | Quit |

---

## Design Patterns Applied (Assignment 3)

### i. MVC Architectural Pattern

The codebase is restructured into three clearly separated layers:

| Layer | Classes | Responsibility |
|---|---|---|
| **Model** | `game/`, `gameelements/`, `exceptions/`, `utility/` | Game state and rules; no I/O |
| **View** | `gui/GraphicalInterface` | All console rendering; no logic |
| **Controller** | `controller/GameController` | Processes user input; calls Model API; instructs View to render |
| **Wiring** | `Main` | Creates M, V, C; runs the event loop |

`Main.java` is now a thin bootstrapper: it creates the `GameEngine` (Model), `GraphicalInterface` (View), `Player` (Model), and `GameController` (Controller), then enters the loop. The loop reads a character from the console and delegates immediately to `GameController.handleMenuChoice()`.

`GameController` is the sole owner of input-handling logic. It calls methods on the Model (`Village`, `Army`, `GameEngine`) and delegates all display work to `GraphicalInterface`.

`GraphicalInterface` (View) has no knowledge of game rules; it only formats and prints data supplied by the Controller.

### ii. Factory Creational Pattern

Two factory classes handle all object instantiation for game entities:

| Factory | Client | Products |
|---|---|---|
| `factory.BuildingFactory` | `controller.GameController.buildBuilding()` | `Farm`, `GoldMine`, `IronMine`, `LumberMill`, `ArcherTower`, `Cannon`, `VillageHall` |
| `factory.HabitantFactory` | `controller.GameController.trainUnit()` | `Soldier`, `Archer`, `Knight`, `Catapult`, `GoldMiner`, `IronMiner`, `Lumberman` |

Both factories expose a `create(String type)` method plus cost-query helpers (`getGoldCost`, `getIronCost`, `getLumberCost`). The Controller passes a type constant to the factory rather than using `new` directly, so adding a new building or unit type only requires updating the factory.

### iii. Adapter Structural Pattern

The provided `ChallengeDecision` package defines its own API (`ChallengeEntitySet`, `ChallengeAttack`, `ChallengeDefense`, `ChallengeResource`, `ChallengeResult`) and must not be modified. The game's existing Model uses `Army` and `Village`. These two APIs are incompatible; the **Object Adapter** `utility.ChallengeDecisionAdapter` bridges them:

```
Army / Village  ──►  ChallengeDecisionAdapter  ──►  ChallengeDecision.Arbitrer
(game API)           (adapter)                       (external API – unmodified)
     ◄── AttackOutcome ──────────────────────────────────────────────────────
```

- **Adaptee**: `ChallengeDecision.Arbitrer.challengeDecide()` — external, unmodified.
- **Target**: `AttackOutcome` — what `Army.attack()` expects back.
- **Adapter**: `ChallengeDecisionAdapter.adapt(Army, Village)` — converts each `Fighter` to a `ChallengeAttack`, each `Building` to a `ChallengeDefense`, and resources to `ChallengeResource`; calls the external arbitrer; converts the `ChallengeResult` back to `AttackOutcome`.
- **Client**: `game.Army.attack()` — replaced the old hand-rolled `utility.Arbitrer` with a call to the adapter.

### iv. XML Persistence (Bonus)

`persistence.VillageSerializer` provides two static utility methods:

- `save(Village, String filePath)` — serialises village name, resources, buildings (type, level, hitPoints), and habitants (type) to a well-formed XML file using Java's built-in `javax.xml` APIs.
- `load(String filePath)` — parses the XML file and reconstructs a `Village` using `BuildingFactory`-equivalent logic. XXE injection is prevented by disabling DOCTYPE declarations in the parser.

The companion file `village_schema.xsd` defines the XML Schema that all save files must conform to. The schema constrains valid building types, habitant types, resource values, and building level ranges.

---

## OOP Concepts & Where They Appear

### a. Generics

| Location | Usage |
|---|---|
| `game/Repository.java` | `Repository<T>` — parameterised key-value store |
| `game/GameEngine.java` | `Repository<Player>`, `Repository<Village>` |
| `game/Army.java` | `List<Fighter>`, `addAll(List<? extends Fighter>)` |
| `game/Defence.java` | `syncWithVillage(List<? extends Building>)` |
| `ChallengeDecision/*` | `ChallengeAttack<T,V>`, `ChallengeDefense<T,V>`, etc. |
| `utility/ChallengeDecisionAdapter` | Uses `ChallengeEntitySet<Double,Double>` |

### b. Local and Anonymous Classes

| Location | Class | Purpose |
|---|---|---|
| `game/GameEngine.java` | `NpcBuilder` (local) | Builds a randomly configured NPC Village |
| `game/Defence.java` | `BuildingMerger` (local) | Merges two typed building lists |
| `game/GameEngine.java` | Anonymous `Comparator<Village>` | Sorts NPC targets by defence score |
| `game/Village.java` | Anonymous `Comparator<Building>` | Sorts buildings highest level first |
| `Main.java` | Anonymous `Runnable` | Prints the ASCII welcome banner |

### c. Lambda Expressions and Method References

(See Assignment 2 README for full table — unchanged in Assignment 3.)

### d. Custom Exceptions

Four custom checked exceptions in the `exceptions` package: `InsufficientResourcesException`, `BuildingLimitExceededException`, `MaxLevelReachedException`, `InvalidOperationException`. All are caught in the `Main` game loop and in `GameController`.

### e. Java Utility Classes (Collections)

`ArrayList`, `TreeMap`, `HashMap`, `Collections.unmodifiableList`, `Comparator.comparing*`, `Collectors.groupingBy`, `Collectors.toList` — see Assignment 2 README for details.

### f. I/O and Streams

- Console I/O: `Scanner(new BufferedReader(new InputStreamReader(System.in)))` in `Main`.
- XML I/O: `DocumentBuilder` / `Transformer` in `persistence.VillageSerializer`.
- Stream API: used extensively in `Village`, `Army`, `GameEngine`, `CollectResources`, `GraphicalInterface`, `Repository` (see Assignment 2 README for full table).

---

## Design Decisions

### MVC Separation

Moving all menu-handling logic from `Main` into `GameController` was the primary structural change. `Main` now plays the role of the MVC "bootstrapper" (sometimes called the Application or Composition Root). The `GraphicalInterface` (View) gained `renderBuildMenu()`, `renderTrainMenu()`, and `renderUpgradeMenu()` methods so that even menu display is delegated to the View — removing the last rendering responsibility from what was formerly a mixed-concern `Main`.

### Adapter vs. Replacing the Old Arbitrer

The old `utility.Arbitrer` is retained for reference but is no longer called by `Army.attack()`. The `ChallengeDecisionAdapter` now sits between the game model and the external engine. Using an Object Adapter (composition) rather than a Class Adapter (inheritance) keeps the design flexible and avoids polluting the class hierarchy.

### Factory vs. Abstract Factory

A plain **Factory** (one factory per product family) was chosen over Abstract Factory because there is currently a single platform/theme for buildings and habitants. If a future requirement introduces multiple building themes (e.g., "stone age" vs. "industrial"), the factories could be promoted to an Abstract Factory hierarchy with minimal impact on the Controller.

### XML Schema

The schema (`village_schema.xsd`) uses enumerations to constrain building and habitant types, ensuring that only known types are persisted and re-instantiated. This prevents load-time errors from corrupted or hand-edited save files.

### XXE Security

`VillageSerializer.load()` explicitly disables DOCTYPE declarations and external entity processing on the `DocumentBuilderFactory` before parsing, guarding against XML External Entity (XXE) injection attacks.
