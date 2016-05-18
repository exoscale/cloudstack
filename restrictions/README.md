cloudstack restrictions
=======================

Enforce service-offering restrictions in some conditions. Each restriction
is based on the service offering name, and has a list of possible
restrictions:

- maxTemplateSize: max size in Bytes for the ROOT disk
- unauthorizedTemplateName: name of an unauthorized template for this
service offering
- authorizedOrgs: list of orgs uuid allowed to use this service offering

Example
-------

```yaml
-------
restrictions:
  - serviceOfferingName: Micro
    maxTemplateSize: 214748364801
    unauthorizedTemplateName: Windows
  - serviceOfferingName: Titan
    authorizedOrgs:
      - 0f286087-85f3-4195-abcf-e67e7cc7eb63
      - 69240fc3-0cfc-4e68-adb8-ef0c1e75540b
      - c36c1577-ee3d-49c4-8934-e32a56f26405
      - a3e8e043-ea72-4a8d-bf0c-61f192d8e89c
  - serviceOfferingName: Mega
    authorizedOrgs:
      - 0f286087-85f3-4195-abcf-e67e7cc7eb63
      - 69240fc3-0cfc-4e68-adb8-ef0c1e75540b
```
