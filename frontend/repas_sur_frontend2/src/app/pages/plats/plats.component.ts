import { Component } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PlatApiService } from '../../core/services/plat-api.service';
import { AllergeneApiService } from '../../core/services/allergene-api.service';
import { PlatDto } from '../../core/models/plat.dto';
import { AllergeneDto } from '../../core/models/allergene.dto';

@Component({
  selector: 'app-plats',
  standalone: true,
  imports: [NgFor, NgIf, ReactiveFormsModule],
  templateUrl: './plats.component.html'
})
export class PlatsComponent {
  plats: PlatDto[] = [];
  allergenes: AllergeneDto[] = [];
  editingId: number | null = null;

  categories: string[] = [
    'ENTREE',
    'PLAT_PRINCIPAL',
    'ACCOMPAGNEMENT',
    'DESSERT',
    'BOISSON',
    'AUTRE'
  ];

  form!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly platApi: PlatApiService,
    private readonly allergeneApi: AllergeneApiService
  ) {
    this.form = this.fb.nonNullable.group({
      nom: ['', [Validators.required]],
      categorie: ['ENTREE', [Validators.required]],
      description: [''],
      contientPorc: [false],
      estVegetarien: [false],
      allergeneIds: [[] as number[]]
    });
    this.load();
  }

  load(): void {
    this.platApi.getAll().subscribe({ next: (data) => (this.plats = data) });
    this.allergeneApi.getAll().subscribe({ next: (data) => (this.allergenes = data) });
  }

  toggleAllergene(id: number, checked: boolean): void {
    const current = new Set(this.form.value.allergeneIds || []);
    checked ? current.add(id) : current.delete(id);
    this.form.patchValue({ allergeneIds: Array.from(current) });
  }

  isAllergeneSelected(id: number): boolean {
    return (this.form.value.allergeneIds || []).includes(id);
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    const payload = this.form.getRawValue();
    const request$ = this.editingId
      ? this.platApi.update(this.editingId, payload)
      : this.platApi.create(payload);

    request$.subscribe({
      next: () => {
        this.resetForm();
        this.load();
      }
    });
  }

  startEdit(item: PlatDto): void {
    this.editingId = item.id;
    this.form.patchValue({
      nom: item.nom,
      categorie: item.categorie ?? '',
      description: item.description ?? '',
      contientPorc: item.contientPorc,
      estVegetarien: item.estVegetarien,
      allergeneIds: item.allergenes.map((a) => a.id)
    });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  remove(id: number): void {
    this.platApi.delete(id).subscribe({ next: () => this.load() });
  }

  formatAllergenes(item: PlatDto): string {
    return item.allergenes.map((a) => a.libelle).filter(Boolean).join(', ') || '-';
  }

  private resetForm(): void {
    this.editingId = null;
        this.form.reset({
          nom: '',
          categorie: 'ENTREE',
          description: '',
          contientPorc: false,
          estVegetarien: false,
      allergeneIds: []
    });
  }
}
