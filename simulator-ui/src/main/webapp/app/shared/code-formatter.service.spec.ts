import { TestBed } from '@angular/core/testing';
import prettierPluginBabel from 'prettier/plugins/babel';
import prettierPluginEstree from 'prettier/plugins/estree';
import prettierPluginHtml from 'prettier/plugins/html';
import { CodeFormatterService } from './code-formatter.service';

jest.mock('prettier/standalone', () => ({
  format: jest.fn(),
}));

import * as prettier from 'prettier/standalone';

describe('CodeFormatterService', () => {
  let service: CodeFormatterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CodeFormatterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should format JSON correctly', done => {
    const inputCode = '{"key": "value"}';
    const expectedOutput = '{\n  "key": "value"\n}';

    (prettier.format as jest.Mock).mockResolvedValueOnce(expectedOutput);

    service.formatCode(inputCode).subscribe(result => {
      expect(result).toBe(expectedOutput);
      expect(prettier.format).toHaveBeenCalledWith(inputCode, {
        parser: 'json',
        plugins: [prettierPluginBabel, prettierPluginEstree],
      });

      done();
    });
  });

  it('should fall back to HTML formatting if JSON fails', done => {
    const inputCode = '<div>Hello</div>';
    const expectedOutput = '<div>\n  Hello\n</div>';

    (prettier.format as jest.Mock).mockRejectedValueOnce(new Error('JSON parsing failed'));
    (prettier.format as jest.Mock).mockResolvedValueOnce(expectedOutput);

    service.formatCode(inputCode).subscribe(result => {
      expect(result).toBe(expectedOutput);
      expect(prettier.format).toHaveBeenCalledWith(inputCode, {
        parser: 'html',
        plugins: [prettierPluginHtml],
      });

      done();
    });
  });

  it('should return original code if both JSON and HTML formatting fail', done => {
    const inputCode = 'unparseable code';

    (prettier.format as jest.Mock).mockRejectedValue(new Error('Parsing failed'));

    service.formatCode(inputCode).subscribe(result => {
      expect(result).toBe(inputCode);
      done();
    });
  });
});
