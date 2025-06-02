# Tech Context

## Technologies Used
- Java 7
- Maven (build, dependency management)
- escpos-coffee (ESC/POS printer communication)
- Apache Commons Configuration (property management)
- Gson (JSON parsing)
- Java-WebSocket (WebSocket server)
- JUnit (testing)
- SLF4J + Logback (logging)
- Lombok (boilerplate reduction)

## Development Setup
- Requires Java 7+
- Build and run with Maven
- Configuration via `printer.properties`

## Technical Constraints
- Only supports ESC/POS-compatible printers
- WebSocket only (no REST API)
- No built-in authentication/security
- Assumes local or network-accessible printers

## Dependencies
See `pom.xml` for full dependency list and versions. 