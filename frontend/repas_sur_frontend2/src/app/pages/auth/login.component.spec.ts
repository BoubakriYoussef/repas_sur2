import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, Subject, throwError } from 'rxjs';
import { AuthService, AuthResponse, LoginRequest } from '../../core/services/auth.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;
  let navigateByUrlSpy: jasmine.Spy;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    navigateByUrlSpy = spyOn(router, 'navigateByUrl').and.resolveTo(true);
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('cree le composant avec le formulaire initial invalide', () => {
    expect(component.form.invalid).toBeTrue();
    expect(component.form.getRawValue()).toEqual({ username: '', password: '' });
    expect(component.error).toBe('');
    expect(component.isLoading).toBeFalse();
  });

  it('valide les champs obligatoires dans le template', () => {
    component.form.controls.username.markAsTouched();
    component.form.controls.password.markAsTouched();
    fixture.detectChanges();

    expect(component.form.controls.username.hasError('required')).toBeTrue();
    expect(component.form.controls.password.hasError('required')).toBeTrue();
    expect(fixture.nativeElement.querySelector('button[type="submit"]').textContent).toContain('Se connecter');
  });

  it('n appelle pas AuthService si le formulaire est invalide', () => {
    component.submit();

    expect(authService.login).not.toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
  });

  it('n appelle pas AuthService si une soumission est deja en cours', () => {
    component.form.setValue({ username: 'admin', password: 'secret' });
    component.isLoading = true;

    component.submit();

    expect(authService.login).not.toHaveBeenCalled();
  });

  it('appelle AuthService avec les bonnes donnees quand le formulaire est valide', () => {
    const response: AuthResponse = { token: 'jwt', role: 'ADMIN' };
    authService.login.and.returnValue(of(response));
    component.form.setValue({ username: 'admin', password: 'secret' });

    component.submit();

    expect(authService.login).toHaveBeenCalledWith({ username: 'admin', password: 'secret' } as LoginRequest);
    expect(navigateByUrlSpy).toHaveBeenCalledWith('/dashboard');
    expect(component.isLoading).toBeFalse();
  });

  it('affiche l etat de chargement et desactive le bouton pendant la connexion', () => {
    const pending$ = new Subject<AuthResponse>();
    authService.login.and.returnValue(pending$);
    component.form.setValue({ username: 'admin', password: 'secret' });

    component.submit();
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(component.isLoading).toBeTrue();
    expect(button.disabled).toBeTrue();
    expect(button.textContent).toContain('Connexion...');

    pending$.next({ token: 'jwt', role: 'ADMIN' });
    pending$.complete();
  });

  it('affiche le message d erreur retourne par l API', () => {
    authService.login.and.returnValue(throwError(() => ({ error: 'Identifiants invalides' })));
    component.form.setValue({ username: 'admin', password: 'bad' });

    component.submit();
    fixture.detectChanges();

    expect(component.error).toBe('Identifiants invalides');
    expect(component.isLoading).toBeFalse();
    expect(navigateByUrlSpy).not.toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain('Identifiants invalides');
  });

  it('affiche un message d erreur par defaut quand l API ne fournit rien', () => {
    authService.login.and.returnValue(throwError(() => new Error('boom')));
    component.form.setValue({ username: 'admin', password: 'bad' });

    component.submit();

    expect(component.error).toBe('Echec de connexion');
  });

  it('soumet le formulaire via le bouton du template', () => {
    authService.login.and.returnValue(of({ token: 'jwt', role: 'ADMIN' }));
    component.form.setValue({ username: 'admin', password: 'secret' });
    fixture.detectChanges();

    (fixture.nativeElement.querySelector('button[type="submit"]') as HTMLButtonElement).click();

    expect(authService.login).toHaveBeenCalledTimes(1);
  });

  it('vide le message precedent avant une nouvelle tentative', () => {
    authService.login.and.returnValue(of({ token: 'jwt', role: 'ADMIN' }));
    component.error = 'ancienne erreur';
    component.form.setValue({ username: 'admin', password: 'secret' });

    component.submit();

    expect(component.error).toBe('');
  });
});
