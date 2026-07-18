import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AllergeneDto } from '../../core/models/allergene.dto';
import { PlatDto } from '../../core/models/plat.dto';
import { AllergeneApiService } from '../../core/services/allergene-api.service';
import { PlatApiService } from '../../core/services/plat-api.service';
import { PlatsComponent } from './plats.component';

describe('PlatsComponent', () => {
  let fixture: ComponentFixture<PlatsComponent>;
  let component: PlatsComponent;
  let platApi: jasmine.SpyObj<PlatApiService>;
  let allergeneApi: jasmine.SpyObj<AllergeneApiService>;

  const allergenes: AllergeneDto[] = [{ id: 1, code: 'GLU', libelle: 'Gluten', description: null }];
  const plats: PlatDto[] = [
    { id: 4, nom: 'Pates', categorie: 'PLAT_PRINCIPAL', description: 'Desc', contientPorc: false, estVegetarien: true, allergenes }
  ];

  beforeEach(async () => {
    platApi = jasmine.createSpyObj<PlatApiService>('PlatApiService', ['getAll', 'create', 'update', 'delete']);
    allergeneApi = jasmine.createSpyObj<AllergeneApiService>('AllergeneApiService', ['getAll']);
    platApi.getAll.and.returnValue(of(plats));
    platApi.create.and.returnValue(of(plats[0]));
    platApi.update.and.returnValue(of(plats[0]));
    platApi.delete.and.returnValue(of(void 0));
    allergeneApi.getAll.and.returnValue(of(allergenes));

    await TestBed.configureTestingModule({
      imports: [PlatsComponent],
      providers: [
        { provide: PlatApiService, useValue: platApi },
        { provide: AllergeneApiService, useValue: allergeneApi }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PlatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('charge les plats et les allergenes', () => {
    expect(component.plats).toEqual(plats);
    expect(component.categories).toContain('AUTRE');
    expect(fixture.nativeElement.textContent).toContain('Pates');
  });

  it('gere la selection des allergenes', () => {
    component.toggleAllergene(1, true);
    expect(component.isAllergeneSelected(1)).toBeTrue();
    component.toggleAllergene(1, false);
    expect(component.isAllergeneSelected(1)).toBeFalse();
  });

  it('bloque la soumission invalide', () => {
    component.form.patchValue({ nom: '' });
    component.submit();
    expect(platApi.create).not.toHaveBeenCalled();
  });

  it('cree puis met a jour un plat', () => {
    component.form.setValue({
      nom: 'Soupe',
      categorie: 'ENTREE',
      description: 'Chaude',
      contientPorc: false,
      estVegetarien: true,
      allergeneIds: [1]
    });
    component.submit();
    expect(platApi.create).toHaveBeenCalledWith({
      nom: 'Soupe',
      categorie: 'ENTREE',
      description: 'Chaude',
      contientPorc: false,
      estVegetarien: true,
      allergeneIds: [1]
    });

    component.startEdit(plats[0]);
    component.form.patchValue({ nom: 'Pates bio' });
    component.submit();
    expect(platApi.update).toHaveBeenCalledWith(4, {
      nom: 'Pates bio',
      categorie: 'PLAT_PRINCIPAL',
      description: 'Desc',
      contientPorc: false,
      estVegetarien: true,
      allergeneIds: [1]
    });
  });

  it('annule l edition, supprime et formate les allergenes', () => {
    component.startEdit(plats[0]);
    component.cancelEdit();
    expect(component.form.getRawValue()).toEqual({
      nom: '',
      categorie: 'ENTREE',
      description: '',
      contientPorc: false,
      estVegetarien: false,
      allergeneIds: []
    });

    component.remove(4);
    expect(platApi.delete).toHaveBeenCalledWith(4);
    expect(component.formatAllergenes(plats[0])).toBe('Gluten');
    expect(component.formatAllergenes({ ...plats[0], allergenes: [] })).toBe('-');
  });
});
