import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { Alert, AlertService } from 'app/core/util/alert.service';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  imports: [CommonModule, NgbModule],
})
export class AlertComponent implements OnInit, OnDestroy {
  alerts = signal<Alert[]>([]);

  private readonly alertService = inject(AlertService);

  ngOnInit(): void {
    this.alerts.set(this.alertService.get());
  }

  setClasses(alert: Alert): Record<string, boolean> {
    const classes = { 'app-toast': Boolean(alert.toast) };
    if (alert.position) {
      return { ...classes, [alert.position]: true };
    }
    return classes;
  }

  ngOnDestroy(): void {
    this.alertService.clear();
  }

  close(alert: Alert): void {
    alert.close?.(this.alerts());
  }
}
