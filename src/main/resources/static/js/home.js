document.getElementById('btnIniciar').addEventListener('click', function() {
    const button = this;
    const loading = document.getElementById('loading');
    const resultMessage = document.getElementById('resultMessage');
    
    // Desabilitar botão e mostrar loading
    button.disabled = true;
    loading.style.display = 'block';
    resultMessage.style.display = 'none';
    
    // Fazer a requisição para iniciar o campeonato
    fetch('/iniciar-campeonato', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        // Esconder loading
        loading.style.display = 'none';
        
        // Mostrar mensagem de resultado
        resultMessage.textContent = data.message;
        resultMessage.className = 'result-message ' + (data.status === 'success' ? 'success' : 'error');
        resultMessage.style.display = 'block';
        
        // Se a simulação foi bem sucedida, redirecionar para a página do campeonato após 1 segundo
        if (data.status === 'success') {
            setTimeout(() => {
                window.location.href = '/championship';
            }, 1000);
        } else {
            // Reabilitar botão apenas em caso de erro
            button.disabled = false;
        }
    })
    .catch(error => {
        // Em caso de erro
        loading.style.display = 'none';
        resultMessage.textContent = 'Erro ao iniciar o campeonato. Tente novamente.';
        resultMessage.className = 'result-message error';
        resultMessage.style.display = 'block';
        button.disabled = false;
    });
});
