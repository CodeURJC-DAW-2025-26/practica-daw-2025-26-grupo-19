document.addEventListener('DOMContentLoaded', function() {
    const nombresGoleadores = [];
    const goles = [];
    const nombresAsistentes = [];
    const asistencias = [];

    document.querySelectorAll('.goleador-item').forEach(item => {
        const nombre = item.getAttribute('data-nombre');
        const equipo = item.getAttribute('data-equipo');
        const cantGoles = parseInt(item.getAttribute('data-goles'), 10);
        
        nombresGoleadores.push(`${nombre} (${equipo})`);
        goles.push(cantGoles);
    });

    document.querySelectorAll('.asistente-item').forEach(item => {
        const nombre = item.getAttribute('data-nombre');
        const equipo = item.getAttribute('data-equipo');
        const cantAsistencias = parseInt(item.getAttribute('data-asistencias'), 10);
        
        nombresAsistentes.push(`${nombre} (${equipo})`);
        asistencias.push(cantAsistencias);
    });

    if (nombresGoleadores.length > 0) {
        const canvasGoles = document.getElementById('graficoGoleadores');
        if (canvasGoles) {
            new Chart(canvasGoles, {
                type: 'bar',
                data: {
                    labels: nombresGoleadores,
                    datasets: [{
                        label: 'Goles anotados',
                        data: goles,
                        backgroundColor: 'rgba(13, 110, 253, 0.7)',
                        borderColor: 'rgba(13, 110, 253, 1)',
                        borderWidth: 1,
                        borderRadius: 4
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    scales: {
                        x: { beginAtZero: true, ticks: { stepSize: 1 } }
                    },
                    plugins: { legend: { display: false } }
                }
            });
        }
    }

    if (nombresAsistentes.length > 0) {
        const canvasAsis = document.getElementById('graficoAsistentes');
        if (canvasAsis) {
            new Chart(canvasAsis, {
                type: 'bar',
                data: {
                    labels: nombresAsistentes,
                    datasets: [{
                        label: 'Asistencias dadas',
                        data: asistencias,
                        backgroundColor: 'rgba(255, 193, 7, 0.7)', 
                        borderColor: 'rgba(255, 193, 7, 1)',
                        borderWidth: 1,
                        borderRadius: 4
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: { beginAtZero: true, ticks: { stepSize: 1 } }
                    },
                    plugins: { legend: { display: false } }
                }
            });
        }
    }
});