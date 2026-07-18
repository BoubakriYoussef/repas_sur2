import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { SiteRestaurationDto } from '../../core/models/site.dto';
import { UtilisateurDto } from '../../core/models/utilisateur.dto';
import { SiteApiService } from '../../core/services/site-api.service';
import { UtilisateurApiService } from '../../core/services/utilisateur-api.service';
import { UtilisateursComponent } from './utilisateurs.component';

describe('UtilisateursComponent', () => {
  let fixture: ComponentFixture<UtilisateursComponent>;
  let component: UtilisateursComponent;
  let utilisateurApi: jasmine.SpyObj<UtilisateurApiService>;
  let siteApi: jasmine.SpyObj<SiteApiService>;

  const sites: SiteRestaurationDto[] = [{ id: 1, nom: 'Site A', type: 'SCOLAIRE', adresse: null }];
  const users: UtilisateurDto[] = [
    { id: 1, username: 'admin', email: 'a@a.fr', telephone: '01', poste: 'Chef', role: 'ADMIN', actif: true, site: sites[0] },
    { id: 2, username: 'resp', email: null, telephone: null, poste: null, role: 'RESPONSABLE', actif: false, site: null }
  ];

  beforeEach(async () => {
    utilisateurApi = jasmine.createSpyObj<UtilisateurApiService>('UtilisateurApiService', ['getAll', 'create', 'update', 'delete']);
    siteApi = jasmine.createSpyObj<SiteApiService>('SiteApiService', ['getAll']);
    utilisateurApi.getAll.and.returnValue(of(users));
    utilisateurApi.create.and.returnValue(of(users[0]));
    utilisateurApi.update.and.returnValue(of(users[0]));
    utilisateurApi.delete.and.returnValue(of(void 0));
    siteApi.getAll.and.returnValue(of(sites));

    await TestBed.configureTestingModule({
      imports: [UtilisateursComponent],
      providers: [
        { provide: UtilisateurApiService, useValue: utilisateurApi },
        { provide: SiteApiService, useValue: siteApi }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UtilisateursComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('charge les utilisateurs, les sites et applique les filtres initiaux', () => {
    expect(component.utilisateurs).toEqual(users);
    expect(component.filtered).toEqual(users);
    expect(component.sites).toEqual(sites);
    expect(fixture.nativeElement.textContent).toContain('admin');
  });

  it('filtre par role et statut puis reinitialise', () => {
    component.filterForm.setValue({ role: 'ADMIN', actif: 'ACTIF' });
    component.applyFilters();
    expect(component.filtered).toEqual([users[0]]);

    component.filterForm.setValue({ role: 'TOUT', actif: 'INACTIF' });
    component.applyFilters();
    expect(component.filtered).toEqual([users[1]]);

    component.resetFilters();
    expect(component.filterForm.getRawValue()).toEqual({ role: 'TOUT', actif: 'TOUT' });
    expect(component.filtered.length).toBe(2);
  });

  it('n appelle pas l API quand le formulaire est invalide', () => {
    component.form.patchValue({ username: '' });
    component.submit();
    expect(utilisateurApi.create).not.toHaveBeenCalled();
  });

  it('cree un utilisateur et convertit le mot de passe vide en null', () => {
    component.form.setValue({
      username: 'new',
      password: '',
      email: 'new@test.fr',
      telephone: '02',
      poste: 'Resp',
      role: 'RESPONSABLE',
      actif: true,
      siteId: '1' as unknown as number | null
    });

    component.submit();

    expect(utilisateurApi.create).toHaveBeenCalledWith({
      username: 'new',
      password: null,
      email: 'new@test.fr',
      telephone: '02',
      poste: 'Resp',
      role: 'RESPONSABLE',
      actif: true,
      siteId: 1
    });
  });

  it('met a jour un utilisateur en mode edition', () => {
    component.startEdit(users[0]);
    component.form.patchValue({ password: 'secret' });

    component.submit();

    expect(utilisateurApi.update).toHaveBeenCalledWith(1, jasmine.objectContaining({ password: 'secret' }));
  });

  it('supprime un utilisateur et recharge', () => {
    component.remove(1);
    expect(utilisateurApi.delete).toHaveBeenCalledWith(1);
  });

  it('affiche les messages adaptes pour 403, 401 et erreur generique', async () => {
    utilisateurApi.getAll.and.returnValue(throwError(() => new HttpErrorResponse({ status: 403 })));
    siteApi.getAll.and.returnValue(throwError(() => new HttpErrorResponse({ status: 401 })));
    await TestBed.resetTestingModule();
    await TestBed.configureTestingModule({
      imports: [UtilisateursComponent],
      providers: [
        { provide: UtilisateurApiService, useValue: utilisateurApi },
        { provide: SiteApiService, useValue: siteApi }
      ]
    }).compileComponents();

    const errorFixture = TestBed.createComponent(UtilisateursComponent);
    errorFixture.detectChanges();
    expect(errorFixture.componentInstance.errorMessage).toBe('Session invalide ou expiree. Merci de vous reconnecter.');

    component['handleError'](new HttpErrorResponse({ status: 500 }), 'test');
    expect(component.errorMessage).toBe('Erreur lors de test.');
  });
});
