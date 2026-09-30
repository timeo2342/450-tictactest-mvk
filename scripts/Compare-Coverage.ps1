param(
    [Parameter(Mandatory)]
    [string] $MainReport,
    [Parameter(Mandatory)]
    [string] $BranchReport,
    [string] $SummaryPath = $env:GITHUB_STEP_SUMMARY
)

$ErrorActionPreference = 'Stop'

function Read-LineCounter([string] $Path) {
    $settings = [System.Xml.XmlReaderSettings]::new()
    $settings.DtdProcessing = [System.Xml.DtdProcessing]::Ignore
    $settings.XmlResolver = $null
    $reader = [System.Xml.XmlReader]::Create((Resolve-Path -LiteralPath $Path).Path, $settings)
    try {
        $report = [System.Xml.XmlDocument]::new()
        $report.XmlResolver = $null
        $report.Load($reader)
    } finally {
        $reader.Dispose()
    }

    $counters = $report.SelectNodes('/report/counter[@type="LINE"]')
    if ($counters.Count -ne 1) {
        throw "Expected exactly one report-level LINE counter in $Path."
    }
    $counter = $counters[0]
    if ($counter.covered -notmatch '^\d+$' -or $counter.missed -notmatch '^\d+$') {
        throw "Invalid LINE counter in $Path."
    }
    $covered = [decimal] $counter.covered
    $total = $covered + [decimal] $counter.missed
    if ($total -eq 0) {
        throw "Cannot compare coverage without executable lines in $Path."
    }
    return @{ Covered = $covered; Total = $total; Percent = 100 * $covered / $total }
}

$main = Read-LineCounter $MainReport
$branch = Read-LineCounter $BranchReport
# Compare the exact fractions, not the rounded percentages shown in the summary.
$passed = $branch.Covered * $main.Total -ge $main.Covered * $branch.Total
$result = if ($passed) { 'PASS' } else { 'FAIL' }
$culture = [System.Globalization.CultureInfo]::InvariantCulture
$mainPercent = $main.Percent.ToString('F3', $culture)
$branchPercent = $branch.Percent.ToString('F3', $culture)
$difference = ($branch.Percent - $main.Percent).ToString('+0.000;-0.000;0.000', $culture)
$summary = @"
## Coverage Gate: $result

Metric: JaCoCo line coverage (whole project).

| main | Branch / PR | Difference (percentage points) | Result |
|---|---|---|---|
| $mainPercent % | $branchPercent % | $difference | $result |
"@
Write-Output $summary
if ($SummaryPath) {
    Add-Content -LiteralPath $SummaryPath -Value $summary -Encoding utf8
}
if (-not $passed) {
    throw 'Line coverage of the branch is lower than current main.'
}
