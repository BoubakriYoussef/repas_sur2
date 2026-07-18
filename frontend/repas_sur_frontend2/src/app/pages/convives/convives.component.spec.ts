import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AllergeneDto } from '../../core/models/allergene.dto';
import { ConviveDto } from '../../core/models/convive.dto';
import { RegimeDto } from '../../core/models/regime.dto';
import { SiteRestaurationDto } from '../../core/models/site.dto';
import { AllergeneApiService } from '../../core/services/allergene-api.service';
import { ConviveApiService } from '../../core/services/convive-api.service';
import { RegimeApiService } from '../../core/services/regime-api.service';
import { SiteApiService } from '../../core/services/site-api.service';
import { ConvivesComponent } from './convives.component';

describe('ConvivesComponent', () => {
  let fixture: ComponentFixture<ConvivesComponent>;
  let component: ConvivesComponent;
  let conviveApi: jasmine.SpyObj<ConviveApiService>;
  let siteApi: jasmine.SpyObj<SiteApiService>;
  let allergeneApi: jasmine.SpyObj<AllergeneApiService>;
  let regimeApi: jasmine.SpyObj<RegimeApiService>;

  const sites: SiteRestaurationDto[] = [{ id: 10, nom: 'Site A', type: 'SCOLAIRE', adresse: null }];
  const allergenes: AllergeneDto[] = [{ id: 1, code: 'GLU', libelle: 'Gluten', description: null }];
  const regimes: RegimeDto[] = [{ id: 2, code: 'VG', libelle: 'Vegetarien', type: 'ALIMENTAIRE', description: null }];
  const convives: ConviveDto[] = [
    {
      id: 7,
      nom: 'Durand',
      prenom: 'Alice',
      typeConvive: 'ENFANT_SCOLAIRE',
      site: sites[0],
      allergenes: [{ id: 1, code: 'GLU', libelle: 'Gluten' }],
      regimes: [regimes[0]]
    }
  ];

  beforeEach(async () => {
    conviveApi = jasmine.createSpyObj<ConviveApiService>('ConviveApiService', ['getAll', 'create', 'update', 'delete']);
    siteApi = jasmine.createSpyObj<SiteApiService>('SiteApiService', ['getAll']);
    allergeneApi = jasmine.createSpyObj<AllergeneApiService>('AllergeneApiService', ['getAll']);
    regimeApi = jasmine.createSpyObj<RegimeApiService>('RegimeApiService', ['getAll']);

    conviveApi.getAll.and.returnValue(of(convives));
    conviveApi.create.and.returnValue(of(convives[0]));
    conviveApi.update.and.returnValue(of(convives[0]));
    conviveApi.delete.and.returnValue(of(void 0));
    siteApi.getAll.and.returnValue(of(sites));
    allergeneApi.getAll.and.returnValue(of(allergenes));
    regimeApi.getAll.and.returnValue(of(regimes));

    await TestBed.configureTestingModule({
      imports: [ConvivesComponent],
      providers: [
        { provide: ConviveApiService, useValue: conviveApi },
        { provide: SiteApiService, useValue: siteApi },
        { provide: AllergeneApiService, useValue: allergeneApi },
        { provide: RegimeApiService, useValue: regimeApi }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ConvivesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('charge les donnees et selectionne le premier site par defaut', () => {
    expect(component.rows).toEqual(convives);
    expect(component.sites).toEqual(sites);
    expect(component.form.getRawValue().siteId).toBe(10);
    expect(fixture.nativeElement.textContent).toContain('Alice Durand');
  });

  it('gere la selection et la deselection des allergenes et regimes', () => {
    component.toggleAllergene(1, true);
    component.toggleRegime(2, true);
    expect(component.isAllergeneSelected(1)).toBeTrue();
    expect(component.isRegimeSelected(2)).toBeTrue();

    component.toggleAllergene(1, false);
    component.toggleRegime(2, false);
    expect(component.isAllergeneSelected(1)).toBeFalse();
    expect(component.isRegimeSelected(2)).toBeFalse();
  });

  it('n ajoute pas de convive si le formulaire est invalide', () => {
    component.form.patchValue({ nom: '', prenom: '' });

    component.submit();

    expect(conviveApi.create).not.toHaveBeenCalled();
  });

  it('cree un convive avec les conversions attendues', () => {
    component.form.setValue({
      nom: 'Martin',
      prenom: 'Bob',
      typeConvive: 'ETUDIANT',
      siteId: '10' as unknown as number,
      allergeneIds: ['1'] as unknown as number[],
      regimeIds: ['2'] as unknown as number[]
    });

    component.submit();

    expect(conviveApi.create).toHaveBeenCalledWith({
      nom: 'Martin',
      prenom: 'Bob',
      typeConvive: 'ETUDIANT',
      siteId: 10,
      allergeneIds: [1],
      regimeIds: [2]
    });
  });

  it('passe en edition, met a jour puis reinitialise le formulaire', () => {
    component.startEdit(convives[0]);
    expect(component.editingId).toBe(7);

    component.form.patchValue({ nom: 'Dupond' });
    component.submit();

    expect(conviveApi.update).toHaveBeenCalledWith(7, {
      nom: 'Dupond',
      prenom: 'Alice',
      typeConvive: 'ENFANT_SCOLAIRE',
      siteId: 10,
      allergeneIds: [1],
      regimeIds: [2]
    });

    component.cancelEdit();
    expect(component.form.getRawValue()).toEqual({
      nom: '',
      prenom: '',
      typeConvive: 'ENFANT_SCOLAIRE',
      siteId: 10,
      allergeneIds: [],
      regimeIds: []
    });
  });

  it('supprime un convive et recharge', () => {
    component.remove(7);

    expect(conviveApi.delete).toHaveBeenCalledWith(7);
    expect(conviveApi.getAll).toHaveBeenCalledTimes(2);
  });

  it('formate les listes vides en tiret', () => {
    expect(component.formatRegimes({ ...convives[0], regimes: [] })).toBe('-');
    expect(component.formatAllergenes({ ...convives[0], allergenes: [] })).toBe('-');
  });
});
