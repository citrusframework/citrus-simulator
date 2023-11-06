import { Component, OnInit, RendererFactory2, Renderer2 } from '@angular/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';

import { AppPageTitleStrategy } from 'app/app-page-title-strategy';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  providers: [AppPageTitleStrategy],
})
export default class MainComponent implements OnInit {
  private renderer: Renderer2;

  constructor(
    private router: Router,
    private appPageTitleStrategy: AppPageTitleStrategy,
    private translateService: TranslateService,
    rootRenderer: RendererFactory2,
  ) {
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
