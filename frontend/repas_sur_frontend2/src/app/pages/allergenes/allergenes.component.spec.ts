import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { AllergeneDto } from '../../core/models/allergene.dto';
import { AllergeneApiService } from '../../core/services/allergene-api.service';
import { AllergenesComponent } from './allergenes.component';

describe('AllergenesComponent', () => {
  let fixture: ComponentFixture<AllergenesComponent>;
  let component: AllergenesComponent;
  let api: jasmine.SpyObj<AllergeneApiService>;
  const items: AllergeneDto[] = [
    { id: 1, code: 'GLU', libelle: 'Gluten', description: 'Cereales' }
  ];

  beforeEach(async () => {
    api = jasmine.createSpyObj<AllergeneApiService>('AllergeneApiService', ['getAll', 'create', 'update', 'delete']);
    api.getAll.and.returnValue(of(items));
    api.create.and.returnValue(of(items[0]));
    api.update.and.returnValue(of(items[0]));
    api.delete.and.returnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [AllergenesComponent],
      providers: [{ provide: AllergeneApiService, useValue: api }]
    }).compileComponents();

    fixture = TestBed.createComponent(AllergenesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('charge et affiche les allergenes', () => {
    expect(api.getAll).toHaveBeenCalled();
    expect(component.allergenes).toEqual(items);
    expect(fixture.nativeElement.textContent).toContain('Gluten');
  });

  it('garde isLoading a false apres une erreur de chargement', async () => {
    api.getAll.and.returnValue(throwError(() => new Error('boom')));
    await TestBed.resetTestingModule();
    await TestBed.configureTestingModule({
      imports: [AllergenesComponent],
      providers: [{ provide: AllergeneApiService, useValue: api }]
    }).compileComponents();

    const errorFixture = TestBed.createComponent(AllergenesComponent);
    errorFixture.detectChanges();

    expect(errorFixture.componentInstance.isLoading).toBeFalse();
    expect(errorFixture.componentInstance.allergenes).toEqual([]);
  });

  it('n envoie rien si le formulaire est invalide', () => {
    component.submit();

    expect(api.create).not.toHaveBeenCalled();
    expect(api.update).not.toHaveBeenCalled();
  });

  it('cree un allergene puis reinitialise le formulaire', () => {
    component.form.setValue({ code: 'ARA', libelle: 'Arachide', description: 'Test' });

    component.submit();

    expect(api.create).toHaveBeenCalledWith({ code: 'ARA', libelle: 'Arachide', description: 'Test' });
    expect(component.editingId).toBeNull();
    expect(component.form.getRawValue()).toEqual({ code: '', libelle: '', description: '' });
    expect(api.getAll).toHaveBeenCalledTimes(2);
  });

  it('passe en mode edition puis met a jour', () => {
    component.startEdit(items[0]);
    expect(component.editingId).toBe(1);
    expect(component.form.getRawValue().code).toBe('GLU');

    component.form.patchValue({ libelle: 'Gluten modifie' });
    component.submit();

    expect(api.update).toHaveBeenCalledWith(1, { code: 'GLU', libelle: 'Gluten modifie', description: 'Cereales' });
  });

  it('annule l edition et masque le bouton annuler', () => {
    component.startEdit(items[0]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Annuler');

    component.cancelEdit();
    fixture.detectChanges();

    expect(component.editingId).toBeNull();
    expect(fixture.nativeElement.textContent).not.toContain('Annuler');
  });

  it('supprime un allergene puis recharge la liste', () => {
    component.remove(1);

    expect(api.delete).toHaveBeenCalledWith(1);
    expect(api.getAll).toHaveBeenCalledTimes(2);
  });
});
