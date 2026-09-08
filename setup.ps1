<#
.SYNOPSIS
    One-time local setup for the personal-finance-tracker multi-module project.

.DESCRIPTION
    Run this from the repo root (double-click setup.cmd or run .\setup.ps1).

    1. Builds and installs all modules (common, reporting-api, core-api) so the
       'common' classes are on the runtime classpath (fixes
       "NoClassDefFoundError: UserRepository" when running inside core-api only).
    2. Ensures core-api/src/main/resources/application.yaml exists and contains
       the correct security.jwt (JWT) configuration:
         - if missing -> copied from application.example.yaml
         - if present -> the security.jwt block is created/updated to the
           canonical values (secret + expirations).

    NOTE: application.yaml is git-ignored, so every developer needs to run this
    once after cloning. The rest of the file (datasource, profile) is left
    untouched.
#>
[CmdletBinding()]
param(
    [string]$ConfigPath = (Join-Path $PSScriptRoot 'core-api\src\main\resources\application.yaml'),
    [string]$TemplatePath = (Join-Path $PSScriptRoot 'core-api\src\main\resources\application.example.yaml'),
    [switch]$SkipBuild
)

$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

$CJKWT = 'fSJZ7SidnbqQB9l1Nh710RbK8TJWpL4fYNvVYowHXYU='
$CJ_ACCESS = '3600000'
$CJ_REFRESH = '86400000'

Write-Host '============================================================' -ForegroundColor Cyan
Write-Host ' Personal Finance Tracker - local setup' -ForegroundColor Cyan
Write-Host '============================================================' -ForegroundColor Cyan

# 1) Build + install all modules
if (-not $SkipBuild) {
    Write-Host ''
    Write-Host '[1/2] Building project (mvnw clean install -DskipTests) ...' -ForegroundColor Cyan
    $ErrorActionPreference = 'Continue'
    & .\mvnw.cmd clean install -DskipTests
    $buildExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    if ($buildExit -ne 0) {
        Write-Host ''
        Write-Host "BUILD FAILED (exit $buildExit). Fix the errors above and re-run." -ForegroundColor Red
        exit $buildExit
    }
    Write-Host ''
    Write-Host 'Build OK.' -ForegroundColor Green
} else {
    Write-Host ''
    Write-Host '[1/2] Build skipped (-SkipBuild).' -ForegroundColor Yellow
}

# 2) Ensure application.yaml with the correct JWT config
Write-Host ''
Write-Host '[2/2] Checking application.yaml ...' -ForegroundColor Cyan

if (-not (Test-Path -LiteralPath $ConfigPath)) {
    if (Test-Path -LiteralPath $TemplatePath) {
        Copy-Item -LiteralPath $TemplatePath -Destination $ConfigPath
        Write-Host "Created $ConfigPath from template (edit DB credentials if needed)." -ForegroundColor Yellow
    } else {
        Write-Host "ERROR: template not found at $TemplatePath" -ForegroundColor Red
        exit 1
    }
}

$canonicalBlock = @"
security:
  jwt:
    secret-key: $CJKWT
    access:
      token:
        expiration: $CJ_ACCESS
    refresh:
      token:
        expiration: $CJ_REFRESH
"@
$canonicalBlock = $canonicalBlock.TrimEnd()

$content = Get-Content -LiteralPath $ConfigPath -Raw
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)

if ($content -notmatch '(?m)^security\s*:') {
    $newContent = $content.TrimEnd() + "`r`n`r`n" + $canonicalBlock + "`r`n"
    [System.IO.File]::WriteAllText($ConfigPath, $newContent, $utf8NoBom)
    Write-Host "Added missing security.jwt block to $ConfigPath" -ForegroundColor Green
} else {
    $hasCorrectSecret = $content -match ('secret-key:\s*' + [regex]::Escape($CJKWT))
    $hasCorrectAccess = $content -match ('expiration:\s*' + $CJ_ACCESS)
    $hasCorrectRefresh = $content -match ('expiration:\s*' + $CJ_REFRESH)

    if ($hasCorrectSecret -and $hasCorrectAccess -and $hasCorrectRefresh) {
        Write-Host "security.jwt block already correct - no change." -ForegroundColor Green
    } else {
        $updated = $content -replace '(?ms)^security\s*:.*$', ($canonicalBlock + "`r`n")
        [System.IO.File]::WriteAllText($ConfigPath, $updated, $utf8NoBom)
        Write-Host "Updated security.jwt block in $ConfigPath" -ForegroundColor Green
    }
}

Write-Host ''
Write-Host 'Done. Run the app with:' -ForegroundColor Cyan
Write-Host '  .\mvnw.cmd -pl core-api spring-boot:run' -ForegroundColor Gray
Write-Host 'or from the IDE: open the root pom.xml and run CoreApiApplication.' -ForegroundColor Gray