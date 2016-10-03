# PeopleWebDb

#### SQL Paging

## Description
Project name: `PeopleWebDb`

Clone your `People Web` project to create a new project called **PeopleWebDb**. Create a database with a table called `people` that can store all the information from the CSV file. Write a method that reads from the CSV and inserts a record for each person into the table. Then make the app use the database. Finally, implement paging in the database query.

## Requirements

* Create a connection to your database.
* Write a method `createTables` which takes the database connection. Run it right after you create your connection. It should execute two things:
  * Calls `DROP TABLE IF EXISTS people`
  * Calls `CREATE TABLE people (id IDENTITY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, country VARCHAR, ip VARCHAR)`
* Write a method called `insertPerson` which takes the database connection and the columns.
* Write a method called `selectPerson` which takes the database connection and an `id` and returns a `Person`.
* Set up your `test` directory and write a test called `testPerson` which tests both of the aforementioned methods.
* Write a method called `populateDatabase` which takes the database connection, then parses the CSV file and inserts each row into the database.
* Write a method called `selectPeople` which takes the database connection and returns an `ArrayList<Person>` of everything from the database.
* Write a test called `testPeople` that tests `selectPeople`.
* Modify `selectPeople` to accept an `int offset` as an argument. Use `LIMIT 20 OFFSET ?` in your SQL query and pass the `offset` to your `PreparedStatement`.
* Remove the global `ArrayList<Person>`.
* Make the `/` route use `selectPeople(conn, offsetNum)` to get the `ArrayList<Person>`.
* Make the `/person` route use `selectPerson(conn, idNum)` to get the `Person`.
