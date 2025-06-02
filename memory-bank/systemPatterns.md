# System Patterns

## Architecture Overview
- **Layered Structure:**
  - DTOs for data transfer
  - Utility classes for printer discovery
  - Workers for business logic
  - Main server and entry point in the root printer package

## Key Technical Decisions
- Use of escpos-coffee for ESC/POS printer communication
- WebSocket for receiving print jobs (real-time, bidirectional)
- Apache Commons Configuration for flexible property management
- SLF4J + Logback for logging
- Gson for JSON parsing
- Lombok for reducing boilerplate

## Design Patterns
- **Factory/Helper:** For printer discovery and selection
- **Worker:** For encapsulating receipt processing logic
- **DTO:** For structured data transfer between layers

## Component Relationships
- `App` initializes configuration and starts the `PrintServer`
- `PrintServer` receives jobs and delegates to the appropriate worker
- Workers process data and interact with printer utilities
- DTOs represent receipt data and lines 