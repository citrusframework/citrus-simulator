import { inject, TestBed } from '@angular/core/testing';

import { TranslateModule, TranslateService, MissingTranslationHandler } from '@ngx-translate/core';

import { missingTranslationHandler } from 'app/config/translation.config';

import { Alert, AlertService } from './alert.service';

describe('Alert service test', () => {
  describe('Alert Service Test', () => {
    let extAlerts: Alert[];

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [
          TranslateModule.forRoot({
            missingTranslationHandler: {
              provide: MissingTranslationHandler,
              useFactory: missingTranslationHandler,
            },
          }),
        ],
      });
      const translateService = TestBed.inject(TranslateService);
      translateService.setDefaultLang('en');
      jest.useFakeTimers();
      extAlerts = [];
    });

    it('should produce a proper alert object and fetch it', inject([AlertService], (service: AlertService) => {
      expect(
        service.addAlert({
          type: 'success',
          message: 'Hello Citrus',
          timeout: 3000,
          toast: true,
          position: 'top left',
        }),
      ).toEqual(
        expect.objectContaining({
          type: 'success',
          message: 'Hello Citrus',
          id: 0,
          timeout: 3000,
          toast: true,
          position: 'top left',
        } as Alert),
      );

      expect(service.get().length).toBe(1);
      expect(service.get()[0]).toEqual(
        expect.objectContaining({
          type: 'success',
          message: 'Hello Citrus',
          id: 0,
          timeout: 3000,
          toast: true,
          position: 'top left',
        } as Alert),
      );
    }));

    it('should produce a proper alert object and add it to external alert objects array', inject(
      [AlertService],
      (service: AlertService) => {
        expect(
          service.addAlert(
            {
              type: 'success',
              message: 'Hello Citrus',
              timeout: 3000,
              toast: true,
              position: 'top left',
            },
            extAlerts,
          ),
        ).toEqual(
          expect.objectContaining({
            type: 'success',
            message: 'Hello Citrus',
            id: 0,
            timeout: 3000,
            toast: true,
            position: 'top left',
          } as Alert),
        );

        expect(extAlerts.length).toBe(1);
        expect(extAlerts[0]).toEqual(
          expect.objectContaining({
            type: 'success',
            message: 'Hello Citrus',
            id: 0,
            timeout: 3000,
            toast: true,
            position: 'top left',
          } as Alert),
        );
      },
    ));

    it('should produce an alert object with correct id', inject([AlertService], (service: AlertService) => {
      service.addAlert({ type: 'info', message: 'Hello Citrus info' });
      expect(service.addAlert({ type: 'success', message: 'Hello Citrus success' })).toEqual(
        expect.objectContaining({
          type: 'success',
          message: 'Hello Citrus success',
          id: 1,
        } as Alert),
      );

      expect(service.get().length).toBe(2);
      expect(service.get()[1]).toEqual(
        expect.objectContaining({
          type: 'success',
          message: 'Hello Citrus success',
          id: 1,
        } as Alert),
      );
    }));

    it('should close an alert correctly', inject([AlertService], (service: AlertService) => {
      const alert0 = service.addAlert({ type: 'info', message: 'Hello Citrus info' });
      const alert1 = service.addAlert({ type: 'info', message: 'Hello Citrus info 2' });
      const alert2 = service.addAlert({ type: 'success', message: 'Hello Citrus success' });
      expect(alert2).toEqual(
        expect.objectContaining({
          type: 'success',
          message: 'Hello Citrus success',
          id: 2,
        } as Alert),
      );

      expect(service.get().length).toBe(3);
      alert1.close?.(service.get());
      expect(service.get().length).toBe(2);
      expect(service.get()[1]).not.toEqual(
        expect.objectContaining({
          type: 'info',
          message: 'Hello Citrus info 2',
          id: 1,
        } as Alert),
      );
      alert2.close?.(service.get());
      expect(service.get().length).toBe(1);
      expect(service.get()[0]).not.toEqual(
        expect.objectContaining({
          type: 'success',
          message: 'Hello Citrus success',
          id: 2,
        } as Alert),
      );
      alert0.close?.(service.get());
      expect(service.get().length).toBe(0);
    }));

    it('should close an alert on timeout correctly', inject([AlertService], (service: AlertService) => {
      service.addAlert({ type: 'info', message: 'Hello Citrus info' });

      expect(service.get().length).toBe(1);

      jest.advanceTimersByTime(6000);

      expect(service.get().length).toBe(0);
    }));

    it('should clear alerts', inject([AlertService], (service: AlertService) => {
      service.addAlert({ type: 'info', message: 'Hello Citrus info' });
      service.addAlert({ type: 'danger', message: 'Hello Citrus info' });
      service.addAlert({ type: 'success', message: 'Hello Citrus info' });
      expect(service.get().length).toBe(3);
      service.clear();
      expect(service.get().length).toBe(0);
    }));

    it('should produce a scoped alert', inject([AlertService], (service: AlertService) => {
      expect(
        service.addAlert(
          {
            type: 'success',
            message: 'Hello Citrus',
            timeout: 3000,
            toast: true,
            position: 'top left',
          },
          [],
        ),
      ).toEqual(
        expect.objectContaining({
          type: 'success',
          message: 'Hello Citrus',
          id: 0,
          timeout: 3000,
          toast: true,
          position: 'top left',
        } as Alert),
      );

      expect(service.get().length).toBe(0);
    }));

    it('should produce a success message', inject([AlertService], (service: AlertService) => {
      expect(service.addAlert({ type: 'success', message: 'Hello Citrus' })).toEqual(
        expect.objectContaining({
          type: 'success',
          message: 'Hello Citrus',
        } as Alert),
      );
    }));

    it('should produce a success message with custom position', inject([AlertService], (service: AlertService) => {
      expect(service.addAlert({ type: 'success', message: 'Hello Citrus', position: 'bottom left' })).toEqual(
        expect.objectContaining({
          type: 'success',
          message: 'Hello Citrus',
          position: 'bottom left',
        } as Alert),
      );
    }));

    it('should produce a error message', inject([AlertService], (service: AlertService) => {
      expect(service.addAlert({ type: 'danger', message: 'Hello Citrus' })).toEqual(
        expect.objectContaining({
          type: 'danger',
          message: 'Hello Citrus',
        } as Alert),
      );
    }));

    it('should produce a warning message', inject([AlertService], (service: AlertService) => {
      expect(service.addAlert({ type: 'warning', message: 'Hello Citrus' })).toEqual(
        expect.objectContaining({
          type: 'warning',
          message: 'Hello Citrus',
        } as Alert),
      );
    }));

    it('should produce a info message', inject([AlertService], (service: AlertService) => {
      expect(service.addAlert({ type: 'info', message: 'Hello Citrus' })).toEqual(
        expect.objectContaining({
          type: 'info',
          message: 'Hello Citrus',
        } as Alert),
      );
    }));

    it('should produce a info message with translated message if key exists', inject(
      [AlertService, TranslateService],
      (service: AlertService, translateService: TranslateService) => {
        translateService.setTranslation('en', {
          'hello.jhipster': 'Translated message',
        });
        expect(service.addAlert({ type: 'info', message: 'Hello Citrus', translationKey: 'hello.jhipster' })).toEqual(
          expect.objectContaining({
            type: 'info',
            message: 'Translated message',
          } as Alert),
        );
      },
    ));

    it('should produce a info message with provided message if key does not exists', inject(
      [AlertService, TranslateService],
      (service: AlertService) => {
        expect(service.addAlert({ type: 'info', message: 'Hello Citrus', translationKey: 'hello.jhipster' })).toEqual(
          expect.objectContaining({
            type: 'info',
            message: 'Hello Citrus',
          } as Alert),
        );
      },
    ));

    it('should produce a info message with provided key if transltion key does not exist in translations and message is not provided', inject(
      [AlertService, TranslateService],
      (service: AlertService) => {
        expect(service.addAlert({ type: 'info', translationKey: 'hello.jhipster' })).toEqual(
          expect.objectContaining({
            type: 'info',
            message: 'hello.jhipster',
          } as Alert),
        );
      },
    ));
  });
});
