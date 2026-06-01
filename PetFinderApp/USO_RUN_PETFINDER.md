# Uso rapido: `run-petfinder.ps1`

Este script levanta y controla, desde un solo comando:
- `msPet`
- `msShelter`
- `msAdopter`
- `msNotification`
- `msVet`
- `msMatch`
- Frontend estatico (`FrontEndPetFinder/public_html`) en `http://localhost:5500` si `python` esta disponible.

## 1) Ejecutar el script

Abre PowerShell en:
`C:\Users\juand\IdeaProjects\PetFinder\PetFinderApp`

```powershell
Set-Location 'C:\Users\juand\IdeaProjects\PetFinder\PetFinderApp'
```

Si tu politica de ejecucion bloquea scripts:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

## 2) Comandos principales

Iniciar todo:

```powershell
.\run-petfinder.ps1 -Action start
```

Iniciar todo y abrir navegador:

```powershell
.\run-petfinder.ps1 -Action start -OpenBrowser
```

Ver estado:

```powershell
.\run-petfinder.ps1 -Action status
```

Detener todo lo iniciado por el script:

```powershell
.\run-petfinder.ps1 -Action stop
```

Reiniciar todo:

```powershell
.\run-petfinder.ps1 -Action restart
```

## 3) Cambiar puerto del frontend

```powershell
.\run-petfinder.ps1 -Action start -FrontendPort 5600
```

## Notas

- El script guarda los PIDs en `.petfinder-processes.json` para poder hacer `stop` y `status`.
- Si no tienes `python` en PATH, el script iniciara los microservicios y te avisara para servir frontend manualmente.
- Cada servicio se abre en una ventana PowerShell minimizada para que puedas revisar logs si hace falta.

