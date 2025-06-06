# Cognite Data Fusion Unit Catalog

This repository stores a comprehensive unit catalog for Cognite Data Fusion (CDF) with a focus on standardization, comprehensiveness, and consistency. The catalog is maintained in two primary JSON files:

1. `units.json`: Contains a list of units with their metadata.
2. `unitSystems.json`: Contains various unit systems and their quantities.

## Structure

### units.json

Each item in the `units.json` has the following structure:

```json
{
    "externalId": "string",
    "name": "string",
    "longName": "string",
    "symbol": "string",
    "aliasNames": ["string", ...],
    "quantity": "string",
    "conversion": {
        "multiplier": "float",
        "offset": "float"
    },
    "source": "string",
    "sourceReference": "URL"
}
```

- `externalId`: The external identifier for the unit. Its structure follows the pattern `{quantity}:{unit}` (e.g., `temperature:deg_c`), where both quantity and unit are lowercase with whitespaces replaced by underscores (`_`). When using QUDT units (preferred when available), the unit portion should match the QUDT name with the same formatting rules. Note that hyphens (`-`) in the unit portion are currently preserved.
- `name`: The primary name of the unit (e.g., `DEG_C`).
- `longName`: A descriptive name for the unit (e.g., `degree Celsius`).
- `symbol`: The symbol for the unit (e.g., `°C`).
- `aliasNames`: An array of possible **aliases** for the unit.
- `quantity`: Specifies the physical quantity the unit measures (e.g., `Temperature`).
- `conversion`: An object containing **multiplier** and **offset** values for converting between units.
- `source`: The primary source of the unit (e.g., `qudt.org`).
- `sourceReference`: A URL reference to the unit definition on an external source, if available.

### unitSystems.json

Each item in the `unitSystems.json` has the following structure:

```json
{
    "name": "string",
    "quantities": [
        {
            "name": "string",
            "unitExternalId": "string"
        },
        ...
    ]
}
```
- `name`: The name of the unit system (e.g., `default`, `SI`).
- `quantities`: An array containing the physical quantities and their associated units in the system.

## Validations and Tests

To ensure the integrity of the catalog, the following tests are conducted:

1. **Syntax Check**: Every unit item in `units.json` must have the specified keys.
2. **Unique IDs**: All unit `externalIds` in `units.json` must be unique.
3. **Reference Validation**: There should be no references to non-existent unit `externalIds` in `unitSystems.json`.
4. **Default Quantities**: All quantities must be present in the `unitSystems.json` for the default quantity.
5. **Consistent References**: All quantity references in `unitSystems.json` must exist in `units.json`.
6. **Unique Quantity-Alias Pairs**: All pairs of (`alias` and `quantity`) must be unique across all units, for all aliases in `aliasNames`.
7. **Unique Unit Aliases**: Each unit's `aliasNames` array must contain only unique values, with no duplicate entries allowed.
8. **ExternalId Format**: All unit `externalIds` must follow the pattern `{quantity}:{unit}`, where both `quantity` and `unit` are in **snake_case**.

## Attribution
Some of the units are sourced from QUDT.org, which is licensed under the [Creative Commons Attribution 4.0 International License](https://creativecommons.org/licenses/by/4.0/).
These are marked with the `qudt.org` source.

## Code ownership
Copyright 2023 Cognite AS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This license applies to any code/data file in this repository, unless otherwise noted.

## Contribution

To maintain the consistency and quality of the unit catalog, please ensure any contributions adhere to the established structure and guidelines. Before submitting any additions or modifications:

1. Ensure that all tests pass
2. Provide meaningful descriptions in your Pull Requests that explain:
   - Summary of the changes introduced in the pull request
   - The rationale behind the changes/contributions
   - Which customers/use cases this change addresses
3. When adding new units, prefer using QUDT units when available. Other standards or publications may be used when a suitable QUDT entry does not exist.

### Release Schedule

To ensure stability and proper validation of changes:
- New versions of the unit catalog are published at most every 2 months, depending on contributions
- Critical bug fixes may be released outside of this schedule as needed
- After a release is published on GitHub, the changes are deployed across CDF clusters within two weeks

Please keep this schedule in mind when planning your contributions.
