# Product Context

## Why This Project Exists
Modern POS systems require seamless, reliable receipt printing. Many businesses use ESC/POS-compatible printers, but integration can be complex. This project provides a simple, configurable Java server to bridge POS applications and printers.

## Problems Solved
- Simplifies integration with ESC/POS printers
- Centralizes printer configuration and management
- Enables remote printing via WebSocket

## How It Should Work
- POS or other systems send print jobs to the server over WebSocket
- The server processes and prints receipts using the configured printer
- Minimal setup required; configuration via properties file

## User Experience Goals
- Fast, reliable printing
- Easy setup and configuration
- Clear error reporting and logging
- Flexible integration for developers 