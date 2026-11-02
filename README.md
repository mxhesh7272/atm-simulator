# Java ATM Simulator

An educational Core Java 21 ATM simulator with a modern navy-and-cyan Java Swing interface. It is designed for a B.Tech CSE project demonstration, not real-world banking.

## Features

- Customer registration and sign-in for savings and current accounts
- PBKDF2-HMAC-SHA-256 salted PIN hashes; PINs are never written in plaintext or shown to administrators
- Deposits, withdrawals, overdraft-aware current accounts, transfers, balance display, PIN changes, mini statements, and complete transaction histories
- Persistent local data with atomic file replacement, malformed-record tolerance, duplicate-account prevention, and persisted three-failure account lockout
- Administrator dashboard for safe account and transaction search (no PIN data)
- Logout, exit confirmation, validation messages, and all money handled as `BigDecimal`

## Java concepts demonstrated

Classes/objects, encapsulation, abstraction, inheritance (`SavingsAccount`/`CurrentAccount`), polymorphism through `BankAccount` and `AccountOperations`, an interface, constructors, overloaded constructors/operations, overriding, `this`/`super`, `static`/`final`, `ArrayList`, `HashMap`, `Iterator`, collections, custom exceptions, file I/O, packages, loops, conditionals, switch expressions, and arrays. The optional console exercise also uses `Scanner`, arrays, a loop, and `switch` without replacing the Swing application.

## Structure

```text
src/atm/Main.java                 entry point and optional console concept demo
src/atm/model/                    account hierarchy and immutable transactions
src/atm/service/                  banking rules, authentication, salted PIN security
src/atm/storage/FileManager.java  local atomic file persistence
src/atm/gui/                      Java Swing screens and shared theme
src/atm/test/CoreBankingTest.java dependency-free automated core tests
data/                             generated account, transaction, and login-attempt data
```

## Prerequisites and VS Code setup

Install a JDK 21 (not a JRE) and select it in VS Code's **Java: Configure Java Runtime** command. This workspace’s `.vscode/settings.json` is preconfigured for the detected Eclipse Adoptium JDK 21 path. If your JDK is elsewhere, update that one path or select your own JDK 21 in VS Code.

## Build and run

From the project root, with JDK 21 `javac` on `PATH`:

```powershell
javac -d out -sourcepath src src/atm/Main.java
java -cp out atm.Main
```

On this computer, where Java 8 is currently ahead of Java 21 on `PATH`, use:

```powershell
$jdk = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot\bin'
& "$jdk\javac.exe" -d out -sourcepath src src/atm/Main.java
& "$jdk\java.exe" -cp out atm.Main
```

The optional, separate console language-concept demo is `java -cp out atm.Main --console-demo`.

## Demo administrator

Administrator login is available from the sign-in page:

- Administrator PIN: `2468`

Create customer accounts from the registration screen. No real or personal credentials are included.

## Data storage

`data/accounts.txt`, `data/transactions.txt`, and `data/login_attempts.txt` are created/updated automatically. Account names and transaction text are Base64-encoded for safe separators; account PIN values are salted PBKDF2 hashes. Do not manually edit these files while the app is running. This is local educational storage, not encrypted banking-grade storage.

## Testing

```powershell
javac -d out -sourcepath src src/atm/Main.java
javac -cp out -d out -sourcepath src src/atm/test/CoreBankingTest.java
java -cp out atm.test.CoreBankingTest
```

`CoreBankingTest` covers registration, authentication, deposits, withdrawals, transfers, invalid/insufficient amounts, PIN changes, three-attempt locking, transaction history, and restart persistence.

## Limitations

This program has no network service, database, real identity verification, encryption-at-rest, concurrent multi-process access control, or account-unlock workflow. It must not be used with real financial data.
