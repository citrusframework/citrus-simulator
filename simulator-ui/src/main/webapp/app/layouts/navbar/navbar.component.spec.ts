import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideTranslateService } from '@ngx-translate/core';

import NavbarComponent from './navbar.component';
import { provideRouter } from '@angular/router';

describe('Navbar Component', () => {
  let fixture: ComponentFixture<NavbarComponent>;
  let component: NavbarComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavbarComponent],
      providers: [provideRouter([]), provideTranslateService()],
    })
      .overrideTemplate(NavbarComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
  });

  it('extracts version from environment', () => {
    expect(component.version).toBeTruthy();
  });
});
