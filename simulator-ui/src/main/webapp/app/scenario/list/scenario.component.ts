import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';

import { combineLatest, debounceTime, Observable, of, Subscription, switchMap, tap } from 'rxjs';

import { DEBOUNCE_TIME_MILLIS } from 'app/config/input.constants';
import { DEFAULT_SORT_DATA, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';

import { UserPreferenceService } from 'app/core/config/user-preference.service';
import { AlertService } from 'app/core/util/alert.service';

import SharedModule from 'app/shared/shared.module';
import { IFilterOption } from 'app/shared/filter';
import { ItemCount, SelectPageSize } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, SortState, sortStateSignal } from 'app/shared/sort';
import { ParamsDialogComponent } from '../params/params-dialog.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { EntityArrayResponseType, ScenarioService } from '../service/scenario.service';
import { IScenario } from '../scenario.model';

import { navigateToWithPagingInformation } from 'app/entities/navigation-util';
import { IScenarioParameter } from 'app/entities/scenario-parameter/scenario-parameter.model';

type ScenarioFilter = {
  nameContains: string | undefined;
};

const USER_PREFERENCES_KEY = 'scenario';

@Component({
  standalone: true,
  selector: 'app-scenario',
  templateUrl: './scenario.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, ItemCount, SelectPageSize, ReactiveFormsModule],
})
export class ScenarioComponent implements OnDestroy, OnInit {
  userPreferencesKey = USER_PREFERENCES_KEY;

  filterForm: FormGroup = new FormGroup({
    nameContains: new FormControl(),
  });

  scenarios?: IScenario[];
  isLoading = false;

  sortState = sortStateSignal({ predicate: 'name' });

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  readonly router = inject(Router);
  readonly scenarioService = inject(ScenarioService);

  protected readonly activatedRoute = inject(ActivatedRoute);

  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  protected readonly alertService = inject(AlertService);

  protected userPreferenceService = inject(UserPreferenceService);

  private filterFormValueChanges: Subscription | null = null;

  trackId = (_index: number, item: IScenario): string => this.scenarioService.getScenarioIdentifier(item);

  ngOnInit(): void {
    this.itemsPerPage = this.userPreferenceService.getPageSize(USER_PREFERENCES_KEY);
    this.sortState = this.userPreferenceService.getSortState(USER_PREFERENCES_KEY, 'name');

    this.navigateToWithComponentValues(this.sortState());
    this.load();

    this.automaticApplyOnFormValueChanges();
  }

  ngOnDestroy(): void {
    this.filterFormValueChanges?.unsubscribe();
  }

  load(): void {
    this.loadFromBackendWithRouteInformation().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  protected applyFilter(formValue = this.filterForm.value): void {
    this.router
      .navigate([], {
        queryParams: {
          ...this.getFilterQueryParameter({
            nameContains: formValue.nameContains,
          }),
        },
      })
      .catch(() => location.reload());
  }

  protected resetFilter(): void {
    this.filterForm.reset();
    this.filterForm.markAsPristine();
    this.applyFilter();
  }

  protected navigateToWithComponentValues(event: SortState): void {
    this.userPreferenceService.setSortState(USER_PREFERENCES_KEY, event);
    this.handleNavigation(this.page, event);
  }

  protected navigateToPage(page = this.page): void {
    this.handleNavigation(page);
  }

  protected loadFromBackendWithRouteInformation(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend()),
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    this.scenarios = this.fillComponentAttributesFromResponseBody(response.body);
  }

  protected fillComponentAttributesFromResponseBody(data: IScenario[] | null): IScenario[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const pageToLoad: number = this.page;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(this.sortState()),
    };

    if (this.filterForm.value.nameContains) {
      queryObject.nameContains = this.filterForm.value.nameContains;
    }

    return this.scenarioService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, sortState: SortState = this.sortState(), filterOptions?: IFilterOption[]): void {
    navigateToWithPagingInformation(
      page,
      this.itemsPerPage,
      this.sortService,
      sortState,

      this.router,
      this.activatedRoute,
      filterOptions,
    );
  }

  protected launch(scenario: IScenario): void {
    this.scenarioService
      .findParameters(scenario.name)
      .pipe(
        switchMap(response => {
          const parameters = response.body;
          if (Array.isArray(parameters) && parameters.length > 0) {
            const modalRef = this.modalService.open(ParamsDialogComponent, { size: 'lg' });
            modalRef.componentInstance.params = parameters;

            return modalRef.result.then(
              (resultParams: IScenarioParameter[]) => resultParams,
              () => null, // Return null if modal is dismissed
            );
          }
          return of(null); // No parameters, proceed with empty array
        }),
        switchMap(params => {
          if (params === null) {
            // Modal dismissed, do not proceed with launch
            return of(null);
          }
          return this.scenarioService.launch(scenario.name, params);
        }),
      )
      .subscribe({
        next: launchResponse => {
          if (launchResponse?.body) {
            this.alertService.addAlert({
              type: 'success',
              translationKey: 'citrusSimulatorApp.scenario.action.launchedSuccessfully',
              translationParams: { scenarioExecutionId: launchResponse.body },
            });
          }
        },
        error: () => {
          this.alertService.addAlert({
            type: 'danger',
            translationKey: 'citrusSimulatorApp.scenario.action.launchFailed',
          });
        },
      });
  }

  protected pageSizeChanged(itemsPerPage: number): void {
    this.itemsPerPage = itemsPerPage;
    this.load();
  }

  private automaticApplyOnFormValueChanges(): void {
    this.filterFormValueChanges = this.filterForm.valueChanges.pipe(debounceTime(DEBOUNCE_TIME_MILLIS)).subscribe({
      next: values => this.applyFilter(values),
    });
  }

  private getFilterQueryParameter({ nameContains }: ScenarioFilter): Record<string, any> {
    return {
      'filter[scenarioName.contains]': nameContains ?? undefined,
    };
  }
}
