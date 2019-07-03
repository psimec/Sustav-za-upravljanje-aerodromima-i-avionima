var wsocket;
function connect() {
    var aplikacija = "/" + document.location.pathname.split("/")[1];
    var wsUri = "ws://" + document.location.host + aplikacija + "/infoKomande";
    wsocket = new WebSocket(wsUri);
    wsocket.onmessage = onMessage;
}
function onMessage(evt) {
    var poruka = JSON.parse(evt.data);
    var korisnik = document.getElementById("forma:korime").value;

    if (poruka.korime == korisnik) {
        osvjeziPoruke();
    }

    if (typeof poruka.korime === 'undefined') {
        osvjeziPorukeMqtt();
    }
}

window.addEventListener("load", connect, false);