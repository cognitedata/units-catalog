# Changelog

All notable changes to this project will be documented in this file.

Each release should include the following types of changes when applicable:
- `Added` for new features
- `Changed` for changes in existing functionality
- `Deprecated` for soon-to-be removed features
- `Removed` for now removed features
- `Fixed` for any bug fixes
- `Security` in case of vulnerabilities

## [0.1.17] - 2025-01-07
### Added
- Added density unit:
  - Gram per cubic centimeter (g/cm³) with comprehensive aliases
- Added angular velocity unit:
  - Radian per second (rad/s)
- Added "rpm" alias for revolutions per minute unit

### Changed
- Updated angular velocity SI unit to radian per second (rad/s)

## [0.1.16] - 2024-11-28
### Added
- Added pressure gradient unit:
  - Pound force per square inch per meter (psi/m) with various aliases

## [0.1.15] - 2024-11-18
### Added
- Added new unit aliases:
  - kg/d for kilogram per day
  - kg/hr, kg/h for kilogram per hour
  - bar a for bar absolute
  - bar G for bar gauge
  - Bbl, BBLS for barrel
  - MSCFD, Mscf/d, MSCF/D for thousand standard cubic feet per day
  - mmscfd, MMScf/d for million standard cubic feet per day

### Fixed
- Removed duplicate aliases from various units
- Standardized JSON formatting

## [0.1.14] - 2024-08-15
### Added
- Added API Gravity quantity and units:
  - Degree API (°API)
  - Kilo Degree API (k °API)
- Added new frequency unit:
  - Hectohertz (hHz)
- Added attenuation quantity and units:
  - Decibel (dB)
  - Bel (B)
- Added unknown unit type for handling undefined units
- Added "spm" and "SPM" as new aliases for frequency per minute unit

## [0.1.13] - 2024-05-23
### Changed
- Improved method return values instead of printing outputs
- Updated Maven test configuration to redirect test output to file
- Improved test output formatting and reporting

## [0.1.12] - 2024-05-13
### Added
- Added ability to fetch units by alias without requiring quantity or system specification
  - Users can now search by alias and handle potential multiple matches themselves

## [0.1.11] - 2024-05-13
### Added
- Added volume flow rate unit:
  - Liter per day (L/day) with comprehensive alias support

## [0.1.10] - 2024-05-08
### Added
- Added angle per length quantity and units:
  - Radians per meter (rad/m)
  - Degrees per meter (deg/m)
  - Radians per foot (rad/ft)

## [0.1.9] - 2024-05-07
### Added
- Added ability to load units and systems from strings instead of URLs

## [0.1.8] - 2024-04-19
### Added
- Added pressure gradient quantity and units:
  - Pascal per meter (Pa/m)
  - Bar per meter (bar/m)
  - Kilopascal per meter (kPa/m)
  - Pound force per square inch per foot (psi/ft)
- Added new aliases for multiple units including:
  - Per minute (1/min, per min)
  - Kiloohm (kOhm, kohm)
  - Hours (Hr, Hrs)
  - Days (days, Days, d)
  - More aliases for volume flow rates, electric units, and dimensions

### Fixed
- Fixed conversion multiplier for Million pound-force (Mlbf)
- Fixed spelling consistency: standardized on 'meter' instead of 'metre' in unit names

## [0.1.7] - 2024-02-19
### Changed
- Fixed conversion offsets for gauge pressure units:
  - bar(g)
  - kPa(g)
  - psi(g)

### Removed
- Removed incorrectly categorized length unit: BTU_IT-PER-LB_F

## [0.1.6] - 2024-02-19
### Added
- Added helper method to use the unit library in scala

## [0.1.5] - 2024-02-02
### Added
- Added method for listing unit systems

## [0.1.4] - 2024-01-25
### Added
- Added new frequency units:
  - Per minute (/min)
- Added new pressure units:
  - Kilogram force per square centimeter (kgf/cm²)
  - Millimeter of water (mmH₂O)
  - Kilopascal gauge (kPa(g))
  - Bar gauge (bar(g))
  - PSI gauge (psi(g))
- Added new density units:
  - Milligram per cubic meter (mg/m³)
- Added new length units:
  - Reciprocal meter (/m)
- Added new velocity units:
  - Millimeter per second (mm/s)
  - Millimeter per hour (mm/h)
- Added new volume units:
  - Million standard cubic meter (MMscm)
  - Million standard cubic feet (MMscf)
- Added new volume flow rate units:
  - Liter per hour (L/hr)
  - Thousand standard cubic feet per day (kft³/day)
  - Million standard cubic feet per day (MMscf/day)
- Added new energy units:
  - Barrel of oil equivalent (boe)
- Added new energy per area quantities and units:
  - Joule per square metre (J/m²)
  - Megajoule per square metre (MJ/m²)
  - Gigajoule per square metre (GJ/m²)
- Added new power per area quantities and units:
  - Watt per square metre (W/m²)
- Added new kinematic viscosity quantities and units:
  - Stokes (St)
  - Centistokes (cSt)
- Added new linear density quantities and units:
  - Kilogram per meter (kg/m)
- Added new time units:
  - Month (mo)

### Changed
- Added new aliases for several units including kg/m³, bar, psi, RPM, km/h, m/s
- Updated barrel (bbl) symbol format

## [0.1.3] - 2023-12-13
### Added
- Added new units for Dynamic Viscosity:
  - Poise (P)
  - Centipoise (cP)
- Added new units for Dimensionless Ratio:
  - Parts per billion (PPB)
- Added new units for Density:
  - Gram per cubic meter (g/m³)
- Added new units for Volume Flow Rate:
  - Cubic foot per day (ft³/day)
- Added new units for Power:
  - Kilovolt Ampere (kV⋅A)
  - Megavolt Ampere (MV⋅A)
  - Volt Ampere (V⋅A)

## [0.1.2] - 2023-10-27
### Added
- Added new units: bbl{US petroleum} and bbl{US petroleum}/d

### Changed
- Updated all QUDT reference links from http to https

## [0.1.1] - 2023-10-16
### Added
- Added getUnits function to list all units
- Added more unit entries for quantities like Acceleration, Angular Velocity, Capacitance, and many more
- Added format validation

### Fixed
- Fixed group and artifact IDs in pom.xml
- Fixed documentation and metadata in pom.xml
- Fixed typos in tests and documentation

## [0.1.0] - 2023-09-28
### Added
- Introduced initial version of the unit catalog with support for multiple unit measurement systems (Default, SI, Imperial)
- Added comprehensive unit systems configuration for various quantities including Temperature, Pressure, Mass, Volume, Energy, etc.
- Added detailed unit definitions with properties like external ID, name, long name, alias names, conversion factors, and symbols
- Added testing framework and library
- Added license and attribution notice
