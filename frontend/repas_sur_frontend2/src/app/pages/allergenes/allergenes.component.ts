import { Component } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AllergeneApiService } from '../../core/services/allergene-api.service';
import { AllergeneDto } from '../../core/models/allergene.dto';

@Component({
  selector: 'app-allergenes',
  standalone: true,
  imports: [NgFor, NgIf, ReactiveFormsModule],
  templateUrl: './allergenes.component.html'
})
export class AllergenesComponent {
  allergenes: AllergeneDto[] = [];
  isLoading = false;
  editingId: number | null = null;

  form!: FormGroup;

  constructor(private readonly fb: FormBuilder, private readonly api: AllergeneApiService) {
    this.form = this.fb.nonNullable.group({
      code: ['', [Validators.required]],
      libelle: ['', [Validators.required]],
      description: ['']
    });
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.api.getAll().subscribe({
      next: (data) => {
        this.allergenes = data;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    const payload = this.form.getRawValue();
    const request$ = this.editingId
      ? this.api.update(this.editingId, payload)
      : this.api.create(payload);

    request$.subscribe({
      next: () => {
        this.resetForm();
        this.load();
      }
    });
  }

  startEdit(item: AllergeneDto): void {
    this.editingId = item.id;
    this.form.patchValue({
      code: item.code,
      libelle: item.libelle,
      description: item.description ?? ''
    });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  remove(id: number): void {
    this.api.delete(id).subscribe({
      next: () => this.load()
    });
  }

  private resetForm(): void {
    this.editingId = null;
    this.form.reset({ code: '', libelle: '', description: '' });
  }
}
