# Releasing units-catalog

Production releases are published to [Maven Central](https://central.sonatype.com/artifact/com.cognite.units/units-catalog) as `com.cognite.units:units-catalog`. Git tags (`vX.Y.Z`) and [GitHub Releases](https://github.com/cognitedata/units-catalog/releases) are created automatically by CI when a new version ships.

## Steps (maintainers)

1. **Changelog** — Add a section to [CHANGELOG.md](CHANGELOG.md) for the new version:

   `## [X.Y.Z] - YYYY-MM-DD`

   Follow the existing *Added* / *Changed* / *Fixed* structure.

2. **Version** — Set `<version>X.Y.Z</version>` in the root [pom.xml](pom.xml) to match the changelog entry.

3. **Merge to `main`** — Open a PR and merge. The workflow [.github/workflows/release.yml](.github/workflows/release.yml) runs when `pom.xml` (or the workflow file) changes on `main`.

4. **CI** — If the POM version is newer than the latest version on Maven Central, the job deploys the artifact, then creates a GitHub Release whose notes are the changelog section for that version plus a link to the Maven Central artifact page.

Do **not** create Git tags or GitHub releases manually for normal releases; automation handles it.

## Retrying a failed GitHub Release

If Maven Central deployment succeeded but the GitHub Release step failed, fix the workflow or re-run the job: once Central lists the new version, CI can create the missing release without redeploying (same `pom.xml` version on `main`).
