# Changelog

All notable changes to this project will be documented in this file.

Each release should include the following types of changes when applicable:
- `Added` for new features
- `Changed` for changes in existing functionality
- `Deprecated` for soon-to-be removed features
- `Removed` for now removed features
- `Fixed` for any bug fixes
- `Security` in case of vulnerabilities

## [0.1.23] - 2025-08-05
### Added
- Added new aliases for electric current units:
  - Ampere (A): Added AAC, ADC
  - Kiloampere (kA): Added kAAC, kADC
  - Milliampere (mA): Added mAAC, mADC
- Added new aliases for electric potential units:
  - Kilovolt (kV): Added kVAC, kVDC
  - Millivolt (mV): Added mVAC, mVDC

### Fixed
- Corrected conversion offset for pressure unit millibar(g) from 1.01325 to 1013.25

## [0.1.22] - 2025-06-10
### Added
- Added new unit for quantity `Length`:
  - One Sixty-Fourth of an Inch (¹⁄₆₄″)
- Added new quantity `Gas Liquid Ratio` with the following units:
  - Standard Cubic Meter (@15°C/101.325 kPa) per Standard Cubic Meter (@15°C/101.325 kPa) (Sm³/Sm³)
  - Standard Cubic Feet per Stock Tank Barrel (scf/STB)
- Added new quantity `Liquid Gas Ratio` with the following units:
  - Standard Cubic Meter (@15°C, 101.325 kPa) per Standard Cubic Meter (@15°C, 101.325 kPa) (Sm³/Sm³)
  - Stock Tank Barrel per Million Standard Cubic Feet (STB/MMscf)
- Added new unit for quantity `Areal Density`:
  - Gram Per Square Meter (g/m²)
- Added new unit for quantity `Angle Per Length`:
  - Degrees Per 30 Meters (deg/30m)
- Added new unit for quantity `Volume Flow Rate`:
  - Thousand Barrel (US) Per Day (kbbl/d)
- Added new unit for quantity `Volume`:
  - Thousand Barrel (US) (kbbl)
- Added new units for quantity `Volume Fraction`:
  - Cubic Foot Per Barrel (US) (ft³/bbl)
  - Barrel (US) Per Barrel (US) (bbl/bbl)
  - Barrel (US) Per Thousand Cubic Foot (bbl/kft³)
  - Thousand Cubic Foot Per Barrel (US) (kft³/bbl)
- Added new unit for quantity `Density`:
  - Milligram per litre (mg/L)
- Added new unit for quantity `Mass Concentration`:
  - Milligram per litre (mg/L)
- Added new quantity `Volume per time per pressure` with the following units:
  - Cubic Meter per Pascal Second (m³/(Pa.s))
  - (Barrel (US) Per Day) Per Psi (bbl/(psi.d))
  - (Cubic Meter Per Day) Per Bar (m³/(bar.d))
  - (Thousand Cubic Foot Per Day) Per Psi (kft³/(psi.d))
  - (Cubic Foot Per Second) Per Psi (ft³/(psi.s))
- Added new unit for quantity `Pressure`:
  - Millibar(g) (mbar(g))
- Added new unit for quantity `Power`:
  - Mega British Thermal Unit (International Definition) per Day (MBtu{IT}/d)
- Added new unit for quantity `Mass Flow Rate`:
  - Pound per Day (lbm/d)

### Changed
- Updated symbols that used to be "k.ft³" to "kft³" for consistency
- Added new aliases for existing units to improve matching and usability

## [0.1.21] - 2025-02-28
### Added
- Added new units for quantity `Volume Flow Rate`:
  - Barrel (US) Per Hour (bbl/h)
  - US Gallon per Minute (gal{US}/min)
- Added new unit for quantity `Angle`:
  - Kilo Revolution (krev)
- Added new unit for quantity `Density`:
  - kilogram per litre (kg/L)
- Added new units for quantity `Force`:
  - Kilo decanewton (kdaN)
  - Kilo Kilogram Force (kkgf)
  - Kilogram Force (kgf)
- Added new units for quantity `Torque`:
  - Kilo Pound Force Foot (klbf⋅ft)
  - Kilo Newton Meter (kN⋅m)
- Added new unit for quantity `Velocity`:
  - Foot per Minute (ft/min)
- Added new quantity `Radioactivity` with the following units:
  - Becquerel (Bq)
  - Curie (Ci)
- Added new quantity `Magnetic Field` with the following units:
  - Tesla (T)
  - Gauss (Gs)
  - Nano Tesla (nT)
- Added new quantity `Resistivity` with the following units:
  - Ohm Meter (Ω⋅m)
  - Ohm Foot (Ω⋅ft)
- Added several new unit aliases

## [0.1.20] - 2025-01-30
### Added
- Added new quantity `Pressure` unit `Pound Force per 100 Square Foot` (lbf/100ft²)

## [0.1.19] - 2025-01-23
### Added
- Added new quantity `Areal Density` with the following units:
  - Pounds per Square Feet (lb/ft²)
  - Kilogram Per Square Meter (kg/m²)
  - Pounds per 100 Square Feet (lb/100ft²)

## [0.1.18] - 2025-01-14
### Fixed
- Fixed conversion factor for `mass_concentration:milligm-per-millil`

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
