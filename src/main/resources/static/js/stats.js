document.addEventListener('DOMContentLoaded', function() {
    // Dados para o gráfico de resultados
    const homeWins = window.statsData?.homeWins || 0;
    const awayWins = window.statsData?.awayWins || 0;
    const draws = window.statsData?.draws || 0;
    
    if (document.getElementById('resultsChart') && homeWins !== null && awayWins !== null && draws !== null) {
        const resultsCtx = document.getElementById('resultsChart').getContext('2d');
        new Chart(resultsCtx, {
            type: 'bar',
            data: {
                labels: ['Vitórias em Casa', 'Vitórias Fora', 'Empates'],
                datasets: [{
                    label: 'Resultados',
                    data: [homeWins, awayWins, draws],
                    backgroundColor: [
                        'rgba(255, 225, 0, 0.7)',
                        'rgba(255, 225, 0, 0.4)',
                        'rgba(255, 225, 0, 0.2)'
                    ],
                    borderColor: [
                        'rgba(255, 225, 0, 1)',
                        'rgba(255, 225, 0, 1)',
                        'rgba(255, 225, 0, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            color: '#CCCCCC',
                        },
                        grid: {
                            color: 'rgba(255, 255, 255, 0.1)',
                        }
                    },
                    x: {
                        ticks: {
                            color: '#CCCCCC',
                        },
                        grid: {
                            display: false,
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });
    }
    
    // Dados para o gráfico de gols por posição
    const goalsByPosition = window.statsData?.goalsByPosition;
    
    if (document.getElementById('positionChart') && goalsByPosition) {
        const positionCtx = document.getElementById('positionChart').getContext('2d');
        new Chart(positionCtx, {
            type: 'pie',
            data: {
                labels: Object.keys(goalsByPosition),
                datasets: [{
                    data: Object.values(goalsByPosition),
                    backgroundColor: [
                        'rgba(255, 225, 0, 0.8)',
                        'rgba(255, 225, 0, 0.5)',
                        'rgba(255, 225, 0, 0.3)'
                    ],
                    borderColor: [
                        'rgba(255, 225, 0, 1)',
                        'rgba(255, 225, 0, 1)',
                        'rgba(255, 225, 0, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            color: '#CCCCCC',
                            font: {
                                family: 'Inter',
                                size: 12,
                            }
                        }
                    }
                }
            }
        });
    }
    
    // Dados para o gráfico de tipos de partidas
    const highScoring = window.statsData?.highScoringGames || 0;
    const lowScoring = window.statsData?.lowScoringGames || 0;
    const mediumScoring = (homeWins + awayWins + draws) - highScoring - lowScoring;
    
    if (document.getElementById('gameTypesChart') && highScoring !== null && lowScoring !== null) {
        const gameTypesCtx = document.getElementById('gameTypesChart').getContext('2d');
        new Chart(gameTypesCtx, {
            type: 'doughnut',
            data: {
                labels: ['Partidas com 3+ gols', 'Partidas com 2 gols', 'Partidas com 0-1 gols'],
                datasets: [{
                    data: [highScoring, mediumScoring, lowScoring],
                    backgroundColor: [
                        'rgba(255, 225, 0, 0.8)',
                        'rgba(255, 225, 0, 0.5)',
                        'rgba(255, 225, 0, 0.3)'
                    ],
                    borderColor: [
                        'rgba(255, 225, 0, 1)',
                        'rgba(255, 225, 0, 1)',
                        'rgba(255, 225, 0, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            color: '#CCCCCC',
                            font: {
                                family: 'Inter',
                                size: 12,
                            }
                        }
                    }
                }
            }
        });
    }
});
