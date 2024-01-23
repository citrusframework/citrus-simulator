import { ChangeDetectorRef, Component, NgZone, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';

import { combineLatest, Observable, switchMap, tap } from 'rxjs';

import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { ASC, DEFAULT_SORT_DATA, DESC, EntityOrder, SORT, toEntityOrder } from 'app/config/navigation.constants';

import { UserPreferenceService } from 'app/core/config/user-preference.service';

import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { AlertService } from 'app/core/util/alert.service';
import { FilterComponent, IFilterOption } from 'app/shared/filter';
import { ItemCountComponent } from 'app/shared/pagination';
import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective } from 'app/shared/sort';

import { EntityArrayResponseType, ScenarioService } from '../service/scenario.service';
import { IScenario } from '../scenario.model';
import { filter, map } from 'rxjs/operators';

@Component({
  standalone: true,
  selector: 'app-scenario',
  templateUrl: './scenario.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    FilterComponent,
    ItemCountComponent,
  ],
})
export class ScenarioComponent implements OnInit {
  scenarios?: IScenario[];
  isLoading = false;

  predicate = 'id';
  ascending = true;
  entityOrder: EntityOrder = EntityOrder.ASCENDING;

  itemsPerPage = ITEMS_PER_PAGE;

  totalItems = 0;
  page = 1;

  protected readonly USER_PREFERENCES_KEY = 'scenario';

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
  }

  load(): void {
    this.loadFromBackendWithRouteInformation().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues({ predicate, ascending }: { predicate: string; ascending: boolean }): void {
    this.updateUserPreferences(predicate, ascending);
    this.handleNavigation(this.page, predicate, ascending);
  }

  navigateToPage(page = this.page): void {
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
    return this.scenarioService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page = this.page, predicate?: string, ascending?: boolean, filterOptions?: IFilterOption[]): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    filterOptions?.forEach(filterOption => {
      queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
    });

    this.ngZone.run(() =>
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      }),
    );
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = true): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
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
}
