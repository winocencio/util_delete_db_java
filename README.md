## README.md for Bulk Delete Records Code

### 1. Introduction

This project provides a Java program to delete records in bulk from a MySQL database. The main goal is to facilitate the removal of large amounts of data efficiently and securely.

### 2. How It Works

The program reads a CSV file containing the primary key values of the records to be deleted. Then, it connects to the MySQL database and executes DELETE statements to remove each corresponding record. The program also offers features to track the deletion progress and display informative messages.

### 3. Prerequisites

* Java installed and configured in your development environment.
* MySQL database with the tables and data you want to delete.
* CSV file containing the primary key values of the records to be deleted. The CSV file must have the first row with the primary key column names and subsequent rows with the primary key values for each record to be deleted.

### 4. Installation

1. Clone this repository or download the source code.
2. Make sure the `db.properties` file is configured with your MySQL database access credentials.
3. Place the CSV file containing the primary key values of the records to be deleted in the `src/main/resources` folder with name `data.csv`.

### 5. How to Use

1. Open a terminal or command prompt and navigate to the project root folder.
2. Execute the following commands:

```
mvn clean install package
java -jar target/util_delete_db_java-1.0-jar-with-dependencies.jar
```

3. The program will start deleting the records according to the settings in the `db.properties` file and the `data.csv` file.

### 6. Important Notes

* This program is intended for experienced Java users with knowledge of MySQL databases.
* Make sure to back up your database before running the program, as the deletion operation is permanent.
* Test the program in a development environment before using it in production to ensure records are being deleted correctly.

### 7. Contributions

If you have any suggestions, improvements, or fixes for the code, feel free to contribute to the project's GitHub repository.
