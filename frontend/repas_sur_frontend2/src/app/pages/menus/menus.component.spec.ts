import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { MenuDto } from '../../core/models/menu.dto';
import { PlatDto } from '../../core/models/plat.dto';
import { MenuApiService } from '../../core/services/menu-api.service';
import { PlatApiService } from '../../core/services/plat-api.service';
import { MenusComponent } from './menus.component';

describe('MenusComponent', () => {
  let fixture: ComponentFixture<MenusComponent>;
  let component: MenusComponent;
  let menuApi: jasmine.SpyObj<MenuApiService>;
  let platApi: jasmine.SpyObj<PlatApiService>;

  const plats: PlatDto[] = [{ id: 1, nom: 'Salade', categorie: 'ENTREE', description: null, contientPorc: false, estVegetarien: true, allergenes: [] }];
  const menus: MenuDto[] = [{ id: 3, nom: 'Menu Frais', description: 'Desc', plats }];

  beforeEach(async () => {
    menuApi = jasmine.createSpyObj<MenuApiService>('MenuApiService', ['getAll', 'create', 'update', 'delete']);
    platApi = jasmine.createSpyObj<PlatApiService>('PlatApiService', ['getAll']);
    menuApi.getAll.and.returnValue(of(menus));
    menuApi.create.and.returnValue(of(menus[0]));
    menuApi.update.and.returnValue(of(menus[0]));
    menuApi.delete.and.returnValue(of(void 0));
    platApi.getAll.and.returnValue(of(plats));

    await TestBed.configureTestingModule({
      imports: [MenusComponent],
      providers: [
        { provide: MenuApiService, useValue: menuApi },
        { provide: PlatApiService, useValue: platApi }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MenusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('charge et affiche les menus et plats', () => {
    expect(component.menus).toEqual(menus);
    expect(component.plats).toEqual(plats);
    expect(fixture.nativeElement.textContent).toContain('Menu Frais');
  });

  it('gere la selection des plats', () => {
    component.togglePlat(1, true);
    expect(component.isPlatSelected(1)).toBeTrue();

    component.togglePlat(1, false);
    expect(component.isPlatSelected(1)).toBeFalse();
  });

  it('bloque la creation si le formulaire est invalide', () => {
    component.submit();
    expect(menuApi.create).not.toHaveBeenCalled();
  });

  it('cree puis met a jour un menu', () => {
    component.form.setValue({ nom: 'Menu B', description: 'Desc', platIds: [1] });
    component.submit();
    expect(menuApi.create).toHaveBeenCalledWith({ nom: 'Menu B', description: 'Desc', platIds: [1] });

    component.startEdit(menus[0]);
    component.form.patchValue({ nom: 'Menu C' });
    component.submit();
    expect(menuApi.update).toHaveBeenCalledWith(3, { nom: 'Menu C', description: 'Desc', platIds: [1] });
  });

  it('reinitialise le formulaire et formate les plats', () => {
    component.startEdit(menus[0]);
    component.cancelEdit();

    expect(component.form.getRawValue()).toEqual({ nom: '', description: '', platIds: [] });
    expect(component.formatPlats(menus[0])).toBe('Salade');
    expect(component.formatPlats({ ...menus[0], plats: [] })).toBe('-');
  });

  it('supprime un menu', () => {
    component.remove(3);
    expect(menuApi.delete).toHaveBeenCalledWith(3);
  });
});
