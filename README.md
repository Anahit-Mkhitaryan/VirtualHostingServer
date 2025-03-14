# Virtual Hosting Server

## Overview

This project is a simple virtual hosting server that allows you to host multiple websites on a single server, each accessible via its own domain. It includes a management interface for adding and removing virtual hosts.

## How to Run It

1. **Compile the Project:**
    - Open a terminal or command prompt.
    - Navigate to the project directory.
    - Compile the project with:
      ```bash
      javac -d bin src/main/java/com/*.java
      ```

2. **Start the Server:**
    - Run the server with:
      ```bash
      java -cp bin main.java.com.VirtualHostingServer
      ```

3. **Access the Server:**
    - The server runs on port `8081`. Open these URLs in your web browser:
        - **Admin Interface:** [http://localhost:8081/admin](http://localhost:8081/admin)
        - **Website Content:** [http://localhost:8081/<domain>](http://localhost:8081/<domain>)

## Using the Admin Interface

- **Add a Virtual Host:**
    - Go to [http://localhost:8081/admin](http://localhost:8081/admin).
    - Enter a domain and directory in the form and click "Add".

- **Remove a Virtual Host:**
    - On the same admin page, find the domain you want to remove and click "Remove".

## Notes

- The server uses the `hosts` file to map domains to IP addresses. Ensure the application has the necessary permissions to modify this file.

