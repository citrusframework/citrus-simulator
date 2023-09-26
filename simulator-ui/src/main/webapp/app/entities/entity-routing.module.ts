import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'test-parameter',
        data: { pageTitle: 'citrusSimulatorApp.testParameter.home.title' },
        loadChildren: () => import('./test-parameter/test-parameter.routes'),
      },
      {
        path: 'test-result',
        data: { pageTitle: 'citrusSimulatorApp.testResult.home.title' },
        loadChildren: () => import('./test-result/test-result.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
