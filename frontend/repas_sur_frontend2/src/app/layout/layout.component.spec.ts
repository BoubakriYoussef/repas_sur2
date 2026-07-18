import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { LayoutComponent } from './layout.component';

describe('LayoutComponent', () => {
  let fixture: ComponentFixture<LayoutComponent>;
  let component: LayoutComponent;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;
  let navigateByUrlSpy: jasmine.Spy;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['logout']);

    await TestBed.configureTestingModule({
      imports: [LayoutComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    navigateByUrlSpy = spyOn(router, 'navigateByUrl').and.resolveTo(true);
    fixture = TestBed.createComponent(LayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('cree le composant et affiche toute la navigation', () => {
    const links = Array.from(fixture.nativeElement.querySelectorAll('aside nav a')) as HTMLAnchorElement[];

    expect(component.nav.length).toBe(10);
    expect(links.length).toBe(component.nav.length);
    expect(links.map((link) => link.textContent?.trim())).toContain('Dashboard');
    expect(fixture.nativeElement.textContent).toContain('Nouveau service');
  });

  it('declenche la deconnexion et navigue vers /login', () => {
    component.logout();

    expect(authService.logout).toHaveBeenCalled();
    expect(navigateByUrlSpy).toHaveBeenCalledWith('/login');
  });

  it('relie le bouton deconnexion a la methode logout', () => {
    const logoutSpy = spyOn(component, 'logout').and.callThrough();

    (fixture.nativeElement.querySelector('button[type="button"]') as HTMLButtonElement).click();

    expect(logoutSpy).toHaveBeenCalled();
    expect(authService.logout).toHaveBeenCalled();
  });

  it('affiche le lien mobile de connexion et la zone de recherche', () => {
    const text = fixture.nativeElement.textContent as string;

    expect(text).toContain('Connexion');
    expect(fixture.nativeElement.querySelector('input[placeholder="Convive, menu, allergene"]')).toBeTruthy();
  });
});
