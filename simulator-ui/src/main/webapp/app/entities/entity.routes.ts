/*
 * Copyright the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'message',
    data: { pageTitle: 'citrusSimulatorApp.message.home.title' },
    loadChildren: () => import('./message/message.routes'),
  },
  {
    path: 'message-header',
    data: { pageTitle: 'citrusSimulatorApp.messageHeader.home.title' },
    loadChildren: () => import('./message-header/message-header.routes'),
  },
  {
    path: 'scenario-execution',
    data: { pageTitle: 'citrusSimulatorApp.scenarioExecution.home.title' },
    loadChildren: () => import('./scenario-execution/scenario-execution.routes'),
  },
  {
    path: 'scenario-action',
    data: { pageTitle: 'citrusSimulatorApp.scenarioAction.home.title' },
    loadChildren: () => import('./scenario-action/scenario-action.routes'),
  },
  {
    path: 'scenario-parameter',
    data: { pageTitle: 'citrusSimulatorApp.scenarioParameter.home.title' },
    loadChildren: () => import('./scenario-parameter/scenario-parameter.routes'),
  },
  {
    path: 'test-result',
    data: { pageTitle: 'citrusSimulatorApp.testResult.home.title' },
    loadChildren: () => import('./test-result/test-result.routes'),
  },
  {
    path: 'test-parameter',
    data: { pageTitle: 'citrusSimulatorApp.testParameter.home.title' },
    loadChildren: () => import('./test-parameter/test-parameter.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
