import json


def get_external_ids(file_path: str) -> set:
    with open(file_path, "r") as f:
        data = json.load(f)
    return set(item["externalId"] for item in data)


main_external_ids: set = get_external_ids("main/versions/v1/units.json")
pr_external_ids: set = get_external_ids("pr/versions/v1/units.json")

# 8. Avoid breaking changes: There can be no removals of unit `externalIds` in `units.json`.
# Only additions are supported.
removed_external_ids: set = main_external_ids - pr_external_ids

if removed_external_ids:
    raise Exception(
        f'externalId(s) [{", ".join(removed_external_ids)}] have been removed from the unit catalogue.'
    )
