# Assignment 3 Verification Report
**Course:** COSC 3P91 | **Due:** March 26, 2026

---

## 1. Compilation & Execution ✅

```bash
# Compile
javac -d out $(find src -name "*.java")
# → Succeeds (no errors; only benign unchecked-cast notes from generic wildcards)

# Run
java -cp out Main
```

The game runs end-to-end from the command line with no IDE required.

---

## 2. Requirements Checklist

### (i) MVC Architectural Pattern — 25 marks ✅

| Layer | Classes | Notes |
|---|---|---|
| **Model** | `game/`, `gameelements/`, `exceptions/`, `utility/` | Pure state + rules, zero I/O |
| **View** | `gui/GraphicalInterface` | Only prints; no game logic |
| **Controller** | `controller/GameController` | Reads input, calls Model API, delegates display to View |
| **Wiring** | `Main` | Thin bootstrapper; creates M/V/C then enters event loop |

`Main.java` is a clean MVC composition root. `GameController.handleMenuChoice()` is the sole owner of all user-input dispatch logic. `GraphicalInterface` has no knowledge of game rules.

---

### (ii) Factory Creational Pattern — 20 marks ✅

| Factory | Client | Products |
|---|---|---|
| `factory.BuildingFactory` | `GameController.buildBuilding()` | Farm, GoldMine, IronMine, LumberMill, ArcherTower, Cannon, VillageHall |
| `factory.HabitantFactory` | `GameController.trainUnit()` | Soldier, Archer, Knight, Catapult, GoldMiner, IronMiner, Lumberman |

Both factories expose `create(String type)` plus cost-query helpers. The controller never calls `new` on a concrete building or habitant.

---

### (iii) Adapter Structural Pattern — 25 marks ✅

```
Army / Village  ──►  ChallengeDecisionAdapter  ──►  ChallengeDecision.Arbitrer
(game API)           (adapter — Object Adapter)      (external API — UNMODIFIED)
     ◄── AttackOutcome ────────────────────────────────────────────────────────
```

- **Adaptee:** `ChallengeDecision.Arbitrer.challengeDecide()` — provided, not modified.
- **Adapter:** `utility.ChallengeDecisionAdapter.adapt(Army, Village)` — translates each `Fighter` → `ChallengeAttack`, each `Building` → `ChallengeDefense`, resources → `ChallengeResource`; calls the external engine; converts `ChallengeResult` → `AttackOutcome`.
- **Client:** `game.Army.attack()` — calls the adapter instead of the old hand-rolled `utility.Arbitrer`.

---

### (iv) XML Persistence (Bonus +15) ✅

| Feature | Class/File | Status |
|---|---|---|
| Save village | `persistence.VillageSerializer.save()` | ✅ |
| Load village | `persistence.VillageSerializer.load()` | ✅ |
| XML Schema | `village_schema.xsd` | ✅ |
| XXE prevention | DocType declarations disabled in parser | ✅ |

Menu key `s` saves `village_save.xml`; menu key `l` loads it and live-reloads the player village.

---

### (v) Game Functionality — 5 marks ✅

| Feature | Menu Key | Status |
|---|---|---|
| View village status | 1 | ✅ |
| Build new building | 2 | ✅ |
| Train unit (fighter/worker) | 3 | ✅ |
| Upgrade building | 4 | ✅ |
| Explore NPC targets | 5 | ✅ |
| Attack a village | 6 | ✅ |
| Collect resources | 7 | ✅ |
| View ranking / leaderboard | 8 | ✅ |
| View army | 9 | ✅ |
| Save / Load XML | s / l | ✅ |
| Quit | 0 | ✅ |

---

### (vi) Object-Oriented Design — 10 marks ✅

| Concept | Where |
|---|---|
| **Inheritance** | `Building` → `Farm`, `GoldMine`, …; `Fighter` → `Soldier`, `Archer`, …; `Resource` → `Gold`, `Iron`, `Lumber` |
| **Interfaces** | `Damager` (damage + newOperation), `Updater` (upgrade) |
| **Encapsulation** | Resources expose `add/subtract/hasEnough`; `Village.getBuildings()` returns unmodifiable list |
| **Generics** | `Repository<T>` parameterised key-value store; `Army.addAll(List<? extends Fighter>)`; `Defence.syncWithVillage(List<? extends Building>)` |
| **Custom Exceptions** | `InsufficientResourcesException`, `BuildingLimitExceededException`, `MaxLevelReachedException`, `InvalidOperationException` |
| **Anonymous classes** | `Comparator<Village>` in `GameEngine.getAvailableTargets()`, `Comparator<Building>` in `Village.getBuildingsSortedByLevel()`, `Runnable` banner in `Main` |
| **Local classes** | `NpcBuilder` in `GameEngine.generateNpcVillages()`, `BuildingMerger` in `Defence.getAllDefenceBuildings()` |
| **Lambdas / method refs** | Stream pipelines throughout `Village`, `Army`, `GameEngine`, `GraphicalInterface`, `Repository` |
| **Collections** | `ArrayList`, `TreeMap`, `HashMap`, `Collections.unmodifiableList`, `Collectors.groupingBy`, `Collectors.toList` |

---

### (vii) Code Quality & Documentation — 10 marks ✅

- All classes have Javadoc-style header comments.
- All public and protected methods have `@param` / `@return` / `@throws` Javadoc tags where applicable.
- Pattern roles are called out explicitly in each class's Javadoc (`<b>Pattern: …</b>`).
- Inline comments explain non-obvious logic (scoring formulas, stream pipelines, anonymous class usage).

---

### (viii) Description Document — 5 marks ⚠️

The **README.md** contains a complete, detailed description of all design decisions, pattern applications, and OOP concept usage. **However**, the assignment submission requires a PDF description compiled from the provided LaTeX template (`latex/manuscript.tex`). The existing `manuscript.tex` still references Assignment 2 in its title and abstract and has not been updated to describe the Assignment 3 patterns (MVC, Factory, Adapter, XML persistence). **This should be updated before submission.**

---

## 3. Summary

| Component | Marks Available | Status |
|---|---|---|
| MVC Architectural Pattern | 25 | ✅ Full implementation |
| Factory/Abstract Factory | 20 | ✅ Two factories (Building + Habitant) |
| Adapter Pattern | 25 | ✅ Object Adapter over external ChallengeDecision API |
| Game Functionality | 5 | ✅ All 6 required features work |
| OOP Design | 10 | ✅ All required concepts demonstrated |
| Code Quality & Docs | 10 | ✅ Compiles, runs, Javadoc comments present |
| Description Document | 5 | ⚠️ README is comprehensive but LaTeX PDF needs updating |
| **Subtotal** | **100** | |
| Bonus: XML Persistence | +15 | ✅ Save/load + XSD schema |

**The codebase fully satisfies the Assignment 3 technical requirements and compiles/runs correctly. The only gap before submission is updating the LaTeX description document (`latex/manuscript.tex`) to reference Assignment 3.**
