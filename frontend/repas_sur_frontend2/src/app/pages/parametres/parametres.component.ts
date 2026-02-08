import { Component } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SiteApiService } from '../../core/services/site-api.service';
import { RegimeApiService } from '../../core/services/regime-api.service';
import { SiteRestaurationDto } from '../../core/models/site.dto';
import { RegimeDto } from '../../core/models/regime.dto';

@Component({
  selector: 'app-parametres',
  standalone: true,
  imports: [NgFor, NgIf, ReactiveFormsModule],
  templateUrl: './parametres.component.html'
})
export class ParametresComponent {
  sites: SiteRestaurationDto[] = [];
  regimes: RegimeDto[] = [];
  editingSiteId: number | null = null;
  editingRegimeId: number | null = null;

  siteForm!: FormGroup;
  regimeForm!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly siteApi: SiteApiService,
    private readonly regimeApi: RegimeApiService
  ) {
    this.siteForm = this.fb.nonNullable.group({
      nom: ['', [Validators.required]],
      type: ['SCOLAIRE', [Validators.required]],
      adresse: ['']
    });

    this.regimeForm = this.fb.nonNullable.group({
      code: ['', [Validators.required]],
      libelle: ['', [Validators.required]],
      type: ['ALIMENTAIRE', [Validators.required]],
      description: ['']
    });
    this.load();
  }

  load(): void {
    this.siteApi.getAll().subscribe({ next: (data) => (this.sites = data) });
    this.regimeApi.getAll().subscribe({ next: (data) => (this.regimes = data) });
  }

  createSite(): void {
    if (this.siteForm.invalid) {
      return;
    }
    const payload = this.siteForm.getRawValue();
    const request$ = this.editingSiteId
      ? this.siteApi.update(this.editingSiteId, payload)
      : this.siteApi.create(payload);

    request$.subscribe({
      next: () => {
        this.resetSiteForm();
        this.load();
      }
    });
  }

  createRegime(): void {
    if (this.regimeForm.invalid) {
      return;
    }
    const payload = this.regimeForm.getRawValue();
    const request$ = this.editingRegimeId
      ? this.regimeApi.update(this.editingRegimeId, payload)
      : this.regimeApi.create(payload);

    request$.subscribe({
      next: () => {
        this.resetRegimeForm();
        this.load();
      }
    });
  }

  startEditSite(site: SiteRestaurationDto): void {
    this.editingSiteId = site.id;
    this.siteForm.patchValue({
      nom: site.nom,
      type: site.type,
      adresse: site.adresse ?? ''
    });
  }

  cancelSiteEdit(): void {
    this.resetSiteForm();
  }

  removeSite(id: number): void {
    this.siteApi.delete(id).subscribe({ next: () => this.load() });
  }

  startEditRegime(regime: RegimeDto): void {
    this.editingRegimeId = regime.id;
    this.regimeForm.patchValue({
      code: regime.code ?? '',
      libelle: regime.libelle ?? '',
      type: regime.type ?? 'ALIMENTAIRE',
      description: regime.description ?? ''
    });
  }

  cancelRegimeEdit(): void {
    this.resetRegimeForm();
  }

  removeRegime(id: number): void {
    this.regimeApi.delete(id).subscribe({ next: () => this.load() });
  }

  private resetSiteForm(): void {
    this.editingSiteId = null;
    this.siteForm.reset({ nom: '', type: 'SCOLAIRE', adresse: '' });
  }

  private resetRegimeForm(): void {
    this.editingRegimeId = null;
    this.regimeForm.reset({ code: '', libelle: '', type: 'ALIMENTAIRE', description: '' });
  }
}
