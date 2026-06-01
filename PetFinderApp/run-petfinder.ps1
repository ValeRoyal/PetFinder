[CmdletBinding()]
param(
    [ValidateSet("start", "stop", "status", "restart")]
    [string]$Action = "start",
    [int]$FrontendPort = 5500,
    [switch]$OpenBrowser
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$pidFile = Join-Path $root ".petfinder-processes.json"

$serviceDefs = @(
    @{ Name = "msPet"; Path = Join-Path $root "msPet"; Command = ".\\mvnw.cmd spring-boot:run" },
    @{ Name = "msShelter"; Path = Join-Path $root "msShelter"; Command = ".\\mvnw.cmd spring-boot:run" },
    @{ Name = "msAdopter"; Path = Join-Path $root "msAdopter"; Command = ".\\mvnw.cmd spring-boot:run" },
    @{ Name = "msNotification"; Path = Join-Path $root "msNotification"; Command = ".\\mvnw.cmd spring-boot:run" },
    @{ Name = "msVet"; Path = Join-Path $root "msVet"; Command = ".\\mvnw.cmd spring-boot:run" },
    @{ Name = "msMatch"; Path = Join-Path $root "msMatch"; Command = ".\\mvnw.cmd spring-boot:run" }
)

$frontendPath = Join-Path $root "FrontEndPetFinder\public_html"
$proxyScript = Join-Path $root "frontend-proxy.py"

function Get-TrackedProcesses {
    if (-not (Test-Path -LiteralPath $pidFile)) {
        return @()
    }

    $raw = Get-Content -LiteralPath $pidFile -Raw
    if ([string]::IsNullOrWhiteSpace($raw)) {
        return @()
    }

    $list = $raw | ConvertFrom-Json
    if ($null -eq $list) {
        return @()
    }

    if ($list -is [System.Array]) {
        return $list
    }

    return @($list)
}

function Save-TrackedProcesses([System.Collections.ArrayList]$entries) {
    $entries | ConvertTo-Json -Depth 3 | Set-Content -LiteralPath $pidFile -Encoding UTF8
}

function Start-ManagedProcess([string]$name, [string]$workingDir, [string]$command) {
    if (-not (Test-Path -LiteralPath $workingDir)) {
        throw "No existe la carpeta para ${name}: $workingDir"
    }

    $argList = @(
        "-NoLogo",
        "-NoExit",
        "-Command",
        "Set-Location -LiteralPath '$workingDir'; $command"
    )

    $proc = Start-Process -FilePath "powershell.exe" -ArgumentList $argList -PassThru -WindowStyle Minimized
    Write-Host "[OK] $name iniciado (PID $($proc.Id))" -ForegroundColor Green

    return [PSCustomObject]@{
        name = $name
        pid = $proc.Id
        path = $workingDir
        startedAt = (Get-Date).ToString("s")
    }
}

function Stop-TrackedProcesses {
    $tracked = @(Get-TrackedProcesses)
    if ($tracked.Count -eq 0) {
        Write-Host "No hay procesos registrados para detener." -ForegroundColor Yellow
        return
    }

    foreach ($item in $tracked) {
        $p = Get-Process -Id $item.pid -ErrorAction SilentlyContinue
        if ($null -ne $p) {
            Stop-Process -Id $item.pid -Force
            Write-Host "[OK] Detenido $($item.name) (PID $($item.pid))" -ForegroundColor Green
        } else {
            Write-Host "[INFO] $($item.name) ya no estaba corriendo (PID $($item.pid))" -ForegroundColor DarkYellow
        }
    }

    Remove-Item -LiteralPath $pidFile -Force -ErrorAction SilentlyContinue
}

function Show-Status {
    $tracked = @(Get-TrackedProcesses)
    if ($tracked.Count -eq 0) {
        Write-Host "No hay procesos registrados." -ForegroundColor Yellow
        return
    }

    $rows = foreach ($item in $tracked) {
        $isRunning = $null -ne (Get-Process -Id $item.pid -ErrorAction SilentlyContinue)
        [PSCustomObject]@{
            Name = $item.name
            PID = $item.pid
            Running = $isRunning
            StartedAt = $item.startedAt
        }
    }

    $rows | Format-Table -AutoSize | Out-String | Write-Host
}

switch ($Action) {
    "start" {
        $existing = @(Get-TrackedProcesses)
        if ($existing.Count -gt 0) {
            $alive = $existing | Where-Object { $null -ne (Get-Process -Id $_.pid -ErrorAction SilentlyContinue) }
            if ($alive.Count -gt 0) {
                Write-Host "Ya hay procesos ejecutandose. Usa -Action status o -Action stop primero." -ForegroundColor Yellow
                Show-Status
                break
            }
        }

        $started = New-Object System.Collections.ArrayList

        foreach ($svc in $serviceDefs) {
            $entry = Start-ManagedProcess -name $svc.Name -workingDir $svc.Path -command $svc.Command
            [void]$started.Add($entry)
            Start-Sleep -Seconds 1
        }

        $python = Get-Command python -ErrorAction SilentlyContinue
        if ($null -ne $python -and (Test-Path -LiteralPath $proxyScript)) {
            $frontCmd = "python `"$proxyScript`" --port $FrontendPort"
            $frontEntry = Start-ManagedProcess -name "frontend-proxy" -workingDir $root -command $frontCmd
            [void]$started.Add($frontEntry)
            Write-Host "Frontend en: http://127.0.0.1:$FrontendPort" -ForegroundColor Cyan

            if ($OpenBrowser) {
                Start-Process "http://127.0.0.1:$FrontendPort"
            }
        } else {
            Write-Host "[WARN] No se detecto Python. El frontend no se levanto automaticamente." -ForegroundColor Yellow
            Write-Host "       Sirvelo manualmente con: python `"$proxyScript`" --port $FrontendPort" -ForegroundColor Yellow
        }

        Save-TrackedProcesses -entries $started
        Write-Host "Inicio completo. Usa -Action status para revisar estado." -ForegroundColor Green
    }

    "stop" {
        Stop-TrackedProcesses
    }

    "status" {
        Show-Status
    }

    "restart" {
        Stop-TrackedProcesses
        Start-Sleep -Seconds 1
        & $MyInvocation.MyCommand.Path -Action start -FrontendPort $FrontendPort -OpenBrowser:$OpenBrowser
    }
}



