document.getElementById('simulateButton')?.addEventListener('click', function() {
    const button = this;
    const resultMessage = document.getElementById('resultMessage');
    const roundsToSimulate = document.getElementById('roundsToSimulate').value;
    
    // Desabilitar botão
    button.disabled = true;
    button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Simulando...';
    
    // Preparar dados para enviar
    const requestData = {
        rounds: parseInt(roundsToSimulate)
    };
    
    // Fazer a requisição para simular rodadas
    fetch('/championship/simulate-rounds', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
    .then(response => response.json())
    .then(data => {
        // Mostrar mensagem de resultado
        resultMessage.textContent = data.message;
        resultMessage.className = data.status === 'success' ? 'alert alert-success' : 'alert alert-danger';
        resultMessage.style.display = 'block';
        
        // Se a simulação foi bem sucedida, recarregar a página após 1 segundo
        if (data.status === 'success') {
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        } else {
            // Reabilitar botão em caso de erro
            button.disabled = false;
            button.innerHTML = '<i class="bi bi-play-fill me-2"></i>Simular Rodadas';
        }
    })
    .catch(error => {
        // Em caso de erro
        resultMessage.textContent = 'Erro ao simular rodadas. Tente novamente.';
        resultMessage.className = 'alert alert-danger';
        resultMessage.style.display = 'block';
        
        // Reabilitar botão
        button.disabled = false;
        button.innerHTML = '<i class="bi bi-play-fill me-2"></i>Simular Rodadas';
    });
});
