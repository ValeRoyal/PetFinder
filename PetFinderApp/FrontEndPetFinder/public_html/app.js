const API_BASES = {
    adopter: "/api/adopters",
    pet: "/api/pets",
    match: "/api/matches",
    shelter: "/api/shelters",
    notification: "/api/notifications",
    veterinarian: "/veterinarians",
    vaccinationCard: "/vaccination-cards",
    vaccine: "/vaccines",
    medicalEvent: "/medical-events",
};

const state = {
    session: null,
    role: null,
    pets: [],
    matches: [],
    notifications: [],
    passedPetIds: new Set(),
    sheltersMap: {},
    sheltersByPetId: {},
    petMap: {},
    petCatalog: [],
    collapsedMatchIds: new Set(),
    deletedNotificationIds: new Set(),
    notificationPollTimer: null,
};

function byId(id) {
    return document.getElementById(id);
}

function splitCsv(text) {
    return (text || "")
        .split(",")
        .map((v) => v.trim())
        .filter(Boolean);
}

function setHtml(id, html) {
    const el = byId(id);
    if (el) el.innerHTML = html;
}

document.addEventListener("DOMContentLoaded", () => {
    updateRegisterForm();

    const stored = localStorage.getItem("petfinder_session");
    if (stored) {
        try {
            state.session = JSON.parse(stored);
            if (state.session?.role) {
                showDashboard(state.session.role);
                return;
            }
        } catch (err) {
            console.warn("Sesion invalida en storage", err);
            localStorage.removeItem("petfinder_session");
        }
    }

    loadAvailablePets();
});

function showModal(modalId) {
    const modal = byId(modalId);
    if (modal) modal.showModal();
}

function closeModal(modalId) {
    const modal = byId(modalId);
    if (modal) modal.close();
}

function switchModals(fromId, toId) {
    closeModal(fromId);
    showModal(toId);
}

function openAuthRegister(role) {
    byId("registerRole").value = role;
    byId("authRole").value = role;
    updateRegisterForm();
    showModal("registerModal");
}

function updateAuthForm() {
    // El login usa identificador unico por rol (ID o email para adoptante)
}

function updateRegisterForm() {
    const role = byId("registerRole").value;
    byId("adopterFields").style.display = role === "adopter" ? "block" : "none";
    byId("shelterFields").style.display = role === "shelter" ? "block" : "none";
    byId("vetFields").style.display = role === "veterinarian" ? "block" : "none";
}

async function handleLogin(event) {
    event.preventDefault();

    const role = byId("authRole").value;
    const authValue = byId("authId").value.trim();
    const password = byId("authPassword").value.trim();

    if (!authValue || !password) {
        alert("Ingresa tu ID/email y contraseña");
        return;
    }

    try {
        const user = await findUserForLogin(role, authValue, password);
        state.session = { ...user, role, id: user.id || authValue };
        localStorage.setItem("petfinder_session", JSON.stringify(state.session));

        await sendLoginNotification();
        showDashboard(role);
        closeModal("authModal");
    } catch (error) {
        alert("No se pudo iniciar sesion: " + error.message);
    }
}

async function findUserForLogin(role, authValue, password) {
    const loginEndpoints = {
        adopter: `${API_BASES.adopter}/login?identifier=${encodeURIComponent(authValue)}&password=${encodeURIComponent(password)}`,
        shelter: `${API_BASES.shelter}/login?identifier=${encodeURIComponent(authValue)}&password=${encodeURIComponent(password)}`,
        veterinarian: `${API_BASES.veterinarian}/login?identifier=${encodeURIComponent(authValue)}&password=${encodeURIComponent(password)}`,
    };

    return apiFetch(loginEndpoints[role], { method: "POST" });
}

async function handleRegister(event) {
    event.preventDefault();

    const role = byId("registerRole").value;
    const payload = buildRegisterPayload(role);

    const endpointByRole = {
        adopter: API_BASES.adopter,
        shelter: API_BASES.shelter,
        veterinarian: API_BASES.veterinarian,
    };

    try {
        const created = await apiFetch(endpointByRole[role], {
            method: "POST",
            body: JSON.stringify(payload),
        });

        const generatedId = created?.id || payload.id || "";
        state.session = { ...(created || payload), role, id: generatedId };
        localStorage.setItem("petfinder_session", JSON.stringify(state.session));

        if (generatedId) {
            alert(`✅ Cuenta creada correctamente. Tu ID automático es: ${generatedId}`);
        }

        showDashboard(role);
        closeModal("registerModal");
    } catch (error) {
        alert("No se pudo registrar: " + error.message);
    }
}

function buildRegisterPayload(role) {
    const base = {
        name: byId("regName").value.trim(),
        email: byId("regEmail").value.trim(),
        password: byId("regPassword").value.trim(),
        phone: byId("regPhone").value.trim(),
    };

    if (role === "adopter") {
        const minAge = Number(byId("regMinAge").value || 0);
        const maxAge = Number(byId("regMaxAge").value || 20);
        return {
            ...base,
            location: byId("regLocationAdopter").value.trim(),
            housing: byId("regHousing").value,
            hasKids: byId("regKids").value === "true",
            currentPets: splitCsv(byId("regCurrentPets").value),
            preferences: {
                preferredSpecies: ["Perro", "Gato"],
                minAge,
                maxAge: maxAge >= minAge ? maxAge : minAge,
                preferredSizes: splitCsv(byId("regPreferredSizes").value),
                energyMatch: byId("regEnergyMatch").value,
                kidsFriendly: byId("regKids").value === "true",
                otherPetsFriendly: byId("regOtherPetsFriendly").value === "true",
            },
        };
    }

    if (role === "shelter") {
        const photo = byId("regShelterPhoto").value.trim();
        const video = byId("regShelterVideo").value.trim();
        return {
            ...base,
            location: byId("regLocationShelter").value.trim(),
            photos: photo ? [photo] : [],
            videos: video ? [video] : [],
        };
    }

    return {
        name: base.name,
        specialty: byId("regSpecialty").value.trim(),
        phoneNumber: base.phone,
        email: byId("regVetEmail").value.trim() || base.email,
        password: base.password,
        shelterId: byId("regShelterId").value.trim(),
    };
}

function showDashboard(role) {
    byId("landingPage").style.display = "none";
    byId("dashboard").style.display = "grid";
    byId("userBadge").textContent = `${role} | ${state.session?.id || ""}`;
    state.role = role;

    byId("adopterPanel").style.display = role === "adopter" ? "block" : "none";
    byId("shelterPanel").style.display = role === "shelter" ? "block" : "none";
    byId("vetPanel").style.display = role === "veterinarian" ? "block" : "none";

    if (role === "adopter") {
        loadAdopterProfile();
        loadAvailablePets();
        loadMatches();
    }

    if (role === "shelter") {
        loadShelterPets();
    }

    if (role === "veterinarian" || role === "shelter" || role === "adopter") {
        loadPetCatalog();
    }

    loadNotifications();
    startNotificationPolling();
}

function logout() {
    state.session = null;
    state.role = null;
    state.pets = [];
    state.passedPetIds = new Set();
    state.sheltersMap = {};
    state.sheltersByPetId = {};
    state.petCatalog = [];
    state.collapsedMatchIds = new Set();
    state.deletedNotificationIds = new Set();
    stopNotificationPolling();
    localStorage.removeItem("petfinder_session");
    byId("dashboard").style.display = "none";
    byId("landingPage").style.display = "block";
    byId("notificationPanel").style.display = "none";
    byId("authForm").reset();
    byId("registerForm").reset();
}

async function loadPetCatalog() {
    try {
        const pets = await apiFetch(API_BASES.pet);
        state.petCatalog = Array.isArray(pets) ? pets : [];
        for (const pet of state.petCatalog) {
            if (pet?.id) state.petMap[pet.id] = pet;
        }
    } catch (error) {
        console.warn("No se pudo cargar catalogo de mascotas", error);
        state.petCatalog = [];
    }

    populatePetSelectors();
}

function populatePetSelectors() {
    const targetIds = ["vaccPetId", "vaccinePetId", "healthPetId", "cardLookupPetId", "shelterCardLookupPetId"];
    const options = state.petCatalog.length
        ? state.petCatalog.map((pet) => `<option value="${pet.id}">${pet.id} - ${pet.name || "Sin nombre"}</option>`).join("")
        : "<option value=''>No hay mascotas registradas</option>";

    for (const id of targetIds) {
        const select = byId(id);
        if (!select) continue;
        select.innerHTML = options;
        if (state.petCatalog.length) {
            select.value = state.petCatalog[0].id;
        }
    }

    setupPetSelectorBindings();
    syncVaccinationCardIdsFromPet();
}

function setupPetSelectorBindings() {
    ["vaccinePetId", "healthPetId"].forEach((id) => {
        const el = byId(id);
        if (!el || el.dataset.bound === "true") return;
        el.addEventListener("change", syncVaccinationCardIdsFromPet);
        el.dataset.bound = "true";
    });
}

function syncVaccinationCardIdsFromPet() {
    const vaccinePetId = byId("vaccinePetId")?.value || "";
    const healthPetId = byId("healthPetId")?.value || "";

    const vaccineCardInput = byId("vaccineCardId");
    if (vaccineCardInput) {
        vaccineCardInput.value = vaccinePetId;
    }

    const healthCardInput = byId("healthCardId");
    if (healthCardInput) {
        healthCardInput.value = healthPetId;
    }
}

function startNotificationPolling() {
    stopNotificationPolling();
    state.notificationPollTimer = setInterval(() => {
        loadNotifications();
    }, 20000);
}

function stopNotificationPolling() {
    if (state.notificationPollTimer) {
        clearInterval(state.notificationPollTimer);
        state.notificationPollTimer = null;
    }
}

// ─── Adopter Profile ───────────────────────────────────────────────────────────

async function loadAdopterProfile() {
    if (!state.session?.id) return;
    try {
        const profile = await apiFetch(`${API_BASES.adopter}/${encodeURIComponent(state.session.id)}`);
        state.session = { ...state.session, ...profile };
        localStorage.setItem("petfinder_session", JSON.stringify(state.session));
        renderProfileDisplay(profile);
        populateProfileForm(profile);
    } catch (err) {
        console.warn("No se pudo cargar el perfil del adoptante", err);
        setHtml("profileDisplayArea", "<p>No se pudo cargar el perfil.</p>");
    }
}

function renderProfileDisplay(profile) {
    const el = byId("profileDisplayArea");
    if (!el) return;
    const prefs = profile.preferences || {};
    el.innerHTML = `
        <div class="profile-grid">
            <div class="profile-field"><span class="profile-label">Nombre</span><span>${profile.name || "-"}</span></div>
            <div class="profile-field"><span class="profile-label">Email</span><span>${profile.email || "-"}</span></div>
            <div class="profile-field"><span class="profile-label">Teléfono</span><span>${profile.phone || "-"}</span></div>
            <div class="profile-field"><span class="profile-label">Ubicación</span><span>${profile.location || "-"}</span></div>
            <div class="profile-field"><span class="profile-label">Vivienda</span><span>${profile.housing || "-"}</span></div>
            <div class="profile-field"><span class="profile-label">¿Tiene hijos?</span><span>${profile.hasKids ? "Sí" : "No"}</span></div>
            <div class="profile-field"><span class="profile-label">Mascotas actuales</span><span>${(profile.currentPets || []).join(", ") || "Ninguna"}</span></div>
        </div>
        ${prefs ? `<div class="prefs-summary"><strong>Preferencias:</strong> especies: ${(prefs.preferredSpecies || []).join(", ") || "cualquiera"} · tallas: ${(prefs.preferredSizes || []).join(", ") || "cualquier"} · energía: ${prefs.energyMatch || "-"}</div>` : ""}
    `;
}

function populateProfileForm(profile) {
    const map = {
        editProfileName: profile.name,
        editProfileEmail: profile.email,
        editProfilePhone: profile.phone,
        editProfileLocation: profile.location,
        editProfileHousing: profile.housing,
        editProfileHasKids: String(profile.hasKids ?? false),
        editProfileCurrentPets: (profile.currentPets || []).join(", "),
    };
    for (const [id, value] of Object.entries(map)) {
        const el = byId(id);
        if (el) el.value = value || "";
    }
}

function toggleProfileEdit() {
    const form = byId("adopterProfileForm");
    const display = byId("profileDisplayArea");
    if (!form || !display) return;
    const isEditing = form.style.display !== "none";
    form.style.display = isEditing ? "none" : "block";
    display.style.display = isEditing ? "block" : "none";
}

async function saveAdopterProfile(event) {
    event.preventDefault();
    if (!state.session?.id) return;

    const payload = {
        id: state.session.id,
        name: byId("editProfileName").value.trim(),
        email: byId("editProfileEmail").value.trim(),
        phone: byId("editProfilePhone").value.trim() || "",
        location: byId("editProfileLocation").value.trim() || "",
        housing: byId("editProfileHousing").value,
        hasKids: byId("editProfileHasKids").value === "true",
        currentPets: splitCsv(byId("editProfileCurrentPets").value),
        preferences: state.session.preferences || {
            preferredSpecies: [],
            minAge: 0,
            maxAge: 20,
            preferredSizes: [],
            energyMatch: "MEDIO",
            kidsFriendly: true,
            otherPetsFriendly: true,
        },
    };

    try {
        const updated = await apiFetch(
            `${API_BASES.adopter}/${encodeURIComponent(state.session.id)}`,
            { method: "PUT", body: JSON.stringify(payload) }
        );
        state.session = { ...state.session, ...updated };
        localStorage.setItem("petfinder_session", JSON.stringify(state.session));
        renderProfileDisplay(updated);
        toggleProfileEdit();
        alert("✅ Perfil actualizado correctamente");
    } catch (err) {
        alert("No se pudo actualizar el perfil: " + err.message);
    }
}

// ─── Preferences ──────────────────────────────────────────────────────────────

async function savePreferences(event) {
    event.preventDefault();

    const preferredSpecies = splitCsv(byId("prefSpecies").value);
    const preferredSizes = [byId("prefSize").value];

    const preferences = {
        preferredSpecies,
        minAge: 0,
        maxAge: 20,
        preferredSizes,
        energyMatch: "MEDIO",
        kidsFriendly: true,
        otherPetsFriendly: true,
    };

    try {
        await apiFetch(`${API_BASES.adopter}/${encodeURIComponent(state.session.id)}/preferences`, {
            method: "PATCH",
            body: JSON.stringify(preferences),
        });

        state.session.preferences = preferences;
        localStorage.setItem("petfinder_session", JSON.stringify(state.session));
        alert("Preferencias guardadas");
    } catch (error) {
        alert("No se pudieron guardar preferencias: " + error.message);
    }
}

// ─── Shelter Map ──────────────────────────────────────────────────────────────

async function loadSheltersMap() {
    try {
        const shelters = await apiFetch(API_BASES.shelter);
        state.sheltersMap = {};
        state.sheltersByPetId = {};
        for (const shelter of shelters) {
            state.sheltersMap[shelter.id] = shelter;
            for (const petId of shelter.petProfileIds || []) {
                state.sheltersByPetId[petId] = shelter;
            }
        }
    } catch (err) {
        console.warn("No se pudo cargar mapa de refugios", err);
        state.sheltersMap = {};
        state.sheltersByPetId = {};
    }
}

// ─── Passed Pets Persistence ──────────────────────────────────────────────────

function getPassedPetIdsFromStorage() {
    if (!state.session?.id) return new Set();
    try {
        const raw = localStorage.getItem(`petfinder_passed_${state.session.id}`);
        return new Set(raw ? JSON.parse(raw) : []);
    } catch {
        return new Set();
    }
}

function persistPassedPetId(petId) {
    if (!state.session?.id) return;
    state.passedPetIds.add(petId);
    localStorage.setItem(
        `petfinder_passed_${state.session.id}`,
        JSON.stringify([...state.passedPetIds])
    );
}

// ─── Pet Discovery ────────────────────────────────────────────────────────────

async function loadAvailablePets() {
    try {
        state.passedPetIds = getPassedPetIdsFromStorage();
        await loadSheltersMap();

        const allPets = await apiFetch(`${API_BASES.pet}/available`);
        // Filter out pets the user already swiped on
        state.pets = (Array.isArray(allPets) ? allPets : []).filter(
            (pet) => !state.passedPetIds.has(pet.id)
        );
        loadNextPet();
    } catch (error) {
        console.error("Error cargando mascotas:", error);
        state.pets = [];
        loadNextPet();
    }
}

function loadNextPet() {
    const petCard = byId("petCard");
    const swipeButtons = byId("swipeButtons");
    if (!petCard) return;

    if (!state.pets.length) {
        petCard.innerHTML = `
            <div class="no-pets-msg">
                <span class="no-pets-icon">🐾</span>
                <p>¡Has visto todas las mascotas disponibles!</p>
                <p>Vuelve pronto para ver nuevos peludos.</p>
                <button class="btn btn-outline" onclick="loadAvailablePets()">Recargar mascotas</button>
            </div>`;
        if (swipeButtons) swipeButtons.style.display = "none";
        return;
    }

    if (swipeButtons) swipeButtons.style.display = "flex";

    // Restore card structure if it was replaced by the empty message
    if (!byId("petName")) {
        petCard.innerHTML = `
            <img id="petImage" src="" alt="Mascota">
            <div class="pet-info">
                <h3 id="petName"></h3>
                <p id="petDetails"></p>
                <p id="petBio"></p>
            </div>`;
    }

    const pet = state.pets[0];
    byId("petName").textContent = pet.name || "Sin nombre";
    byId("petDetails").textContent = `${pet.species || "Mascota"} · ${pet.age ?? "?"} años · ${pet.size || "-"}`;
    byId("petBio").textContent = pet.bio || "Sin descripcion";
    byId("petImage").src = pet.photos?.[0] || pet.image || "https://via.placeholder.com/300x200";
}

async function executeSwipe(direction) {
    if (!state.session?.id || !state.pets.length) return;

    const pet = state.pets[0];

    try {
        await apiFetch(
            `${API_BASES.adopter}/${encodeURIComponent(state.session.id)}/swipes/${direction}`,
            { method: "POST", body: JSON.stringify({ id: `SWIPE-${Date.now()}`, petProfileId: pet.id }) }
        );

        // Mark pet as seen so it never shows again
        persistPassedPetId(pet.id);
        state.pets.shift();

        if (direction === "right") {
            // Create match and refresh match list
            const shelter = state.sheltersByPetId?.[pet.id] || null;
            await createMatchForPet(pet, shelter);
            await loadMatches();
        }

        loadNextPet();
    } catch (error) {
        alert("No se pudo registrar swipe: " + error.message);
    }
}

async function createMatchForPet(pet, shelter) {
    try {
        const created = await apiFetch(`${API_BASES.match}/swipes`, {
            method: "POST",
            body: JSON.stringify({
                id: `MATCH-${Date.now()}`,
                adopterId: state.session.id,
                petProfileId: pet.id,
                shelterId: shelter?.id || "",
                score: 0.75,
                shelterApproves: true,
            }),
        });

        await publishNotificationEvent(
            "MATCH_CREATED",
            created?.id || `MATCH-${pet.id}`,
            `Tienes un nuevo match con ${pet.name || "una mascota"}.`
        );
    } catch (err) {
        console.warn("No se pudo crear match automáticamente:", err);
    }
}

// ─── Matches ──────────────────────────────────────────────────────────────────

async function loadMatches() {
    if (!state.session?.id) return;

    try {
        state.collapsedMatchIds = getCollapsedMatchIdsFromStorage();
        const matches = await apiFetch(
            `${API_BASES.match}/adopters/${encodeURIComponent(state.session.id)}`
        );
        state.matches = matches;
        await renderMatches(matches);
    } catch (error) {
        console.warn("No se pudieron cargar matches", error);
        await renderMatches([]);
    }
}

async function renderMatches(matches) {
    const container = byId("matchesList");
    if (!container) return;

    if (!matches.length) {
        container.innerHTML = "<p class='empty-state'>Aún no tienes matches. Sigue deslizando mascotas. 🐾</p>";
        return;
    }

    // Enrich matches with shelter and pet info to render user-friendly cards.
    const enriched = await Promise.all(
        matches.map(async (m) => {
            let shelter = m.shelterId ? (state.sheltersMap[m.shelterId] || null) : null;
            let pet = m.petProfileId ? (state.petMap[m.petProfileId] || null) : null;
            if (!shelter && m.shelterId) {
                try {
                    shelter = await apiFetch(`${API_BASES.shelter}/${encodeURIComponent(m.shelterId)}`);
                    if (shelter?.id) state.sheltersMap[shelter.id] = shelter;
                } catch {
                    shelter = null;
                }
            }
            if (!pet && m.petProfileId) {
                try {
                    pet = await apiFetch(`${API_BASES.pet}/${encodeURIComponent(m.petProfileId)}`);
                    if (pet?.id) state.petMap[pet.id] = pet;
                } catch {
                    pet = null;
                }
            }
            return { ...m, shelter, pet };
        })
    );

    container.innerHTML = enriched
        .map((m) => {
            const shelter = m.shelter;
            const petName = m.pet?.name || m.petProfileId || "Mascota";
            const isCollapsed = state.collapsedMatchIds.has(m.id);
            const statusClass = (m.status || "PENDING").toLowerCase();
            const statusLabel = {
                MUTUAL: "✅ Match mutuo",
                PENDING: "⏳ Pendiente",
                REJECTED: "❌ Rechazado",
                EXPIRED: "⏰ Expirado",
            }[m.status] || m.status;
            const shouldShowMessage = m.message && m.message !== "Waiting for mutual approval or minimum score.";

            if (isCollapsed) {
                return `
                <article class="match-card match-card--collapsed">
                    <div class="match-card-header">
                        <h3>🐾 ${petName}</h3>
                        <span class="match-status match-status--${statusClass}">${statusLabel}</span>
                    </div>
                    <div class="match-actions">
                        <button type="button" class="btn btn-secondary" onclick="expandMatchCard('${m.id}')">Abrir tarjeta</button>
                    </div>
                </article>`;
            }

            return `
            <article class="match-card">
                <div class="match-card-header">
                    <h3>🐾 ${petName}</h3>
                    <span class="match-status match-status--${statusClass}">${statusLabel}</span>
                </div>
                ${
                    shelter
                        ? `<div class="match-shelter-info">
                            <h4>📍 Contacto del refugio</h4>
                            <p class="shelter-name">${shelter.name}</p>
                            <div class="shelter-contact-grid">
                                <span>📧 <a href="mailto:${shelter.email}">${shelter.email}</a></span>
                                <span>📞 ${shelter.phone}</span>
                                <span>📌 ${shelter.location}</span>
                            </div>
                        </div>`
                        : m.shelterId
                        ? `<p class="match-shelter-id">Refugio ID: ${m.shelterId}</p>`
                        : ""
                }
                <div class="match-meta">
                    ${m.matchScore != null ? `<span>Score: ${m.matchScore}</span>` : ""}
                    ${shouldShowMessage ? `<p class="match-message">${m.message}</p>` : ""}
                </div>
                <div class="match-actions">
                    <button type="button" class="btn btn-secondary" onclick="viewMatchVaccinationCard('${m.id}', '${m.petProfileId || ""}')">Ver carnet</button>
                    <button type="button" class="btn btn-outline" onclick="hideMatchCard('${m.id}')">Ocultar tarjeta</button>
                </div>
                <div id="matchCardVaccination-${m.id}" class="match-vaccination-inline"></div>
            </article>`;
        })
        .join("");
}

function getCollapsedMatchIdsFromStorage() {
    if (!state.session?.id) return new Set();
    try {
        const raw = localStorage.getItem(`petfinder_collapsed_matches_${state.session.id}`);
        return new Set(raw ? JSON.parse(raw) : []);
    } catch {
        return new Set();
    }
}

function persistCollapsedMatchIds() {
    if (!state.session?.id) return;
    localStorage.setItem(
        `petfinder_collapsed_matches_${state.session.id}`,
        JSON.stringify([...state.collapsedMatchIds])
    );
}

async function hideMatchCard(matchId) {
    if (!matchId) return;
    state.collapsedMatchIds.add(matchId);
    persistCollapsedMatchIds();
    await renderMatches(state.matches || []);
    await publishNotificationEvent("GENERAL", `HIDE-${matchId}`, "Ocultaste una tarjeta de match.");
}

async function expandMatchCard(matchId) {
    if (!matchId) return;
    state.collapsedMatchIds.delete(matchId);
    persistCollapsedMatchIds();
    await renderMatches(state.matches || []);
}

// ─── Shelter Panel ────────────────────────────────────────────────────────────

async function addPet(event) {
    event.preventDefault();

    const petImageInput = byId("petImage2");
    const petImageFile = petImageInput?.files?.[0] || null;
    let uploadedPhotoUrl = petImageInput?.dataset?.uploadedUrl || "";

    if (petImageFile && !uploadedPhotoUrl) {
        uploadedPhotoUrl = await uploadPetImage(petImageFile);
        if (petImageInput) {
            petImageInput.dataset.uploadedUrl = uploadedPhotoUrl;
        }
    }

    const petData = {
        id: byId("petId").value.trim(),
        name: byId("petName2").value.trim(),
        species: byId("petSpecies").value,
        breed: byId("petBreed").value.trim(),
        age: Number(byId("petAge").value || 0),
        sex: byId("petSex").value,
        size: byId("petSize").value,
        energyLevel: byId("petEnergyLevel").value,
        kidsCompatible: byId("petKidsCompatible").value === "true",
        otherPetsCompatible: byId("petOtherPetsCompatible").value === "true",
        photos: uploadedPhotoUrl ? [uploadedPhotoUrl] : [],
        bio: byId("petBio2").value.trim(),
    };

    await savePet(petData);
}

async function savePet(petData) {
    try {
        const created = await apiFetch(API_BASES.pet, {
            method: "POST",
            body: JSON.stringify(petData),
        });

        if (state.session?.role === "shelter" && state.session?.id) {
            try {
                await apiFetch(`${API_BASES.shelter}/${encodeURIComponent(state.session.id)}/pets`, {
                    method: "POST",
                    body: JSON.stringify({ value: created?.id || petData.id }),
                });
            } catch (linkError) {
                console.warn("Mascota creada pero no vinculada al refugio", linkError);
            }
        }

        alert("Mascota registrada exitosamente");
        byId("petForm").reset();
        setHtml("imagePreview", "");
        const imageInput = byId("petImage2");
        if (imageInput) {
            delete imageInput.dataset.uploadedUrl;
            imageInput.value = "";
        }
        await loadShelterPets();
        await loadPetCatalog();
    } catch (error) {
        alert("Error al registrar mascota: " + error.message);
    }
}

async function previewImage(event) {
    const file = event.target.files[0];
    if (!file) return;

    delete event.target.dataset.uploadedUrl;

    const reader = new FileReader();
    reader.onload = (e) => {
        setHtml("imagePreview", `<img src="${e.target.result}" alt="Preview">`);
    };
    reader.readAsDataURL(file);

    try {
        event.target.dataset.uploadedUrl = await uploadPetImage(file);
    } catch (error) {
        alert("No se pudo subir la imagen: " + error.message);
    }
}

async function uploadPetImage(file) {
    const formData = new FormData();
    formData.append("image", file);

    const response = await fetch("/uploads", {
        method: "POST",
        body: formData,
    });

    if (!response.ok) {
        const message = await response.text().catch(() => response.statusText || "Error");
        throw new Error(`HTTP ${response.status}: ${message}`);
    }

    const payload = await response.json();
    if (!payload?.url) {
        throw new Error("Respuesta de subida invalida");
    }
    return payload.url;
}

async function loadShelterPets() {
    const target = byId("shelterPetsList");
    if (!target) return;

    try {
        let pets = [];
        if (state.session?.id) {
            const shelter = await apiFetch(`${API_BASES.shelter}/${encodeURIComponent(state.session.id)}`);
            const ids = shelter.petProfileIds || [];
            if (ids.length) {
                pets = await Promise.all(
                    ids.map((id) => apiFetch(`${API_BASES.pet}/${encodeURIComponent(id)}`))
                );
            }
        }

        if (!pets.length) {
            pets = await apiFetch(`${API_BASES.pet}/available`);
        }

        target.innerHTML = pets
            .map(
                (pet) => `
                <article class="pet-item">
                    <img src="${pet.photos?.[0] || "https://via.placeholder.com/300"}" alt="${pet.name || "Mascota"}">
                    <div class="pet-item-content">
                        <h3>${pet.name || "Sin nombre"}</h3>
                        <p>${pet.species || "-"} - ${pet.breed || "-"}</p>
                        <p>Edad: ${pet.age ?? "-"} | Tamaño: ${pet.size || "-"}</p>
                        <p>${pet.bio || "Sin descripcion"}</p>
                    </div>
                </article>
            `
            )
            .join("");
    } catch (error) {
        target.innerHTML = `<p>No se pudieron cargar mascotas: ${error.message}</p>`;
    }
}

// ─── Veterinarian Panel ───────────────────────────────────────────────────────

async function createVaccinationCard(event) {
    event.preventDefault();

    const petProfileId = byId("vaccPetId").value.trim();
    if (!petProfileId) return;

    try {
        await ensureVaccinationCardExists(petProfileId);
        alert("Carnet creado correctamente");
    } catch (error) {
        alert("No se pudo crear el carnet: " + error.message);
    }
}

async function addVaccine(event) {
    event.preventDefault();
    syncVaccinationCardIdsFromPet();
    const petProfileId = byId("vaccinePetId").value.trim();
    if (!petProfileId) {
        alert("Selecciona una mascota");
        return;
    }

    await ensureVaccinationCardExists(petProfileId);

    const payload = {
        id: byId("vaccineId").value.trim(),
        name: byId("vaccineName").value.trim(),
        appliedDate: byId("vaccineAppliedDate").value,
        nextDueDate: byId("vaccineNextDueDate").value || null,
        administeredById: byId("vaccineAdminVetId").value.trim(),
        vaccinationCardId: byId("vaccineCardId").value.trim(),
    };

    try {
        await apiFetch(API_BASES.vaccine, {
            method: "POST",
            body: JSON.stringify(payload),
        });
        alert("Vacuna registrada correctamente");
        event.target.reset();
    } catch (error) {
        alert("No se pudo registrar vacuna: " + error.message);
    }
}

async function addHealthRecord(event) {
    event.preventDefault();
    syncVaccinationCardIdsFromPet();
    const petProfileId = byId("healthPetId").value.trim();
    if (!petProfileId) {
        alert("Selecciona una mascota");
        return;
    }

    await ensureVaccinationCardExists(petProfileId);

    const payload = {
        id: `MED-${Date.now()}`,
        petProfileId: byId("healthPetId").value.trim(),
        date: byId("healthDate").value,
        eventType: byId("healthType").value,
        title: byId("healthTitle").value.trim(),
        description: byId("healthDescription").value.trim(),
        vetNotes: byId("healthVetNotes").value.trim() || null,
        registeredById: state.session?.id || "",
        registeredByRole: "VET",
        vaccinationCardId: byId("healthCardId").value.trim(),
    };

    try {
        await apiFetch(API_BASES.medicalEvent, {
            method: "POST",
            body: JSON.stringify(payload),
        });

        alert("Evento medico guardado");
        event.target.reset();
    } catch (error) {
        alert("No se pudo guardar evento medico: " + error.message);
    }
}

async function ensureVaccinationCardExists(petId) {
    try {
        return await apiFetch(`${API_BASES.vaccinationCard}/${encodeURIComponent(petId)}`);
    } catch (error) {
        const message = String(error.message || "");
        if (message.includes("HTTP 404") || message.includes("HTTP 500")) {
            try {
                await apiFetch(API_BASES.vaccinationCard, {
                    method: "POST",
                    body: JSON.stringify({ petProfileId: petId }),
                });
            } catch (createError) {
                const createMessage = String(createError.message || "");
                if (!createMessage.includes("Ya existe un carnet")) {
                    throw createError;
                }
            }
            return apiFetch(`${API_BASES.vaccinationCard}/${encodeURIComponent(petId)}`);
        }
        throw error;
    }
}

function renderVaccinationCard(card) {
    const vaccines = card.vaccines || [];
    const events = card.medicalEvents || [];

    return `
        <article class="section">
            <h3>Carnet de mascota: ${card.petProfileId}</h3>
            <p>Proxima fecha sugerida: ${card.nextDueDate || "Sin fecha"}</p>
            <h4>Vacunas</h4>
            <ul>
                ${
                    vaccines.length
                        ? vaccines
                              .map(
                                  (v) =>
                                      `<li>${v.name} - aplicada: ${v.appliedDate || "-"} - proxima: ${v.nextDueDate || "-"}</li>`
                              )
                              .join("")
                        : "<li>Sin vacunas registradas</li>"
                }
            </ul>
            <h4>Eventos medicos</h4>
            <ul>
                ${
                    events.length
                        ? events
                              .map(
                                  (e) =>
                                      `<li>${e.date || "-"} | ${e.eventType || "-"} | ${e.title || "-"} | ${e.description || ""}</li>`
                              )
                              .join("")
                        : "<li>Sin eventos registrados</li>"
                }
            </ul>
        </article>
    `;
}

async function loadVaccinationCard(event) {
    event.preventDefault();

    const petId = byId("cardLookupPetId").value.trim();
    if (!petId) return;

    const list = byId("vaccinationCardsList");
    try {
        const card = await ensureVaccinationCardExists(petId);
        list.innerHTML = renderVaccinationCard(card);
    } catch (error) {
        list.innerHTML = `<p>No se pudo consultar carnet: ${error.message}</p>`;
    }
}

async function loadShelterVaccinationCard(event) {
    event.preventDefault();
    const petId = byId("shelterCardLookupPetId")?.value?.trim();
    const list = byId("shelterVaccinationCardsList");
    if (!petId || !list) return;

    try {
        const card = await ensureVaccinationCardExists(petId);
        list.innerHTML = renderVaccinationCard(card);
    } catch (error) {
        list.innerHTML = `<p>No se pudo consultar carnet: ${error.message}</p>`;
    }
}

async function viewMatchVaccinationCard(matchId, petId) {
    const container = byId(`matchCardVaccination-${matchId}`);
    if (!container || !petId) return;

    try {
        if (container.innerHTML.trim()) {
            container.innerHTML = "";
            return;
        }

        const card = await ensureVaccinationCardExists(petId);
        container.innerHTML = renderVaccinationCard(card);
    } catch (error) {
        container.innerHTML = `<p class="empty-state">No se pudo consultar carnet: ${error.message}</p>`;
    }
}

// ─── Notifications ────────────────────────────────────────────────────────────

async function sendLoginNotification() {
    if (!state.session?.id) return;

    try {
        await publishNotificationEvent(
            "LOGIN",
            `LOGIN-${Date.now()}`,
            `Bienvenido(a) ${state.session.name || state.session.id}`
        );
    } catch (error) {
        console.warn("No se pudo registrar notificacion de login", error);
    }
}

async function publishNotificationEvent(type, sourceEventId, contentText) {
    if (!state.session?.id) return;
    try {
        await apiFetch(`${API_BASES.notification}/events`, {
            method: "POST",
            body: JSON.stringify({
                id: `NTF-${Date.now()}-${Math.floor(Math.random() * 10000)}`,
                sourceService: "frontend",
                sourceEventId,
                recipientId: state.session.id,
                recipientEmail: state.session.email || "",
                type,
                channel: "IN_APP",
                subject: "",
                content: contentText || "",
            }),
        });

        await apiFetch(`${API_BASES.notification}/process-pending`, {
            method: "POST",
        });
        await loadNotifications();
    } catch (error) {
        console.warn("No se pudo publicar evento de notificacion", error);
    }
}

async function loadNotifications() {
    if (!state.session?.id) return;

    try {
        state.deletedNotificationIds = getDeletedNotificationIdsFromStorage();
        const list = await apiFetch(`${API_BASES.notification}/recipients/${encodeURIComponent(state.session.id)}`);
        state.notifications = list.filter((n) => !state.deletedNotificationIds.has(n.id));
        byId("notificationCount").textContent = String(state.notifications.filter((n) => n.status !== "READ").length);
        renderNotifications();
    } catch (error) {
        console.warn("No se pudieron cargar notificaciones", error);
    }
}

function getDeletedNotificationIdsFromStorage() {
    if (!state.session?.id) return new Set();
    try {
        const raw = localStorage.getItem(`petfinder_deleted_notifications_${state.session.id}`);
        return new Set(raw ? JSON.parse(raw) : []);
    } catch {
        return new Set();
    }
}

function persistDeletedNotificationIds() {
    if (!state.session?.id) return;
    localStorage.setItem(
        `petfinder_deleted_notifications_${state.session.id}`,
        JSON.stringify([...state.deletedNotificationIds])
    );
}

function renderNotifications() {
    const panel = byId("notificationPanel");
    if (!panel) return;

    if (!state.notifications.length) {
        panel.innerHTML = "<div class='notification-item'>Sin notificaciones</div>";
        return;
    }

    panel.innerHTML = state.notifications
        .map(
            (n) => `
            <article class="notification-item">
                <strong>${n.subject || "Notificacion"}</strong>
                <p>${n.content || ""}</p>
                <small>${n.status || "-"}</small>
                <div class="notification-actions">
                    ${n.status !== "READ" ? `<button class="btn btn-small" onclick="markNotificationRead('${n.id}')">Marcar como leida</button>` : ""}
                    <button class="btn btn-small btn-danger" onclick="deleteNotification('${n.id}')">Eliminar</button>
                </div>
            </article>
        `
        )
        .join("");
}

async function markNotificationRead(notificationId) {
    try {
        await apiFetch(`${API_BASES.notification}/${encodeURIComponent(notificationId)}/read`, {
            method: "PATCH",
        });
        await loadNotifications();
    } catch (error) {
        alert("No se pudo marcar notificacion: " + error.message);
    }
}

async function deleteNotification(notificationId) {
    try {
        await apiFetch(`${API_BASES.notification}/${encodeURIComponent(notificationId)}`, {
            method: "DELETE",
        });
    } catch (error) {
        console.warn("No se pudo eliminar en backend, se oculta en frontend", error);
    }

    state.deletedNotificationIds.add(notificationId);
    persistDeletedNotificationIds();
    await loadNotifications();
}

function toggleNotifications() {
    const panel = byId("notificationPanel");
    panel.style.display = panel.style.display === "none" ? "block" : "none";
    if (panel.style.display === "block") {
        loadNotifications();
    }
}

// ─── API Helper ───────────────────────────────────────────────────────────────

async function apiFetch(url, options = {}) {
    const method = (options.method || "GET").toUpperCase();
    const headers = { ...(options.headers || {}) };

    if (options.body !== undefined && options.body !== null && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/json";
    }

    const response = await fetch(url, {
        ...options,
        method,
        headers,
    });

    if (!response.ok) {
        const message = await response.text().catch(() => response.statusText || "Error");
        throw new Error(`HTTP ${response.status}: ${message}`);
    }

    if (response.status === 204) return null;

    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        return response.json();
    }

    return response.text();
}

// ─── Window exports ───────────────────────────────────────────────────────────

window.showModal = showModal;
window.closeModal = closeModal;
window.switchModals = switchModals;
window.openAuthRegister = openAuthRegister;
window.updateAuthForm = updateAuthForm;
window.updateRegisterForm = updateRegisterForm;
window.handleLogin = handleLogin;
window.handleRegister = handleRegister;
window.logout = logout;
window.savePreferences = savePreferences;
window.executeSwipe = executeSwipe;
window.addPet = addPet;
window.previewImage = previewImage;
window.toggleNotifications = toggleNotifications;
window.addHealthRecord = addHealthRecord;
window.createVaccinationCard = createVaccinationCard;
window.addVaccine = addVaccine;
window.loadVaccinationCard = loadVaccinationCard;
window.markNotificationRead = markNotificationRead;
window.toggleProfileEdit = toggleProfileEdit;
window.saveAdopterProfile = saveAdopterProfile;
window.loadAvailablePets = loadAvailablePets;
window.hideMatchCard = hideMatchCard;
window.expandMatchCard = expandMatchCard;
window.viewMatchVaccinationCard = viewMatchVaccinationCard;
window.loadShelterVaccinationCard = loadShelterVaccinationCard;
window.deleteNotification = deleteNotification;
