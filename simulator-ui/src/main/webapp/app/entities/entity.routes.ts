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
