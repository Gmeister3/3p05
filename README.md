# Village War Strategy Game — COSC 3P91 Assignment 2

A console-based village war strategy game implemented in Java, fulfilling the requirements of COSC 3P91 Assignment 2. Players build and upgrade a village, train an army, and attack NPC opponents to earn resources and climb the leaderboard.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Package Structure](#package-structure)
3. [Compilation & Execution](#compilation--execution)
4. [Game Features](#game-features)
5. [OOP Concepts & Where They Appear](#oop-concepts--where-they-appear)
   - [a. Generics](#a-generics)
   - [b. Local and Anonymous Classes](#b-local-and-anonymous-classes)
   - [c. Lambda Expressions and Method References](#c-lambda-expressions-and-method-references)
   - [d. Custom Exceptions](#d-custom-exceptions)
   - [e. Java Utility Classes (Collections)](#e-java-utility-classes-collections)
   - [f. I/O and Streams](#f-io-and-streams)
6. [Design Decisions](#design-decisions)

---

## Project Overview

Each player manages a **Village** that contains buildings (farms, mines, defence towers, etc.) and inhabitants (fighters and workers). The **GameEngine** generates NPC opponent villages, coordinates the wall clock, and arbitrates combat results via the **Arbitrer**. The player can:

- Build new structures from seven building types
- Train military fighters (Soldier, Archer, Knight, Catapult) and civilian workers (GoldMiner, IronMiner, Lumberman)
- Upgrade buildings up to level 5 (constrained by VillageHall level)
- Explore a list of NPC villages sorted by defence difficulty
- Attack a chosen NPC village and loot resources on success
- Collect resources produced by workers and production buildings
- View the leaderboard ranked by player score

---

## Package Structure

```
src/
├── Main.java                         Game entry point and console loop
├── exceptions/
│   ├── BuildingLimitExceededException.java
│   ├── InsufficientResourcesException.java
│   ├── InvalidOperationException.java
│   └── MaxLevelReachedException.java
├── game/
│   ├── Army.java                     Player's attack force (List<Fighter>)
│   ├── CollectResources.java         Resource harvesting action
│   ├── Defence.java                  Aggregates ArcherTowers and Cannons
│   ├── GameEngine.java               Central controller; NPC generation; leaderboard
│   ├── Player.java                   Human player profile, score, Village & Army
│   ├── Repository.java               Generic keyed store Repository<T>
│   ├── Village.java                  Village state; build / train / upgrade logic
│   └── WallClock.java                Tick-based in-game timer
├── gameelements/
│   ├── Building.java                 Abstract base for all buildings (implements Updater)
│   ├── Habitant.java                 Abstract base for all inhabitants (implements Damager)
│   ├── Fighter.java                  Abstract military unit extending Habitant
│   ├── Peasant.java                  Abstract worker unit extending Habitant
│   ├── Damager.java                  Interface: damage() + newOperation()
│   ├── Updater.java                  Interface: upgrade()
│   ├── Resource.java                 Abstract base for Gold, Iron, Lumber
│   ├── Archer.java / Soldier.java / Knight.java / Catapult.java
│   ├── GoldMiner.java / IronMiner.java / Lumberman.java
│   ├── ArcherTower.java / Cannon.java / Farm.java
│   ├── GoldMine.java / IronMine.java / LumberMill.java
│   ├── VillageHall.java
│   ├── Gold.java / Iron.java / Lumber.java
├── gui/
│   └── GraphicalInterface.java       Console rendering (menus, village, army, targets)
└── utility/
    ├── Arbitrer.java                 Combat outcome calculation
    ├── AttackOutcome.java            Value object: success + loot fields
    ├── GameMap.java                  Map region with HashMap<String, Position>
    ├── Position.java                 2-D coordinate
    └── Region.java                   Rectangular map area
```

---

## Compilation & Execution

All commands are run from the repository root. No IDE is required.

### Compile

```bash
javac -d out $(find src -name "*.java")
```

Or using the provided `sources.txt`:

```bash
javac -d out @sources.txt
```

### Run

```bash
java -cp out Main
```

---

## Game Features

| Menu Option | Feature | Engine Requirement |
|---|---|---|
| 1 | View village status | — |
| 2 | **Build** a building | Deducts Gold/Iron/Lumber; enforces MAX_BUILDINGS cap |
| 3 | **Train** a unit (fighter or worker) | Deducts resources; fighters can join Army |
| 4 | **Upgrade** a building | Enforces VillageHall level cap and MAX_LEVEL |
| 5 | **Explore** NPC targets | Lists targets sorted by defence score |
| 6 | **Attack** a village | Arbitrer resolves outcome; loot credited on success |
| 7 | **Collect** resources | Workers + production buildings deposit resources |
| 8 | View ranking / leaderboard | Sorted by score descending |
| 9 | View army composition | Grouped counts and individual fighter details |
| 0 | Quit | — |

---

## OOP Concepts & Where They Appear

### a. Generics

**Type parameters and wildcards** are used throughout to achieve type-safe, reusable containers and utilities.

| Location | Usage |
|---|---|
| `game/Repository.java` | `Repository<T>` — parameterised key-value store; methods use `Predicate<T>` and `Consumer<T>` |
| `game/GameEngine.java` | `Repository<Player>` and `Repository<Village>` — typed repos for players and NPC villages |
| `game/Army.java` | `List<Fighter>` — typed list; `addAll(List<? extends Fighter>)` uses upper-bounded wildcard |
| `game/Defence.java` | `syncWithVillage(List<? extends Building>)` — wildcard accepts any Building subtype |
| `game/Village.java` | `List<Building>`, `List<Habitant>` — parameterised collections throughout |
| `java.util.Comparator` | `Comparator.comparingInt(Player::getScore)` / `comparingDouble(Fighter::damage)` |

### b. Local and Anonymous Classes

**Local classes** encapsulate temporary construction logic close to their single use site. **Anonymous classes** provide one-shot interface implementations.

| Location | Class | Purpose |
|---|---|---|
| `game/GameEngine.java` | `NpcBuilder` *(local class inside `generateNpcVillages`)* | Builds a randomly configured NPC Village |
| `game/Defence.java` | `BuildingMerger` *(local class inside `getAllDefenceBuildings`)* | Merges two typed building lists |
| `game/GameEngine.java` | Anonymous `Comparator<Village>` in `getAvailableTargets()` | Sorts NPC targets by defence score |
| `game/Village.java` | Anonymous `Comparator<Building>` in `getBuildingsSortedByLevel()` | Sorts buildings highest level first |
| `Main.java` | Anonymous `Runnable` in `printWelcomeBanner()` | Prints the ASCII welcome banner |

### c. Lambda Expressions and Method References

Lambdas and method references are used with the Stream API, `forEach`, `removeIf`, `Comparator.comparing*`, and functional interfaces.

| Location | Expression | Concept demonstrated |
|---|---|---|
| `game/Army.java` `recalculateDamage()` | `fighters.stream().mapToDouble(Fighter::damage).sum()` | Method reference with stream |
| `game/Army.java` `getSummary()` | `Collectors.groupingBy(f -> f.getClass().getSimpleName(), Collectors.counting())` | Lambda in stream collector |
| `game/Army.java` `attack()` | `fighters.removeIf(f -> rand.nextDouble() < 0.30)` | Lambda predicate |
| `game/CollectResources.java` | `peasants.stream().map(Peasant::work).collect(Collectors.toList())` | Method reference + stream |
| `game/CollectResources.java` | `buildings.stream().filter(b -> b instanceof GoldMine).mapToDouble(...)` | Lambda filter + mapToDouble |
| `game/Defence.java` | `buildings.stream().filter(...).map(...).forEach(archerTowers::add)` | Method reference forEach |
| `game/Defence.java` | `.reduce(0, Double::sum)` | Method reference in reduce |
| `game/GameEngine.java` | `.sorted(Comparator.comparingInt(Player::getScore).reversed())` | Method reference comparator |
| `game/Village.java` | `buildings.stream().mapToDouble(b -> b.getLevel() * b.getHitPoints()).sum()` | Lambda in defence score |
| `game/Village.java` | `.sorted(Comparator.comparingDouble(Fighter::damage).reversed())` | Method reference |
| `game/Village.java` | `buildings.forEach(b -> sb.append(...))` | Lambda forEach |
| `game/Repository.java` | `store.values().stream().filter(predicate).collect(...)` | Lambda predicate |
| `game/Repository.java` | `store.values().forEach(action)` | Consumer lambda |
| `gui/GraphicalInterface.java` | `village.getBuildings().stream().sorted(...).forEach(b -> ...)` | Lambda in rendering |
| `gui/GraphicalInterface.java` | `Collectors.groupingBy(h -> h.getClass().getSimpleName(), ...)` | Lambda grouping |

### d. Custom Exceptions

Four custom checked exceptions are defined in the `exceptions` package. Each carries structured context fields for meaningful error reporting. No empty catch blocks are used — all caught exceptions display an informative message to the player.

| Exception | When thrown | Fields |
|---|---|---|
| `InsufficientResourcesException` | Building / training / upgrading when Gold, Iron, or Lumber is short | `resourceType`, `required`, `available` |
| `BuildingLimitExceededException` | Adding a building when `Village.MAX_BUILDINGS` (20) is reached | `currentCount`, `maxAllowed` |
| `MaxLevelReachedException` | Upgrading a building already at level 5 (or at VillageHall cap) | `entityName`, `maxLevel` |
| `InvalidOperationException` | Attacking with an empty army; invalid army/village operations | `operationName` |

All four are caught and handled in `Main.handleMenuChoice()` — each prints an informative `[!]` message without crashing the game loop.

### e. Java Utility Classes (Collections)

| Class | Where used | Purpose |
|---|---|---|
| `ArrayList<T>` | `Village`, `Army`, `Defence`, `GameEngine`, `CollectResources` | Dynamic, ordered lists of buildings, fighters, resources |
| `TreeMap<K,V>` | `Repository<T>` (backing store) | Sorted key → entity mapping for players and villages |
| `HashMap<K,V>` | `GameMap` (`villageLocations`) | Village name → Position lookup |
| `Collections.unmodifiableList` | `Village.getBuildings()`, `Village.getHabitants()`, `Army.getFighters()` | Defensive copies to prevent external mutation |
| `Collections.unmodifiableMap` | `GameMap.getAllVillageLocations()` | Read-only map view |
| `Collections.sort` | `Village.getBuildingsSortedByLevel()` | Sorting with anonymous Comparator |
| `Comparator.comparing*` | `GameEngine.getLeaderboard()`, `Village.getFightersSortedByDamage()` | Typed field-based comparators |
| `Collectors.groupingBy` | `Army.getSummary()`, `GraphicalInterface` | Fighter/habitant counts by type |
| `Collectors.toList` | Many stream pipelines | Terminal collection operation |

### f. I/O and Streams

**Java I/O** is used for console interaction, and the **Stream API** is used to process collections with filtering, mapping, and aggregation.

| Location | Usage |
|---|---|
| `Main.java` | `new Scanner(new BufferedReader(new InputStreamReader(System.in)))` — stacked I/O streams for efficient line reading |
| `CollectResources.java` | `peasants.stream().map(Peasant::work).collect(Collectors.toList())` — stream maps worker output to resources |
| `CollectResources.java` | `buildings.stream().filter(...).mapToDouble(...).sum()` — stream aggregates production per building type |
| `Village.java` | `buildings.stream().mapToDouble(...).sum()` — stream computes defence score |
| `Village.java` | `habitants.stream().filter(...).map(...).collect(...)` — streams filter fighters/peasants from habitants |
| `GameEngine.java` | `playerRepository.getAll().stream().sorted(...).collect(Collectors.toList())` — stream produces sorted leaderboard |
| `Army.java` | `fighters.stream().mapToDouble(Fighter::damage).sum()` — stream aggregates army damage |
| `Army.java` | `fighters.stream().collect(Collectors.groupingBy(...))` — stream groups fighters for summary |
| `Defence.java` | `buildings.stream().filter(...).map(...).forEach(...)` — stream populates defence lists |
| `GraphicalInterface.java` | `village.getBuildings().stream().sorted(...).forEach(...)` — stream renders sorted buildings |
| `Repository.java` | `store.values().stream().filter(predicate).collect(...)` — generic stream filtering |

---

## Design Decisions

### Inheritance Hierarchy

- `Resource` → `Gold`, `Iron`, `Lumber` — a common base class allows the resource collection logic to handle all resource types uniformly.
- `Habitant` → `Fighter` → `{Soldier, Archer, Knight, Catapult}` — multi-level hierarchy; combat logic is shared at `Fighter` level while base habitant traits are at `Habitant`.
- `Habitant` → `Peasant` → `{GoldMiner, IronMiner, Lumberman}` — workers share production logic via the abstract `work()` method.
- `Building` → `{Farm, GoldMine, IronMine, LumberMill, ArcherTower, Cannon, VillageHall}` — upgrade cost and level logic is centralised; subclasses only override `applyUpgradeBonus()` and `getName()`.

### Interfaces

- `Damager` (damage + newOperation) is implemented by **both** buildings (ArcherTower, Cannon) and habitants (all Fighters and Peasants), enabling the engine to treat any entity as a damage source.
- `Updater` (upgrade) is implemented by `Building`, ensuring all building types conform to the same upgrade contract used by `Village.upgrade()`.

### VillageHall Level Cap

Non-VillageHall buildings cannot be upgraded beyond the current VillageHall level. This creates a meaningful progression dependency: to improve defences and production, the player must first invest in the VillageHall.

### Generic Repository

`Repository<T>` wraps a `TreeMap<String, T>` and exposes `findWhere(Predicate<T>)` and `forEach(Consumer<T>)` methods. This design supports both `Repository<Player>` and `Repository<Village>` without code duplication, and keeps entries sorted by key for deterministic iteration.

### Combat Resolution

`Arbitrer.judgeAttack()` compares the attacker's aggregate attack score against the defender's defence score with a random variance in \[0, 50\]. Loot is proportional to the victory margin, capped at 30 % of the defender's resources. On failure, the attacking army loses ~30 % of its fighters (simulated casualties).

### Separation of Concerns

| Responsibility | Class |
|---|---|
| Game state & rules | `GameEngine`, `Village`, `Army` |
| Combat resolution | `Arbitrer` |
| Resource production | `CollectResources`, `Peasant` subclasses |
| Score aggregation | `Village.getDefenceScore/getAttackScore`, `Army.getAttackScore` |
| Console rendering | `GraphicalInterface` |
| Persistence | `Repository<T>` |
| Spatial data | `GameMap`, `Region`, `Position` |
