# publicar_apk.ps1 - Publica la APK debug con el nombre Asis-v<versionName>.apk
# Uso: .\publicar_apk.ps1
$ErrorActionPreference = "Stop"

# 1. Leer versionName de app\build.gradle.kts
$gradleFile = Join-Path $PSScriptRoot "app\build.gradle.kts"
$content = Get-Content $gradleFile -Raw
if ($content -notmatch 'versionName\s*=\s*"([^"]+)"') {
    throw "No se encontro versionName en app\build.gradle.kts"
}
$version = $Matches[1]
Write-Host "Version detectada: v$version"

# 2. Verificar que exista la APK debug
$apk = Join-Path $PSScriptRoot "app\build\outputs\apk\debug\app-debug.apk"
if (-not (Test-Path $apk)) {
    throw "No existe la APK debug. Ejecuta primero: .\gradlew.bat assembleDebug"
}

# 3. Copiar con el nombre estandar Asis-vX.X.apk
$destDir = "D:\Publicados\Asistencia"
if (-not (Test-Path $destDir)) {
    New-Item -ItemType Directory -Path $destDir | Out-Null
}
$dest = Join-Path $destDir "Asis-v$version.apk"
Copy-Item $apk $dest -Force

$tamanoMB = [math]::Round((Get-Item $dest).Length / 1MB, 1)
Write-Host "Publicada: $dest ($tamanoMB MB)"
