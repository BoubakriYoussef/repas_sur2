import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { RegimeDto } from '../../core/models/regime.dto';
import { SiteRestaurationDto } from '../../core/models/site.dto';
import { RegimeApiService } from '../../core/services/regime-api.service';
import { SiteApiService } from '../../core/services/site-api.service';
import { ParametresComponent } from './parametres.component';

describe('ParametresComponent', () => {
  let fixture: ComponentFixture<ParametresComponent>;
  let component: ParametresComponent;
  let siteApi: jasmine.SpyObj<SiteApiService>;
  let regimeApi: jasmine.SpyObj<RegimeApiService>;

  const sites: SiteRestaurationDto[] = [{ id: 1, nom: 'Site A', type: 'SCOLAIRE', adresse: 'Rue A' }];
  const regimes: RegimeDto[] = [{ id: 2, code: 'VG', libelle: 'Vegetarien', type: 'ALIMENTAIRE', description: 'Sans viande' }];

  beforeEach(async () => {
    siteApi = jasmine.createSpyObj<SiteApiService>('SiteApiService', ['getAll', 'create', 'update', 'delete']);
    regimeApi = jasmine.createSpyObj<RegimeApiService>('RegimeApiService', ['getAll', 'create', 'update', 'delete']);
    siteApi.getAll.and.returnValue(of(sites));
    siteApi.create.and.returnValue(of(sites[0]));
    siteApi.update.and.returnValue(of(sites[0]));
    siteApi.delete.and.returnValue(of(void 0));
    regimeApi.getAll.and.returnValue(of(regimes));
    regimeApi.create.and.returnValue(of(regimes[0]));
    regimeApi.update.and.returnValue(of(regimes[0]));
    regimeApi.delete.and.returnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [ParametresComponent],
      providers: [
        { provide: SiteApiService, useValue: siteApi },
        { provide: RegimeApiService, useValue: regimeApi }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ParametresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('charge les sites et les regimes', () => {
    expect(component.sites).toEqual(sites);
    expect(component.regimes).toEqual(regimes);
    expect(fixture.nativeElement.textContent).toContain('Site A');
    expect(fixture.nativeElement.textContent).toContain('Vegetarien');
  });

  it('bloque les creations avec formulaires invalides', () => {
    component.createSite();
    component.createRegime();
    expect(siteApi.create).not.toHaveBeenCalled();
    expect(regimeApi.create).not.toHaveBeenCalled();
  });

  it('cree puis met a jour un site', () => {
    component.siteForm.setValue({ nom: 'Site B', type: 'EHPAD', adresse: 'Rue B' });
    component.createSite();
    expect(siteApi.create).toHaveBeenCalledWith({ nom: 'Site B', type: 'EHPAD', adresse: 'Rue B' });

    component.startEditSite(sites[0]);
    component.siteForm.patchValue({ nom: 'Site C' });
    component.createSite();
    expect(siteApi.update).toHaveBeenCalledWith(1, { nom: 'Site C', type: 'SCOLAIRE', adresse: 'Rue A' });
  });

  it('cree puis met a jour un regime', () => {
    component.regimeForm.setValue({ code: 'HAL', libelle: 'Halal', type: 'RELIGIEUX', description: 'Desc' });
    component.createRegime();
    expect(regimeApi.create).toHaveBeenCalledWith({ code: 'HAL', libelle: 'Halal', type: 'RELIGIEUX', description: 'Desc' });

    component.startEditRegime(regimes[0]);
    component.regimeForm.patchValue({ libelle: 'Veggie' });
    component.createRegime();
    expect(regimeApi.update).toHaveBeenCalledWith(2, { code: 'VG', libelle: 'Veggie', type: 'ALIMENTAIRE', description: 'Sans viande' });
  });

  it('annule les editions et supprime site et regime', () => {
    component.startEditSite(sites[0]);
    component.cancelSiteEdit();
    expect(component.siteForm.getRawValue()).toEqual({ nom: '', type: 'SCOLAIRE', adresse: '' });

    component.startEditRegime(regimes[0]);
    component.cancelRegimeEdit();
    expect(component.regimeForm.getRawValue()).toEqual({ code: '', libelle: '', type: 'ALIMENTAIRE', description: '' });

    component.removeSite(1);
    component.removeRegime(2);
    expect(siteApi.delete).toHaveBeenCalledWith(1);
    expect(regimeApi.delete).toHaveBeenCalledWith(2);
  });
});
