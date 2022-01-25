window.onload = () => {
    updateClock();
    setInterval(updateClock, 13000);
}

function updateClock() {
    let dt = new Date();
    let time = dt.getHours() + ":" + dt.getMinutes().toString().padStart(2, '0');
    let date = dt.getFullYear() + "-" + (dt.getMonth() + 1) + "-" + dt.getDate();

    document.getElementById("clock-time").innerText = time;
    document.getElementById("clock-date").innerText = date;
}



