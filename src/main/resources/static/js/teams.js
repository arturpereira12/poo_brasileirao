document.addEventListener('DOMContentLoaded', function() {
    const sortSelect = document.getElementById('sortTeams');
    
    // Ordenar os times alfabeticamente por padrão ao carregar a página
    sortTeamsByValue('alphabetical');
    
    sortSelect.addEventListener('change', function() {
        const sortValue = this.value;
        sortTeamsByValue(sortValue);
    });
    
    function sortTeamsByValue(sortValue) {
        const teamsContainer = document.getElementById('teamsContainer');
        const teamCards = Array.from(document.querySelectorAll('.col-lg-6.mb-4'));
        
        // Ordenar os cards com base na opção selecionada
        teamCards.sort(function(a, b) {
            const teamNameA = a.querySelector('.team-name').textContent;
            const teamNameB = b.querySelector('.team-name').textContent;
            
            const strengthTextA = a.querySelector('.team-strength').textContent;
            const strengthTextB = b.querySelector('.team-strength').textContent;
            
            // Extrair apenas o número da força (remover o texto "de Força")
            const teamStrengthA = parseInt(strengthTextA);
            const teamStrengthB = parseInt(strengthTextB);
            
            switch(sortValue) {
                case 'alphabetical':
                    return teamNameA.localeCompare(teamNameB);
                case 'strength-desc':
                    return teamStrengthB - teamStrengthA;
                case 'strength-asc':
                    return teamStrengthA - teamStrengthB;
                default:
                    return teamNameA.localeCompare(teamNameB); // Agora o default também é alfabético
            }
        });
        
        // Limpar o container e adicionar os cards ordenados
        teamsContainer.innerHTML = '';
        teamCards.forEach(function(card) {
            teamsContainer.appendChild(card);
        });
    }
});
