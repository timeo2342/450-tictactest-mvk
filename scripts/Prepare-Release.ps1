param(
    [Parameter(Mandatory)]
    [string] $Tag,
    [Parameter(Mandatory)]
    [string] $Commit,
    [string] $Changelog = 'CHANGELOG.md',
    [string] $NotesPath = 'build/release-notes.md'
)

$ErrorActionPreference = 'Stop'
if ($Tag -notmatch '^v([1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)$') {
    throw "Release tag must be vMAJOR.MINOR.PATCH with MAJOR >= 1: $Tag"
}
if ($Commit -notmatch '^[0-9a-f]{40}$') {
    throw 'A full Git commit SHA is required.'
}
$version = $Tag.Substring(1)
$text = Get-Content -LiteralPath $Changelog -Raw
$heading = '(?m)^## \[' + [regex]::Escape($version) + '\] - (\d{4}-\d{2}-\d{2})\r?$'
$entry = [regex]::Match($text, $heading)
if (-not $entry.Success) {
    throw "Missing dated changelog entry for $version."
}
$date = [datetime]::ParseExact($entry.Groups[1].Value, 'yyyy-MM-dd',
    [System.Globalization.CultureInfo]::InvariantCulture)
if ($date.Date -gt [datetime]::UtcNow.Date) {
    throw "The changelog date for $version is in the future."
}
$remainder = $text.Substring($entry.Index + $entry.Length)
$changes = ([regex]::Split($remainder, '(?m)^## ')[0]).Trim()
foreach ($section in 'Added', 'Changed', 'Fixed') {
    if ($changes -notmatch "(?m)^### $section\r?$") {
        throw "Changelog entry $version requires an $section section."
    }
}
$notes = @"
# TicTacTest $Tag

Tag: ``$Tag``
Commit: ``$Commit``
Artifact: ``tictactest-$version.jar`` (requires Java 25)

$changes
"@
$parent = Split-Path -Parent $NotesPath
if ($parent) {
    New-Item -ItemType Directory -Path $parent -Force | Out-Null
}
Set-Content -LiteralPath $NotesPath -Value $notes -Encoding utf8
Write-Output "Prepared $Tag from $Commit"
