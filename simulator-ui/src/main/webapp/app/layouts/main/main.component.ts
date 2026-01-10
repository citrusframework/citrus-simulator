import { Component, OnInit, RendererFactory2, Renderer2, inject } from '@angular/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';

import { AppPageTitleStrategy } from 'app/app-page-title-strategy';
import { Router, RouterModule } from '@angular/router';
import SharedModule from 'app/shared/shared.module';
import FooterComponent from '../footer/footer.component';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  imports: [SharedModule, RouterModule, FooterComponent],
  providers: [AppPageTitleStrategy],
})
export default class MainComponent implements OnInit {
  private router = inject(Router);
  private appPageTitleStrategy = inject(AppPageTitleStrategy);
  private translateService = inject(TranslateService);

  private renderer: Renderer2;

  constructor() {
    const rootRenderer = inject(RendererFactory2);

    this.renderer = rootRenderer.createRenderer(document.querySelector('html'), null);
  }

  ngOnInit(): void {
    this.translateService.onLangChange.subscribe((langChangeEvent: LangChangeEvent) => {
      this.appPageTitleStrategy.updateTitle(this.router.routerState.snapshot);
      dayjs.locale(langChangeEvent.lang);
      this.renderer.setAttribute(document.querySelector('html'), 'lang', langChangeEvent.lang);
    });
  }
}
