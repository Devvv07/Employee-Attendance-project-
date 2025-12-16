# 🗂️ Java Attendance Management System

A **Java Swing–based desktop application** for managing employee attendance. The system provides a secure login, attendance marking interface, and database connectivity using MySQL. This project is suitable for **college submission** and **GitHub portfolio**.

---

## ✨ Features

* Employee login system
* Secure authentication using database
* Attendance marking interface
* MySQL database connectivity
* Simple and clean Swing GUI
* Modular Java class structure

---

## 🧰 Tech Stack

* **Language:** Java
* **GUI:** Java Swing
* **Database:** MySQL
* **JDBC:** MySQL Connector/J

---

## 📁 Project Structure

```
Java-Attendance-Management-System/
│
├── src/
│   ├── Main.java              # Application entry point
│   ├── EmployeeLogin.java     # Login UI and authentication logic
│   ├── AttendanceFrame.java   # Attendance marking UI
│   └── DBConnection.java      # MySQL database connection
│
├── database.sql               # Database schema (optional)
├── README.md
```

---

## 🗄️ Database Schema (Example)

```sql
CREATE DATABASE attendance_db;
USE attendance_db;

CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50)
);

CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    date DATE,
    status VARCHAR(10)
);
```

---

## ⚙️ How to Run the Project

### 1️⃣ Prerequisites

* Java JDK 8 or above
* MySQL Server
* MySQL Connector/J (JAR file)

---

### 2️⃣ Setup Database

* Create database and tables using the SQL above
* Update database credentials in `DBConnection.java`

```java
String url = "jdbc:mysql://localhost:3306/attendance_db";
String user = "root";
String password = "";
```

---

### 3️⃣ Compile the Project

```bash
javac src/*.java
```

---

### 4️⃣ Run the Application

```bash
java src.Main
```

---

## 🔐 Login Details (Sample)

```
Username: admin
Password: admin123
```

*(Change as per your database values)*

---

## 🚀 Future Enhancements

* Role-based access (Admin / Employee)
* Attendance reports (CSV / PDF)
* Date-wise attendance view
* Improved password encryption
* Modern UI design

---

## 👤 Author

**Dev Mohite**
BSc Data Science – 1st Year
Java | Swing | MySQL

---
---

⭐ If you find this project useful, don’t forget to **star** the repository!
