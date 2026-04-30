const UPDATE_INTERVAL = 30000; // 30 secondi

async function fetchWeatherData() {
    try {
        const response = await fetch('/api/meteo/medie');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        updateUI(data);
        updateTimestamp();
    } catch (error) {
        console.error("Errore nel recupero dei dati meteo:", error);
        document.getElementById('last-update').innerText = "Errore di connessione";
        document.querySelector('.dot').style.backgroundColor = "#ff1744";
        document.querySelector('.dot').style.boxShadow = "0 0 8px #ff1744";
    }
}

function updateUI(data) {
    const grid = document.getElementById('weather-grid');
    grid.innerHTML = ''; // Svuota la griglia per ricrearla con i nuovi dati

    if (!data || data.length === 0) {
        grid.innerHTML = '<p>Nessun dato disponibile al momento.</p>';
        return;
    }

    data.forEach(city => {
        const card = document.createElement('div');
        card.className = 'weather-card';
        
        card.innerHTML = `
            <div class="city-name">${city.citta}</div>
            <div class="weather-data">
                <div class="data-row">
                    <span class="label">Temp</span>
                    <div>
                        <span class="value">${city.media_temperatura.toFixed(1)}</span>
                        <span class="unit">${city.unita_temperatura}</span>
                    </div>
                </div>
                <div class="data-row">
                    <span class="label">Vento</span>
                    <div>
                        <span class="value">${city.media_vento.toFixed(1)}</span>
                        <span class="unit">${city.unita_vento}</span>
                    </div>
                </div>
            </div>
        `;
        grid.appendChild(card);
    });
}

function updateTimestamp() {
    const now = new Date();
    const timeString = now.toLocaleTimeString('it-IT', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
    document.getElementById('last-update').innerText = `Ultimo aggiornamento: ${timeString}`;
    document.querySelector('.dot').style.backgroundColor = "#00e676";
    document.querySelector('.dot').style.boxShadow = "0 0 8px #00e676";
}

// Avvia il caricamento dei dati al caricamento della pagina e imposta il polling
document.addEventListener('DOMContentLoaded', () => {
    fetchWeatherData();
    setInterval(fetchWeatherData, UPDATE_INTERVAL);
});
