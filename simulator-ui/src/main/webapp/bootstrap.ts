import { enableProdMode } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';

import App from './app/app';
import { appConfig } from './app/app.config';
import { DEBUG_INFO_ENABLED } from './app/app.constants';

// disable debug data on prod profile to improve performance
if (!DEBUG_INFO_ENABLED) {
  enableProdMode();
}

bootstrapApplication(App, appConfig)
  // eslint-disable-next-line no-console
  .then(() => console.log('Application started'))
  .catch((err: unknown) => console.error(err));
