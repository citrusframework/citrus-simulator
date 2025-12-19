import { Routes } from '@angular/router';

import { errorRoute } from './layouts/error/error.route';
import NavbarComponent from './layouts/navbar/navbar.component';
import HomeComponent from './home/home.component';

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
