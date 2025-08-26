# Jachat

Jachat is a simple Java-based multi-user chat server and client application supporting private chat rooms, user statuses, admin commands, and chat history persistence.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)

## Features

- Multiple users can connect to the server and chat in real time.
- Private chat rooms with invitation and join functionality.
- Admin can kick users from the server.
- User status management (e.g., online, away).
- Chat history for each room is saved to a .txt file.
- Command-based interface for room and user management.

## Installation
### Prerequisites
Java 8 or higher
### Build & Run
1. Clone the repository:
```
    git clone https://github.com/jungboixitinh/jachat.git

    cd jachat
```
2. Compile the source code:
```bash
javac src/*.java
```
3. Start the server:
```
java -cp src Server
```
4. Start a client (in a new terminal window):
```
java -cp src Client
```
## Usage

- **/help** — Show available commands.
- **/create <room_name>** — Create a new private chat room.
- **/join <room_name>** — Join an invited room.
- **/invite <username> <room_name>** — Invite a user to a room.
- **/leave** — Leave the current room (saves chat history).
- **/listroom** — List all chat rooms.
- **/listuser** — List all online users.
- **/status <new_status>** — Change your status.
- **/kick <username>** — (Admin only) Kick a user from the server.





