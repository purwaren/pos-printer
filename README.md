# mpos-printer-receipt

MPOS Printer Implementation using Java Point of Sales Using escpos-coffee Library

## Overview
This application acts as a WebSocket server that receives print jobs (receipts) and prints them using an ESC/POS-compatible printer. It is designed for easy integration with POS systems or any application that needs to print receipts remotely.

## How to Run

### Without Compile
```sh
mvn exec:java -Dexec.mainClass="main.java.id.modefashion.printer.App"
```

### With Compile
```sh
mvn clean compile exec:java -Dexec.mainClass="main.java.id.modefashion.printer.App"
```

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
