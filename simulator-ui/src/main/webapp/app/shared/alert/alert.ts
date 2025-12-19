import { NgClass } from '@angular/common';
import { Component, OnDestroy, inject } from '@angular/core';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AlertModel, AlertService } from 'app/core/util/alert.service';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.html',
  imports: [NgClass, NgbModule],
})
export class Alert implements OnDestroy {
  private readonly alertService = inject(AlertService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  protected readonly alerts = this.alertService.alertsSignal;

  setClasses(alert: AlertModel): Record<string, boolean> {
    const classes = { 'app-toast': Boolean(alert.toast) };
    if (alert.position) {
      return { ...classes, [alert.position]: true };
    }
    return classes;
  }

  ngOnDestroy(): void {
    this.alertService.clear();
  }

  close(alert: AlertModel): void {
    this.alertService.removeAlert(alert.id);
  }
}
