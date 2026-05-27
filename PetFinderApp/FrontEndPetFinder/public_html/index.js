const API_BASES = {
  adopter: 'http://localhost:8083/api/adopters',
  shelter: 'http://localhost:8082/api/shelters',
  veterinarian: 'http://localhost:8085/veterinarians'
};

const modal = document.querySelector('#authModal');
const form = document.querySelector('#authForm');
const roleSelect = document.querySelector('#roleSelect');
const statusEl = document.querySelector('#formStatus');
const modeLabel = document.querySelector('#authModeLabel');
const title = document.querySelector('#authTitle');
const submitBtn = document.querySelector('.boton-submit');

let currentMode = 'register';

document.querySelectorAll('[data-open-auth]').forEach((button) => {
  button.addEventListener('click', () => {
    openAuth(button.dataset.openAuth, button.dataset.role || 'adopter');
  });
});

document.querySelectorAll('[data-close-auth]').forEach((button) => {
  button.addEventListener('click', () => modal.close());
});

roleSelect.addEventListener('change', () => updateRoleFields(roleSelect.value));

modal.addEventListener('cancel', (e) => {
  e.preventDefault();
  modal.close();
});

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  clearStatus();

  const data = Object.fromEntries(new FormData(form));
  const payload = buildPayload(data, currentMode);

  try {
    const response = await fetch(resolveEndpoint(data.role, currentMode), {
      method: currentMode === 'login' ? 'GET' : 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: currentMode === 'login' ? undefined : JSON.stringify(payload)
    });

    if (!response.ok) {
      throw new Error(`El servidor respondio ${response.status}`);
    }

    showStatus(
      currentMode === 'login'
        ? 'Sesión validada correctamente.'
        : 'Registro enviado correctamente.',
      'ok'
    );

    // Si quieres cerrar automático al éxito:
    // setTimeout(() => modal.close(), 700);
  } catch (error) {
    showStatus('No se pudo conectar con el servicio. Revisa que el microservicio esté levantado.', 'error');
  }
});

function openAuth(mode = 'register', role = 'adopter') {
  currentMode = mode;
  form.reset();

  roleSelect.value = role;
  updateRoleFields(role);
  clearStatus();

  const isLogin = mode === 'login';
  modeLabel.textContent = isLogin ? 'Inicio de sesión' : 'Registro';
  title.textContent = isLogin ? 'Iniciar sesión' : 'Crear cuenta';
  submitBtn.textContent = isLogin ? 'Entrar' : 'Continuar';

  document.querySelectorAll('.solo-registro').forEach((field) => {
    field.classList.toggle('oculto', isLogin);
  });

  updateRequiredFields(role, isLogin);
  modal.showModal();
}

function updateRoleFields(role) {
  document.querySelectorAll('.solo-adopter, .solo-shelter, .solo-veterinarian').forEach((field) => {
    field.classList.add('oculto');
  });

  if (currentMode !== 'login') {
    document.querySelectorAll(`.solo-${role}`).forEach((field) => {
      field.classList.remove('oculto');
    });
  }

  updateRequiredFields(role, currentMode === 'login');
}

function updateRequiredFields(role, isLogin) {
  setRequired('#name', !isLogin);
  setRequired('#phone', !isLogin);
  setRequired('#location', !isLogin && role === 'shelter');
  setRequired('#specialty', !isLogin && role === 'veterinarian');
  setRequired('#shelterId', !isLogin && role === 'veterinarian');
  setRequired('#housing', !isLogin && role === 'adopter');
}

function setRequired(selector, required) {
  const field = document.querySelector(selector);
  if (field) field.required = required;
}

function buildPayload(data, mode) {
  if (mode === 'login') {
    return { id: data.id, email: data.email, role: data.role };
  }

  if (data.role === 'shelter') {
    return {
      id: data.id,
      name: data.name,
      location: data.location,
      email: data.email,
      phone: data.phone,
      photos: [],
      videos: []
    };
  }

  if (data.role === 'veterinarian') {
    return {
      id: data.id,
      name: data.name,
      specialty: data.specialty,
      phoneNumber: data.phone,
      email: data.email,
      shelterId: data.shelterId
    };
  }

  return {
    id: data.id,
    name: data.name,
    email: data.email,
    phone: data.phone,
    location: data.location || 'Sin ubicación',
    housing: data.housing || 'No especificado',
    hasKids: false,
    currentPets: [],
    preferences: null
  };
}

function resolveEndpoint(role, mode) {
  if (mode === 'login') {
    return `${API_BASES[role]}/${encodeURIComponent(document.querySelector('#userId').value)}`;
  }
  return API_BASES[role];
}

function showStatus(message, type) {
  statusEl.textContent = message;
  statusEl.className = `estado-formulario ${type}`;
}

function clearStatus() {
  statusEl.textContent = '';
  statusEl.className = 'estado-formulario';
}