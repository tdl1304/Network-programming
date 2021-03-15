const http = require('http');
const { createHash } = require('crypto');

// Simple HTTP server responds with a simple WebSocket client test
const httpServer = http.createServer((req, res) => {
    let content = `<!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8" />
    </head>
    <body>
        WebSocket test page
        <script>
        let ws = new WebSocket('ws://localhost:3000');
        ws.onmessage = event => alert('Message from server: ' + event.data);
        ws.onopen = () => ws.send('hello from client');
        </script>
    </body>
    </html>
    `;
    res.setHeader("Content-Type", "text/html");
    res.writeHead(200);
    res.end(content);
});

//WebSocket server
let sockets = [];
httpServer.on('upgrade', (req, socket) => {
    const id = sockets.length;
    console.log('A client has been connected with id: '+ id);
    sockets.push(socket);
    //Ws handshake
    let hash = createHash('sha1')
        .update(req.headers['sec-websocket-key']+"258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
        .digest('base64');
    let responeHeaders = 'HTTP/1.1 101 Switching Protocols\r\n'+
            'Upgrade: WebSocket\r\n'+
            'Connection: Upgrade\r\n'+
            'Sec-WebSocket-Accept: '+ hash+'\r\n\r\n';
    socket.write(responeHeaders)
    new Promise(resolve => socket.write(encodeMsg("hello world " + id)))

    //When client sends data over ws
    socket.on("data", (req) => {
        let msg = decodeMsg(req)
        if(msg === null) {
            console.log("Client id: "+id+" has disconnected")
            socket.end()
        } else {
            console.log(msg+" id:"+id)
            for(let i = 0; i<sockets.length;i++) {
                try{
                    if(i !== id) {
                        new Promise(resolve => sockets[i].write(encodeMsg(msg+" id:"+id)))
                    }
                } catch(error) {
                    sockets.splice(i, 1)
                    i--;
                }
            }
        }
    });
});

httpServer.on('error', (error) => {
  console.error('Error: ', error);
});

httpServer.listen(3000, () => {
    console.log('HTTP server listening on port 3000');
  });

function encodeMsg(msg) {
    let fin = 0x81;
    let length = msg.length;
    let encodedMsg = Buffer.concat([Buffer.from([fin,length]), Buffer.from(msg)]);
    return encodedMsg;
}

function decodeMsg(bytes) {
    if(bytes[0] === 0x81) {
        let length = bytes[1] & 0x7F;
        let mask = bytes.slice(2,6);
        let msg = "";
        for(let i = 0; i<length; i++) {
            let byte = bytes[6+i] ^ mask[i%mask.length];
            msg += String.fromCharCode(byte);
        }
        return msg;
    } else {
        return null;
    }
}