# Blockchain-Basics-Building-Distributing-and-Securing-Systems
Focussed on illustrating the tamper-evident design of blockchain technology. We build a standalone blockchain and create a distributed system using JSON messages over TCP sockets for client-server interaction. 


**Task 0:**

- Study the Javadoc provided for Block.java and BlockChain.java.
- Write a solution for Task 0, a non-distributed stand-alone program.
- Capture the console output and include it labeled as "Task 0 Execution" in the PDF submission.
- Note that the order of name-value pairs within a JSON message is not significant.

**Task1:**


- Develop a distributed client-server application for.
- Client interacts with the user through a menu-driven interface and communicates with the server using JSON over TCP sockets.
- Generate RSA public and private keys for each client session and display them to the user.
- Compute the client's ID by taking the least significant 20 bytes of the hash of the client's public key (e and n concatenated).
- Transmit the client's public key (e and n) with each request, along with the ID.
- Sign each request using the client's private key (d and n) by encrypting the hash of the message with RSA encryption.
- Server verifies the request by checking if the public key hashes to the ID and if the request is properly signed.
- Utilize SHA-256 for the hash function.
- Implement a proxy design to encapsulate communication code.
