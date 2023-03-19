const webSocketsUrl = "wss://localhost/ws";

console.log("Starting js to test websockets...");


let socket;

connect();


function connect() {
    if (socket) {
        socket.close();
    }

    socket = new WebSocket(webSocketsUrl);
    socket.onopen = function (e) {
        alert("[open] Connection established");
        const longMessage = "message".repeat(100);
        socket.send(JSON.stringify({
            type: "USER_AUTH",
            data: longMessage
        }));
//        setInterval(function() {
//            if (socket) {
//                socket.send(JSON.stringify({
//                        "type": "USER_AUTHENTICATION",
//                        "data": Date.now()
//                    }));
//                   }
//        }, 1000);
    };

    socket.onmessage = function (e) {
        const msg = `[message] Data received from server: ${e.data}`;
        console.log(msg);
//        alert(msg);
    };

    socket.onclose = function (e) {
        if (e.wasClean) {
            alert(`[close] Connection closed cleanly, code=${e.code} reason=${e.reason}`);
        } else {
            // e.g. server process killed or network down
            // event.code is usually 1006 in this case
            alert(`[close] Connection died, code = ${e.code}`);
        }
    };

    socket.onerror = function (e) {
        alert(`[error] ${e.message}`);
        console.error("Error event", e);
    };
}


