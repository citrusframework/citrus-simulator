import { ChangeDetectorRef, Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';

import { combineLatest, debounceTime, Observable, Subscription, switchMap, tap } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import { DEBOUNCE_TIME_MILLIS } from 'app/config/input.constants';
import { DEFAULT_SORT_DATA, EntityOrder, SORT, toEntityOrder } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';

import { UserPreferenceService } from 'app/core/config/user-preference.service';
import { AlertService } from 'app/core/util/alert.service';

import SharedModule from 'app/shared/shared.module';
import { IFilterOption } from 'app/shared/filter';
import { ItemCountComponent } from 'app/shared/pagination';
import { SortByDirective, SortDirective } from 'app/shared/sort';

import { EntityArrayResponseType, ScenarioService } from '../service/scenario.service';
import { IScenario } from '../scenario.model';

import { navigateToWithPagingInformation } from '../../entities/navigation-util';

type ScenarioFilter = {
  nameContains: string | undefined;
};

@Component({
  standalone: true,
  selector: 'app-scenario',
  templateUrl: './scenario.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, ItemCountComponent, ReactiveFormsModule],
})
export class ScenarioComponent implements OnDestroy, OnInit {
  filterForm: FormGroup = new FormGroup({
    nameContains: new FormControl(),
  });

  scenarios?: IScenario[];
  isLoading = false;

  predicate = 'name';
  ascending = true;
  entityOrder: EntityOrder = EntityOrder.ASCENDING;

  itemsPerPage = ITEMS_PER_PAGE;

  totalItems = 0;
  page = 1;

  protected readonly USER_PREFERENCES_KEY = 'scenario';

  private filterFormValueChanges: Subscription | null = null;

  constructor(
    public router: Router,
    protected scenarioService: ScenarioService,
    protected activatedRoute: ActivatedRoute,
    private alertService: AlertService,
    private ngZone: NgZone,
    private userPreferenceService: UserPreferenceService,
    private changeDetectorRef: ChangeDetectorRef,
  ) {}

  trackId = (_index: number, item: IScenario): string => this.scenarioService.getScenarioIdentifier(item);

  ngOnInit(): void {
    this.itemsPerPage = this.userPreferenceService.getPageSize(this.USER_PREFERENCES_KEY);
    this.predicate = this.userPreferenceService.getPredicate(this.USER_PREFERENCES_KEY, this.predicate);
    this.entityOrder = this.userPreferenceService.getEntityOrder(this.USER_PREFERENCES_KEY);
    this.ascending = this.entityOrder === EntityOrder.ASCENDING;

    this.navigateToWithComponentValues({ predicate: this.predicate, ascending: this.ascending });
    this.load();
    this.changeDetectorRef.detectChanges();

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

  protected navigateToWithComponentValues({ predicate, ascending }: { predicate: string; ascending: boolean }): void {
    this.updateUserPreferences(predicate, ascending);
    this.handleNavigation(this.page, predicate, ascending);
  }

  protected navigateToPage(page = this.page): void {
    this.handleNavigation(page, this.predicate, this.ascending);
  }

  protected loadFromBackendWithRouteInformation(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.page, this.predicate, this.ascending)),
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.entityOrder = toEntityOrder(sort[1]) ?? EntityOrder.ASCENDING;
    this.ascending = this.entityOrder === EntityOrder.ASCENDING;
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.scenarios = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: IScenario[] | null): IScenario[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(page?: number, predicate?: string, ascending?: boolean): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const pageToLoad: number = page ?? 1;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    if (this.filterForm.value.nameContains) {
      queryObject.nameContains = this.filterForm.value.nameContains;
    }

    return this.scenarioService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page = this.page, predicate?: string, ascending?: boolean, filterOptions?: IFilterOption[]): void {
    navigateToWithPagingInformation(
      page,
      this.itemsPerPage,
      () => this.getSortQueryParam(predicate, ascending),
      this.ngZone,
      this.router,
      this.activatedRoute,
      predicate,
      ascending,
      filterOptions,
    );
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = true): string[] {
    const ascendingQueryParam = ascending ? EntityOrder.ASCENDING : EntityOrder.DESCENDING;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }

  protected launch(scenario: IScenario): void {
    this.scenarioService
      .launch(scenario.name)
      .pipe(
        filter(response => !!response.body),
        map(response => response.body),
      )
      .subscribe({
        next: scenarioExecutionId => {
          this.alertService.addAlert({
            type: 'success',
            translationKey: 'citrusSimulatorApp.scenario.action.launchedSuccessfully',
            translationParams: { scenarioExecutionId },
          });
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

  private updateUserPreferences(predicate: string, ascending: boolean): void {
    this.userPreferenceService.setPredicate(this.USER_PREFERENCES_KEY, predicate);
    this.userPreferenceService.setEntityOrder(this.USER_PREFERENCES_KEY, ascending ? EntityOrder.ASCENDING : EntityOrder.DESCENDING);
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
