[CmdletBinding()]
param(
    [string]$AdopterId = "ADOPTER-001",
    [string]$ShelterId1 = "SHELTER-001",
    [string]$ShelterId2 = "SHELTER-002",
    [string]$VetId = "VET-001",
    [string]$AdopterPassword = "1234",
    [string]$ShelterPassword = "1234",
    [string]$VetPassword = "1234",
    [string]$PetId1 = "PET-001",
    [string]$PetId2 = "PET-002",
    [string]$PetId3 = "PET-003",
    [string]$PetId4 = "PET-004",
    [string]$PetId5 = "PET-005",
    [string]$PetId6 = "PET-006",
    [switch]$DryRun
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$base = @{
    Adopter = "http://127.0.0.1:8083/api/adopters"
    Shelter = "http://127.0.0.1:8082/api/shelters"
    Pet = "http://127.0.0.1:8081/api/pets"
    Vet = "http://127.0.0.1:8085"
    Notification = "http://127.0.0.1:8084/api/notifications"
    Match = "http://127.0.0.1:8086/api/matches"
}

function Invoke-JsonApi {
    param(
        [Parameter(Mandatory)] [string]$Url,
        [Parameter(Mandatory)] [ValidateSet('GET','POST','PUT','PATCH')] [string]$Method,
        [object]$Body
    )

    if ($DryRun) {
        Write-Host "[DRYRUN] $Method $Url" -ForegroundColor Cyan
        if ($null -ne $Body) {
            ($Body | ConvertTo-Json -Depth 10) | Write-Host
        }
        return $null
    }

    $params = @{
        Uri = $Url
        Method = $Method
        ContentType = 'application/json'
        ErrorAction = 'Stop'
    }

    if ($null -ne $Body) {
        $params.Body = ($Body | ConvertTo-Json -Depth 10)
    }

    return Invoke-RestMethod @params
}

function Write-Step([string]$msg) {
    Write-Host "`n==> $msg" -ForegroundColor Yellow
}

$created = [ordered]@{}

try {
    Write-Step "Creando refugios de prueba"
    $created.shelters = New-Object System.Collections.ArrayList
    $shelterSeeds = @(
        @{
            defaultId = $ShelterId1
            name = "Refugio Adoptame Bogotá"
            location = "Bogotá, Chapinero"
            email = "shelter1@petfinder.test"
            phone = "099000111"
            photos = @("uploads/shelter1.jpg")
            videos = @("https://youtu.be/5b5X_Xex0xw?si=jZJJkLRlTrubusy-")
        },
        @{
            defaultId = $ShelterId2
            name = "Refugio Huellitas Unidas"
            location = "Chía, Cundinamarca"
            email = "shelter2@petfinder.test"
            phone = "099000222"
            photos = @("uploads/shelter2.jpg")
            videos = @("https://www.youtube.com/watch?v=T5vhiWswLVA")
        }
    )

    foreach ($shelterSeed in $shelterSeeds) {
        $createdShelter = Invoke-JsonApi -Method POST -Url $base.Shelter -Body @{
            name = $shelterSeed.name
            location = $shelterSeed.location
            email = $shelterSeed.email
            password = $ShelterPassword
            phone = $shelterSeed.phone
            photos = $shelterSeed.photos
            videos = $shelterSeed.videos
        }

        $resolvedShelterId = $shelterSeed.defaultId
        if (-not $DryRun -and $null -ne $createdShelter -and $createdShelter.id) {
            $resolvedShelterId = $createdShelter.id
        }

        [void]$created.shelters.Add([PSCustomObject]@{
            id = $resolvedShelterId
            name = $shelterSeed.name
            email = $shelterSeed.email
        })
    }

    $ShelterId1 = $created.shelters[0].id
    $ShelterId2 = $created.shelters[1].id

    Write-Step "Creando adoptante de prueba"
    $created.adopter = Invoke-JsonApi -Method POST -Url $base.Adopter -Body @{
        name = "Ana Adoptante"
        email = "ana.adoptante@petfinder.test"
        password = $AdopterPassword
        phone = "099123123"
        location = "Bogotá"
        housing = "Casa"
        hasKids = $true
        currentPets = @("Gato")
        preferences = @{
            preferredSpecies = @("Perro", "Gato")
            minAge = 1
            maxAge = 8
            preferredSizes = @("Mediano", "Grande")
            energyMatch = "MEDIO"
            kidsFriendly = $true
            otherPetsFriendly = $true
        }
    }
    if (-not $DryRun -and $null -ne $created.adopter -and $created.adopter.id) {
        $AdopterId = $created.adopter.id
    }

    Write-Step "Creando veterinario de prueba"
    $created.vet = Invoke-JsonApi -Method POST -Url "$($base.Vet)/veterinarians" -Body @{
        name = "Dr. Mateo Salud"
        specialty = "Medicina felina"
        phoneNumber = "098765432"
        email = "mateo.vet@petfinder.test"
        password = $VetPassword
        shelterId = $ShelterId1
    }
    if (-not $DryRun -and $null -ne $created.vet -and $created.vet.id) {
        $VetId = $created.vet.id
    }

    Write-Step "Creando mascotas de prueba (3 por refugio)"
    $petSeeds = @(
        @{ id = $PetId1; shelterId = $ShelterId1; name = "Luna"; species = "DOG"; breed = "Golden Retriever"; age = 3; sex = "HEMBRA"; size = "Grande"; energyLevel = "ALTO"; kidsCompatible = $true; otherPetsCompatible = $true; photos = @("uploads/golden.png"); bio = "Alegre, sociable y lista para adopcion." },
        @{ id = $PetId2; shelterId = $ShelterId1; name = "Milo"; species = "CAT"; breed = "Siames"; age = 2; sex = "MACHO"; size = "Pequeño"; energyLevel = "MEDIO"; kidsCompatible = $true; otherPetsCompatible = $false; photos = @("uploads/siames.jpg"); bio = "Tranquilo, carinoso y perfecto para interiores." },
        @{ id = $PetId3; shelterId = $ShelterId1; name = "Toby"; species = "DOG"; breed = "Labrador"; age = 4; sex = "MACHO"; size = "Mediano"; energyLevel = "MEDIO"; kidsCompatible = $true; otherPetsCompatible = $true; photos = @("uploads/labrador.jpg"); bio = "Amigable, obediente y companero ideal para familia." },
        @{ id = $PetId4; shelterId = $ShelterId2; name = "Nina"; species = "CAT"; breed = "Europeo"; age = 1; sex = "HEMBRA"; size = "Pequeño"; energyLevel = "BAJO"; kidsCompatible = $true; otherPetsCompatible = $true; photos = @("uploads/europeo.jpg"); bio = "Dulce y tranquila, ideal para apartamento." },
        @{ id = $PetId5; shelterId = $ShelterId2; name = "Rocky"; species = "DOG"; breed = "Mestizo"; age = 5; sex = "MACHO"; size = "Grande"; energyLevel = "ALTO"; kidsCompatible = $false; otherPetsCompatible = $true; photos = @("uploads/mestizo.jpg"); bio = "Activo y leal, necesita espacio y paseos diarios." },
        @{ id = $PetId6; shelterId = $ShelterId2; name = "Kira"; species = "CAT"; breed = "Persa"; age = 3; sex = "HEMBRA"; size = "Mediano"; energyLevel = "MEDIO"; kidsCompatible = $true; otherPetsCompatible = $false; photos = @("uploads/persa.jpg"); bio = "Cariñosa y elegante, disfruta ambientes tranquilos." }
    )

    $created.pets = New-Object System.Collections.ArrayList
    foreach ($petSeed in $petSeeds) {
        Invoke-JsonApi -Method POST -Url $base.Pet -Body @{
            id = $petSeed.id
            name = $petSeed.name
            species = $petSeed.species
            breed = $petSeed.breed
            age = $petSeed.age
            sex = $petSeed.sex
            size = $petSeed.size
            energyLevel = $petSeed.energyLevel
            kidsCompatible = $petSeed.kidsCompatible
            otherPetsCompatible = $petSeed.otherPetsCompatible
            photos = $petSeed.photos
            bio = $petSeed.bio
        } | Out-Null

        Invoke-JsonApi -Method POST -Url "$($base.Shelter)/$($petSeed.shelterId)/pets" -Body @{ value = $petSeed.id } | Out-Null
        [void]$created.pets.Add($petSeed.id)
    }

    Write-Step "Creando carnet de vacunacion para la mascota principal"
    Invoke-JsonApi -Method POST -Url "$($base.Vet)/vaccination-cards" -Body @{ petProfileId = $PetId1 } | Out-Null

    Write-Step "Registrando vacuna de prueba"
    Invoke-JsonApi -Method POST -Url "$($base.Vet)/vaccines" -Body @{
        id = "VAC-001"
        name = "Rabia"
        appliedDate = (Get-Date).ToString('yyyy-MM-dd')
        nextDueDate = (Get-Date).AddMonths(12).ToString('yyyy-MM-dd')
        administeredById = $VetId
        vaccinationCardId = $PetId1
    } | Out-Null

    Write-Step "Registrando evento medico de prueba"
    Invoke-JsonApi -Method POST -Url "$($base.Vet)/medical-events" -Body @{
        id = "MED-001"
        petProfileId = $PetId1
        date = (Get-Date).ToString('yyyy-MM-dd')
        eventType = "OTHER"
        title = "Chequeo general"
        description = "Revision general de rutina"
        vetNotes = "Todo en orden"
        registeredById = $VetId
        registeredByRole = "VET"
        vaccinationCardId = $PetId1
    } | Out-Null

    Write-Step "Preparando notificacion de prueba"
    Invoke-JsonApi -Method POST -Url $base.Notification -Body @{
        id = "NTF-001"
        recipientId = $AdopterId
        recipientEmail = "ana.adoptante@petfinder.test"
        type = "GENERAL"
        channel = "IN_APP"
        subject = "Bienvenida a PetFinder"
        content = "Cuenta de prueba creada correctamente."
    } | Out-Null

    Write-Host "`nSemilla completada correctamente." -ForegroundColor Green
    Write-Host "Adoptante: $AdopterId | Refugios: $ShelterId1, $ShelterId2 | Vet: $VetId" -ForegroundColor Green
    Write-Host "Mascotas creadas: $($created.pets -join ', ')" -ForegroundColor Green
    Write-Host "Passwords -> Adoptante: $AdopterPassword | Refugio: $ShelterPassword | Vet: $VetPassword" -ForegroundColor Green
}
catch {
    Write-Host "`nError al crear datos de prueba: $($_.Exception.Message)" -ForegroundColor Red
    throw
}


