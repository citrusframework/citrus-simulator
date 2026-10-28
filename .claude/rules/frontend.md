---
paths:
  - "simulator-ui/**"
---

# Frontend rules

- Run npm commands from `simulator-ui/`. Use `npm ci` (not `npm install`) unless you are intentionally changing
  dependencies; keep `package.json` versions pinned (no `^`/`~`) and update `package-lock.json` together with it.
- The code base follows JHipster conventions: standalone components, entity folders under `app/entities/<entity>`
  with `list`, `detail`, `service`, `route` sub-structure. Mirror an existing entity when adding a new one.
- Backend calls go through services using `ApplicationConfigService.getEndpointFor('api/...')`; keep query parameters
  compatible with the backend `*Criteria` filters (`field.equals`, `field.contains`, …).
- User-visible strings belong in the i18n JSON files under `src/main/webapp/i18n`, not inline in templates.
- Unit tests are Jest `*.spec.ts` files next to the source. E2E tests are Playwright specs in `tests/`.
- Before finishing, run `npm test` (includes ESLint) and `npm run prettier:check`; CI fails on either.
