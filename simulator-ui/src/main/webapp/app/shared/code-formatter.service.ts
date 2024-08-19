import { Injectable } from '@angular/core';

import * as prettier from 'prettier/standalone';

import prettierPluginBabel from 'prettier/plugins/babel';
import prettierPluginEstree from 'prettier/plugins/estree';
import prettierPluginHtml from 'prettier/plugins/html';

import { catchError, from, Observable, of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CodeFormatterService {
  public formatCode(code: string): Observable<string> {
    return from(
      prettier.format(code, {
        parser: 'json',
        plugins: [prettierPluginBabel, prettierPluginEstree],
      }),
    )
      .pipe(
        catchError(() =>
          from(
            prettier.format(code, {
              parser: 'html',
              plugins: [prettierPluginHtml],
            }),
          ),
        ),
      )
      .pipe(catchError(_ => of(code)));
  }
}
