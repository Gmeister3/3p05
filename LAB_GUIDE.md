# COSC 3P32 – Lab Activity 1: PostgreSQL Step-by-Step Guide

> **Worth:** 2% of final grade  
> **Deadline:** Must be demonstrated to a TA (James or Yasamin) during your registered lab in **Week 7 (week of February 23rd)**.  
> **Reference docs:** https://www.postgresql.org/docs/12/index.html

---

## Prerequisites

- A Sandcastle account (username = your login, initial password = your student ID number)
- PuTTY (or another SSH client) to connect to Sandcastle
- The file `populatelabA.sql` downloaded from Brightspace to the directory you will use on Sandcastle

---

## Step 0 – Connect to Sandcastle and Start PostgreSQL

1. Open PuTTY (or your SSH client) and log in to your Sandcastle account.
2. At the shell prompt, start PostgreSQL:
   ```
   psql
   ```
   Enter your password when prompted.
3. **Change your password immediately** (replace `<username>` and `newpass` with your own values):
   ```sql
   ALTER USER <username> WITH PASSWORD 'newpass';
   ```

---

## Step 1 – Create the Tables

You need four tables. Type each `CREATE TABLE` statement at the PostgreSQL prompt, followed by a semicolon (`;`).

> **Tip:** After creating each table, inspect it with `\d <tablename>` and list all your tables with `\d`.

### 1a. `customer`
```sql
CREATE TABLE customer (
    cid     INTEGER PRIMARY KEY,
    cname   CHAR(80),
    address CHAR(200)
);
```

### 1b. `book`
```sql
CREATE TABLE book (
    isbn        CHAR(13) PRIMARY KEY,
    title       CHAR(80),
    author      CHAR(80),
    qtyinstock  INTEGER,
    price       REAL
);
```

### 1c. `orders`
```sql
CREATE TABLE orders (
    ordernum   INTEGER PRIMARY KEY,
    cid        INTEGER REFERENCES customer(cid) ON DELETE CASCADE ON UPDATE CASCADE,
    order_date DATE,
    cardnum    CHAR(16)
);
```

### 1d. `orderlist`
```sql
CREATE TABLE orderlist (
    ordernum  INTEGER REFERENCES orders(ordernum) ON DELETE CASCADE ON UPDATE CASCADE,
    isbn      CHAR(13) REFERENCES book(isbn) ON DELETE CASCADE ON UPDATE CASCADE,
    qty       INTEGER,
    ship_date DATE,
    PRIMARY KEY (ordernum, isbn)
);
```

> **Note on foreign key options:** The `ON DELETE CASCADE` / `ON UPDATE CASCADE` choices above are one valid option. You will explore the effect of different options in Steps 5 and 6.

---

## Step 2 – Insert Data and Observe Constraint Violations

### 2a. Insert a valid book record
```sql
INSERT INTO book VALUES ('0000136006329', 'Operating Systems: Internals and Design Principles', 'Stallings, William', 1, 89.99);
```

### 2b. Try to violate the PRIMARY KEY constraint
Attempt to insert a duplicate `isbn`:
```sql
INSERT INTO book VALUES ('0000136006329', 'Some other random book', 'Stahl, Willa', 1, 9.99);
```
PostgreSQL will reject this with a duplicate-key error. ✅ This is expected.

### 2c. Try to violate the FOREIGN KEY constraint
Attempt to insert an order before any customer exists:
```sql
INSERT INTO orders VALUES (123, 101, '2011-05-01', '4505123412344321');
```
PostgreSQL will reject this because `cid = 101` does not exist in `customer`. ✅ This is expected.

---

## Step 3 – Populate Tables from a SQL File

1. Make sure `populatelabA.sql` is in the directory you were in when you started `psql`.
2. At the PostgreSQL prompt, run:
   ```
   \i populatelabA.sql
   ```
3. Observe any messages or errors. After it finishes, verify data was loaded:
   ```sql
   SELECT * FROM customer;
   SELECT * FROM book;
   SELECT * FROM orders;
   SELECT * FROM orderlist;
   ```

---

## Step 4 – Query Data

At any time you can inspect a table's contents:
```sql
SELECT * FROM book;
SELECT * FROM customer;
SELECT * FROM orders;
SELECT * FROM orderlist;
```

---

## Step 5 – Delete Data and Observe Foreign Key Behaviour

Try deleting a customer and observe the cascading effect on `orders` (and through it, `orderlist`):
```sql
DELETE FROM customer WHERE cid = 101;
```
Then check what happened to the related rows:
```sql
SELECT * FROM orders;
SELECT * FROM orderlist;
```

**Think about:** What would be different if you had chosen `ON DELETE NO ACTION` instead of `ON DELETE CASCADE`? (It would raise an error instead of deleting child rows.)

---

## Step 6 – Update Data and Observe Foreign Key Behaviour

Try updating the `cid` of a customer and observe the cascading effect:
```sql
UPDATE customer SET cid = 999 WHERE cid = 102;
```
Then check the related orders:
```sql
SELECT * FROM orders WHERE cid = 999;
```

**Think about:** What would be different if you had chosen `ON UPDATE NO ACTION`? (It would raise an error instead of cascading the new key value.)

---

## Step 7 – Write SQL Queries

### (a) Customers named 'Luke Skywalker'
```sql
SELECT * FROM customer WHERE TRIM(cname) = 'Luke Skywalker';
```
*(PostgreSQL pads `CHAR` values with spaces, so always use `TRIM()` when comparing character columns.)*

### (b) Names of books ordered by 'John Doe'
```sql
SELECT TRIM(b.title) AS book_title
FROM   book b
JOIN   orderlist ol ON ol.isbn = b.isbn
JOIN   orders   o  ON o.ordernum = ol.ordernum
JOIN   customer c  ON c.cid = o.cid
WHERE  TRIM(c.cname) = 'John Doe';
```

### (c) Customer IDs who have ordered the most books
```sql
SELECT o.cid, SUM(ol.qty) AS total_qty
FROM   orders o
JOIN   orderlist ol ON ol.ordernum = o.ordernum
GROUP  BY o.cid
ORDER  BY total_qty DESC
LIMIT  1;
```

### (d) Names of customers who have NOT ordered any books
```sql
SELECT TRIM(cname) AS cname
FROM   customer
WHERE  cid NOT IN (SELECT DISTINCT cid FROM orders);
```

---

## Step 8 – Exit PostgreSQL and Log Off

1. Exit PostgreSQL:
   ```
   \q
   ```
2. Log off Sandcastle:
   ```
   logout
   ```
   *(or type `exit`)*

---

## Summary Checklist

- [ ] Connected to Sandcastle and started `psql`
- [ ] Changed your password
- [ ] Created all four tables (`customer`, `book`, `orders`, `orderlist`) with correct primary/foreign keys
- [ ] Observed primary key violation when inserting duplicate book
- [ ] Observed foreign key violation when inserting order with no matching customer
- [ ] Loaded data with `\i populatelabA.sql`
- [ ] Verified data with `SELECT *` queries
- [ ] Deleted customer with `cid=101` and noted cascade effect
- [ ] Updated customer with `cid=102` and noted cascade effect
- [ ] Wrote and ran queries (a)–(d)
- [ ] Exited `psql` and logged off Sandcastle
- [ ] Demonstrated completed work to a TA before the end of your Week 7 lab
