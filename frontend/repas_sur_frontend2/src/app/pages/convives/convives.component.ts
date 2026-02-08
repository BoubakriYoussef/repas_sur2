import { Component } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConviveApiService } from '../../core/services/convive-api.service';
import { SiteApiService } from '../../core/services/site-api.service';
import { AllergeneApiService } from '../../core/services/allergene-api.service';
import { RegimeApiService } from '../../core/services/regime-api.service';
import { ConviveDto } from '../../core/models/convive.dto';
import { SiteRestaurationDto } from '../../core/models/site.dto';
import { AllergeneDto } from '../../core/models/allergene.dto';
import { RegimeDto } from '../../core/models/regime.dto';

@Component({
  selector: 'app-convives',
  standalone: true,
  imports: [NgFor, NgIf, ReactiveFormsModule],
  templateUrl: './convives.component.html'
})
export class ConvivesComponent {
  rows: ConviveDto[] = [];
  sites: SiteRestaurationDto[] = [];
  allergenes: AllergeneDto[] = [];
  regimes: RegimeDto[] = [];
  editingId: number | null = null;

  form!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly conviveApi: ConviveApiService,
    private readonly siteApi: SiteApiService,
    private readonly allergeneApi: AllergeneApiService,
    private readonly regimeApi: RegimeApiService
  ) {
    this.form = this.fb.nonNullable.group({
      nom: ['', [Validators.required]],
      prenom: ['', [Validators.required]],
      typeConvive: ['ENFANT_SCOLAIRE', [Validators.required]],
      siteId: [0, [Validators.required]],
      allergeneIds: [[] as number[]],
      regimeIds: [[] as number[]]
    });
    this.load();
  }

  load(): void {
    this.conviveApi.getAll().subscribe({ next: (data) => (this.rows = data) });
    this.siteApi.getAll().subscribe({
      next: (data) => {
        this.sites = data;
        const current = this.form.getRawValue().siteId as number;
        if ((!current || current === 0) && this.sites.length > 0) {
          this.form.patchValue({ siteId: this.sites[0].id });
        }
      }
    });
    this.allergeneApi.getAll().subscribe({ next: (data) => (this.allergenes = data) });
    this.regimeApi.getAll().subscribe({ next: (data) => (this.regimes = data) });
  }

  toggleAllergene(id: number, checked: boolean): void {
    const current = new Set(this.form.value.allergeneIds || []);
    checked ? current.add(id) : current.delete(id);
    this.form.patchValue({ allergeneIds: Array.from(current) });
  }

  isAllergeneSelected(id: number): boolean {
    return (this.form.value.allergeneIds || []).includes(id);
  }

  toggleRegime(id: number, checked: boolean): void {
    const current = new Set(this.form.value.regimeIds || []);
    checked ? current.add(id) : current.delete(id);
    this.form.patchValue({ regimeIds: Array.from(current) });
  }

  isRegimeSelected(id: number): boolean {
    return (this.form.value.regimeIds || []).includes(id);
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    const raw = this.form.getRawValue();
    const payload = {
      ...raw,
      siteId: Number(raw.siteId),
      allergeneIds: (raw.allergeneIds || []).map(Number),
      regimeIds: (raw.regimeIds || []).map(Number)
    };
    const request$ = this.editingId
      ? this.conviveApi.update(this.editingId, payload)
      : this.conviveApi.create(payload);

    request$.subscribe({
      next: () => {
        this.resetForm();
        this.load();
      }
    });
  }

  startEdit(row: ConviveDto): void {
    this.editingId = row.id;
    this.form.patchValue({
      nom: row.nom,
      prenom: row.prenom,
      typeConvive: row.typeConvive,
      siteId: row.site?.id ?? 0,
      allergeneIds: row.allergenes.map((item) => item.id),
      regimeIds: row.regimes.map((item) => item.id)
    });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  remove(id: number): void {
    this.conviveApi.delete(id).subscribe({ next: () => this.load() });
  }

  formatRegimes(row: ConviveDto): string {
    return row.regimes.map((r) => r.libelle).filter(Boolean).join(', ') || '-';
  }

  formatAllergenes(row: ConviveDto): string {
    return row.allergenes.map((a) => a.libelle).filter(Boolean).join(', ') || '-';
  }

  private resetForm(): void {
    this.editingId = null;
    this.form.reset({
      nom: '',
      prenom: '',
      typeConvive: 'ENFANT_SCOLAIRE',
      siteId: this.sites[0]?.id ?? 0,
      allergeneIds: [],
      regimeIds: []
    });
  }
}
