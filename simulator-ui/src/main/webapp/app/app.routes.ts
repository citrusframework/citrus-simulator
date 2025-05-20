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

import { errorRoute } from './layouts/error/error.route';
import HomeComponent from './home/home.component';
import NavbarComponent from './layouts/navbar/navbar.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    title: 'home.title',
  },
  {
    path: '',
    component: NavbarComponent,
    outlet: 'navbar',
  },
  {
    path: '',
    loadChildren: () => import(`./entities/entity.routes`),
  },
  {
    path: 'scenario',
    data: { pageTitle: 'citrusSimulatorApp.scenario.home.title' },
    loadChildren: () => import(`./scenario/scenario.routes`),
  },
  {
    path: 'scenario-result',
    data: { pageTitle: 'citrusSimulatorApp.scenarioExecution.home.title' },
    loadChildren: () => import(`./scenario-result/scenario-result.routes`),
  },
  ...errorRoute,
];

export default routes;
