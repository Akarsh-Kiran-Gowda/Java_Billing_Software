# Billing Software

A simple desktop-based Billing Software application built using Java Swing and MySQL for managing orders, customer details, and generating invoices.

---

## Features

1. **Login Page**
   - Secure admin login functionality.

2. **Admin Page**
   - Manage transactions.
   - Generate transaction reports within a specific time frame.

3. **Billing Page**
   - Add customer details (name, mobile number).
   - Search for items dynamically from the database.
   - Add orders for customers.
   - Apply discounts.
   - Generate PDF invoices with the itemized list, price, and total.

4. **Database Integration**
   - Uses MySQL to store customer details, orders, and item information.

5. **Invoice Generation**
   - Generates detailed PDF invoices for each customer.

---

## Prerequisites

1. **Java JDK**: Ensure you have Java JDK (version 8 or above) installed.
2. **MySQL Database**: Set up a MySQL database with the following details:
   - Host: `localhost`
   - Port: `3306`
   - Database Name: `billing_db`
   - Username: `root`
   - Password: `root`

## Dependencies

1. **iTextPDF Library**:
   - Used for generating PDF invoices.
   - Ensure you include the `iText` JAR file in your project classpath.

2. **MySQL JDBC Driver**:
   - Required to connect the application to the MySQL database.
   - Add the `mysql-connector-java` JAR file to your classpath.
