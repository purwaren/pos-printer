# mpos-printer-receipt

![version](https://img.shields.io/badge/version-1.0.0-blue)

MPOS Printer Implementation using Java Point of Sales Using escpos-coffee Library

## Features (v1.0.0)
- JavaFX GUI for cross-platform use
- Start/Stop WebSocket server from GUI
- Real-time log display in GUI (monospace, auto-scroll)
- Printer selection from system dropdown
- Edit and save printer configuration (port, printer, font family, font size) via GUI
- Font family and size selection via dropdown (only valid values)
- Configuration changes require manual server restart
- WebSocket server for receiving print jobs
- Supports print jobs in JSON and delimited string format
- Receipt printing: text and barcode (image printing not yet implemented)
- Logging and error handling
- Example WebSocket client provided

## Overview
This application acts as a WebSocket server that receives print jobs (receipts) and prints them using an ESC/POS-compatible printer. It is designed for easy integration with POS systems or any application that needs to print receipts remotely.

## How to Run

### JavaFX GUI (Recommended)

> **Requirements:**
> - Java 11 or higher (Java 21+ recommended)
> - Maven 3.6+

To launch the graphical user interface (GUI):

```sh
mvn clean compile exec:java
```

This will start the JavaFX-based GUI, where you can:
- Start/Stop the WebSocket server
- View real-time logs
- Edit printer configuration via the menu

If you change the configuration, you must manually stop and restart the server for changes to take effect.

### Command-Line (Legacy)

You can still run the server without the GUI:

```sh
mvn exec:java -Dexec.mainClass="main.java.id.modefashion.printer.App"
```

or

```sh
mvn clean compile exec:java -Dexec.mainClass="main.java.id.modefashion.printer.App"
```

## Running with a Script

You can create a simple script to run the application JAR on your platform:

### Linux (Bash)
Create a file named `run-printer.sh`:
```bash
#!/bin/bash
java -jar printer-1.0.0.jar
```
Make it executable:
```sh
chmod +x run-printer.sh
```
Run it:
```sh
./run-printer.sh
```

### Windows (Batch)
Create a file named `run-printer.bat`:
```bat
@echo off
java -jar printer-1.0.0.jar
pause
```
Double-click the batch file to run the application.

## Configuration
Edit the `printer.properties` file to set your printer name, port, font family, and font size:

```
printer.port = 20001
printer.name = BP-LITE 80D+80X Printer
font.family = Font_A_Default
font.size = _1
```

## Usage Scenario

1. **Start the Server**
   - Run the application as described above. The server will listen for WebSocket connections on the port specified in `printer.properties` (default: 20001).

2. **Send a Print Job**
   - Connect to the server using a WebSocket client (from your POS system, a test tool, or a custom script).
   - Send a message containing the receipt data. The server supports two formats:
     - **Structured JSON** (recommended):
       ```json
       [
         { "type": "string", "content": "Item 1   $10.00" },
         { "type": "string", "content": "Item 2   $5.00" },
         { "type": "barcode", "content": "1234567890" }
       ]
       ```
     - **Delimited String** (legacy/simple):
       ```
       Item 1   $10.00#Item 2   $5.00#Thank you!
       ```
       (Each line separated by `#`)

3. **Receipt Printing**
   - The server will process the message and print the receipt using the configured printer.
   - Supported line types: `string` (text), `barcode` (barcode). Image printing is not yet implemented.

4. **Response**
   - The server will send a "Success" message back to the client after processing the print job.

## Example WebSocket Client (Python)

```python
import websocket
import json

ws = websocket.create_connection("ws://localhost:20001")
receipt = [
    {"type": "string", "content": "Item 1   $10.00"},
    {"type": "string", "content": "Item 2   $5.00"},
    {"type": "barcode", "content": "1234567890"}
]
ws.send(json.dumps(receipt))
print(ws.recv())  # Should print 'Success'
ws.close()
```

## Notes
- Make sure your printer is connected and matches the name in `printer.properties`.
- Only ESC/POS-compatible printers are supported.
- For advanced integration, refer to the code and memory-bank documentation.
