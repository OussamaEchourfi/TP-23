const etat = { busy: false };

async function charger() {
    if (etat.busy) return;
    etat.busy = true;
    try {
        const [registre, sante] = await Promise.all([
            fetch('/api/migration/registry').then(r => r.json()),
            fetch('/api/migration/health').then(r => r.json())
        ]);
        rendreKpis(registre, sante);
        rendreRegistre(registre.services || []);
        rendreInstances(registre.instances || []);
        rendreMigration(registre, sante);
    } catch (e) {
        document.getElementById('kpis').innerHTML =
            '<div class="kpi"><div class="label">Erreur</div><div class="value error">' + e.message + '</div></div>';
    } finally {
        etat.busy = false;
    }
}

function rendreKpis(registre, sante) {
    const cartes = [
        { label: 'Fournisseur discovery', value: registre.provider || '-', unite: 'CONSUL' },
        { label: 'Services decouverts', value: registre.serviceCount ?? 0, unite: 'registre' },
        { label: 'Instances actives', value: registre.instanceCount ?? 0, unite: 'agents' },
        { label: 'Residus Eureka', value: (sante.eurekaResidual || []).length, unite: 'attendu : 0' }
    ];
    document.getElementById('kpis').innerHTML = cartes.map(c => 
        '<div class="kpi"><div class="label">' + c.label + '</div>' +
        '<div class="value">' + c.value + ' <small>' + c.unite + '</small></div></div>'
    ).join('');
}

function rendreRegistre(services) {
    const cible = document.getElementById('registry');
    if (!services.length) {
        cible.innerHTML = '<span class="pill error">Aucun service enregistre dans Consul</span>';
        return;
    }
    cible.innerHTML = services.map(s => 
        '<span class="pill"><span class="dot"></span>' + s + '</span>'
    ).join('');
}

function rendreInstances(instances) {
    const corps = document.querySelector('#instances tbody');
    corps.innerHTML = instances.map(i => 
        '<tr><td>' + i.service + '</td><td>' + i.instanceId + '</td><td>' + i.host + '</td>' +
        '<td>' + i.port + '</td><td>' + i.uri + '</td></tr>'
    ).join('') || '<tr><td colspan="5">Aucune instance</td></tr>';
}

function rendreMigration(registre, sante) {
    const eureka = (sante.eurekaResidual || []).length;
    const lignes = [
        { nom: 'Consul joignable', ok: sante.consulReachable === true },
        { nom: 'Aucun service EUREKA', ok: eureka === 0 },
        { nom: 'SERVICE-CLIENT decouvert', ok: (registre.services || []).includes('SERVICE-CLIENT') },
        { nom: 'SERVICE-CAR decouvert', ok: (registre.services || []).includes('SERVICE-CAR') },
        { nom: 'SERVICE-GATEWAY decouvert', ok: (registre.services || []).includes('SERVICE-GATEWAY') }
    ];
    document.getElementById('migration').innerHTML = lignes.map(l =>
        '<div class="bar-row"><span>' + l.nom + '</span>' +
        '<div class="bar-track"><div class="bar-fill" style="width:' + (l.ok ? 100 : 8) + '%;' +
        (l.ok ? '' : 'background:#fbbf24') + '"></div></div>' +
        '<span class="bar-value">' + (l.ok ? 'OK' : 'KO') + '</span></div>'
    ).join('');
}

document.getElementById('refresh').addEventListener('click', charger);
charger();
