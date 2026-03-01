document.addEventListener('DOMContentLoaded', function() {
    // Arrays vacíos para Chart.js
    const nombresGoleadores = [];
    const goles = [];
    const nombresAsistentes = [];
    const asistencias = [];

    // Leer los goleadores del HTML invisible
    document.querySelectorAll('.goleador-item').forEach(item => {
        const nombre = item.getAttribute('data-nombre');
        const equipo = item.getAttribute('data-equipo');
        const cantGoles = parseInt(item.getAttribute('data-goles'), 10);
        
        nombresGoleadores.push(`${nombre} (${equipo})`);
        goles.push(cantGoles);
    });

    // Leer los asistentes del HTML invisible
    document.querySelectorAll('.asistente-item').forEach(item => {
        const nombre = item.getAttribute('data-nombre');
        const equipo = item.getAttribute('data-equipo');
        const cantAsistencias = parseInt(item.getAttribute('data-asistencias'), 10);
        
        nombresAsistentes.push(`${nombre} (${equipo})`);
        asistencias.push(cantAsistencias);
    });

    // ==========================================
    // 1. Gráfica de Goleadores (HORIZONTAL)
    // ==========================================
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
                        backgroundColor: 'rgba(13, 110, 253, 0.7)', // Azul Bootstrap
                        borderColor: 'rgba(13, 110, 253, 1)',
                        borderWidth: 1,
                        borderRadius: 4
                    }]
                },
                options: {
                    indexAxis: 'y', // <--- ESTO LA HACE HORIZONTAL
                    responsive: true,
                    scales: {
                        // En horizontal, los números van en la X
                        x: { beginAtZero: true, ticks: { stepSize: 1 } }
                    },
                    plugins: { legend: { display: false } }
                }
            });
        }
    }

    // ==========================================
    // 2. Gráfica de Asistentes (VERTICAL)
    // ==========================================
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
                        backgroundColor: 'rgba(255, 193, 7, 0.7)', // Amarillo Bootstrap
                        borderColor: 'rgba(255, 193, 7, 1)',
                        borderWidth: 1,
                        borderRadius: 4
                    }]
                },
                options: {
                    // Al quitar el indexAxis: 'y', Chart.js asume que es VERTICAL por defecto
                    responsive: true,
                    scales: {
                        // En vertical, los números van en la Y
                        y: { beginAtZero: true, ticks: { stepSize: 1 } }
                    },
                    plugins: { legend: { display: false } }
                }
            });
        }
    }
});