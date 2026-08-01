# Expense Splitter (mini-Splitwise)

A group expense-sharing app: add people, form a group, log shared expenses,
and get the minimum set of "who pays whom" transactions to settle up.

Built to cover the Cognizant Java FSE (Angular) Deepskilling syllabus in one
project: Spring Boot REST APIs, Spring Data JPA/Hibernate, a design pattern,
exception handling, JUnit/Mockito tests, and an Angular frontend.

## Tech stack

- **Backend**: Java 17, Spring Boot 3.2, Spring Data JPA, Hibernate, H2 (in-memory DB)
- **Frontend**: Angular 17
- **Tests**: JUnit 5 + Mockito

## How to run

### Backend
```
cd backend
mvn spring-boot:run
```
Runs on `http://localhost:8080`. H2 console available at `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:expensedb`, user `sa`, no password).

### Frontend
```
cd frontend
npm install
npm start
```
Runs on `http://localhost:4200`.

### Run the tests
```
cd backend
mvn test
```

## How to use it

1. Add a few people (e.g. Alice, Bob, Charlie)
2. Create a group and select members
3. Open the group, add expenses — pick who paid and who the cost should be
   split between (splits equally by default)
4. Scroll down to see live "net balances" and the suggested settlement —
   the minimum number of payments needed to clear all debts

## The core algorithm: debt simplification (`SettlementService.java`)

This is the "DSA on top of CRUD" part worth talking through in an interview.

**Step 1 — Net balance per person:**
```
netBalance(person) = totalAmountTheyPaid - totalAmountTheyOwe(their share across all expenses)
```
Positive => they're owed money. Negative => they owe money.

**Step 2 — Greedy debt simplification (a variant of "min cash flow"):**
Instead of settling every expense individually (which could mean many small
payments), repeatedly match the person owed the *most* money with the person
who owes the *most* money, and settle the smaller of the two amounts between
them. Repeat until everyone's balance is ~0.

This is implemented with two max-heaps (`PriorityQueue`) — one for creditors,
one for debtors — giving O(log n) access to the largest balance on each side.

**Why it matters for the interview:** it's a genuine greedy/graph-adjacent
problem (same family as "minimum cash flow to settle debts"), not just a
database read/write, so it's a good example if asked "tell me about a
challenging piece of logic you wrote."

## Design patterns / concepts you can point to

- **Layered architecture**: Controller → Service → Repository, each with a
  single responsibility (a form of separation of concerns worth mentioning)
- **DTO pattern**: `ExpenseRequest`, `SettlementDto`, `BalanceDto` — never
  exposing JPA entities directly as API request/response bodies
- **Builder-ish object construction**: could be extended with a proper
  `Builder` pattern for `Expense` if asked to demonstrate one live
- **Centralized exception handling**: `@RestControllerAdvice` +
  `GlobalExceptionHandler` for consistent error responses, with custom
  exceptions (`ResourceNotFoundException`, `InvalidExpenseException`)
- **N+1 awareness**: `@ManyToOne`/`@OneToMany` mappings use lazy loading by
  default; worth mentioning you'd add `@EntityGraph` or a JOIN FETCH query
  if you saw N+1 query issues in the logs (Hibernate `show-sql=true` is
  already on in `application.properties` so you can point at real query logs)

## API endpoints

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/people` | Create a person |
| GET | `/api/people` | List people |
| POST | `/api/groups` | Create a group `{name, memberIds}` |
| GET | `/api/groups` | List groups |
| GET | `/api/groups/{id}` | Get one group |
| POST | `/api/groups/{groupId}/expenses` | Add an expense |
| GET | `/api/groups/{groupId}/expenses` | List a group's expenses |
| GET | `/api/groups/{groupId}/balances` | Net balance per person |
| GET | `/api/groups/{groupId}/settle` | Minimal settlement transactions |

## Possible "what would you improve" answers for the interview

- Add Spring Security + JWT for real auth (currently open/no auth)
- Add pagination on expense lists for large groups
- Move off H2 to Postgres/MySQL for persistence across restarts
- Add a currency field / multi-currency support
- Add optimistic locking (`@Version`) on `Expense` to avoid race conditions
  when two people add expenses at the same time
- Containerize with Docker (`Dockerfile` for backend + frontend, `docker-compose`)
- Add integration tests with `@SpringBootTest` + Testcontainers, not just
  unit tests with mocks
