import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators, FormControl, FormGroup } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService, RegisterRequest } from '../../core/services/auth.service';

type RoleOption = 'ADMIN' | 'RESPONSABLE' | 'CUISINE';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [NgIf, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  error = '';
  isLoading = false;

  form!: FormGroup<{
    username: FormControl<string>;
    email: FormControl<string>;
    password: FormControl<string>;
    role: FormControl<RoleOption>;
  }>;

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router
  ) {
    this.form = this.fb.nonNullable.group({
      username: ['', [Validators.required]],
      email: [''],
      password: ['', [Validators.required]],
      role: ['RESPONSABLE' as RoleOption, [Validators.required]]
    });
  }

  submit(): void {
    if (this.form.invalid || this.isLoading) {
      return;
    }
    this.error = '';
    this.isLoading = true;

    const payload = this.form.getRawValue() as RegisterRequest;
    this.auth.register(payload).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigateByUrl('/dashboard');
      },
      error: (err) => {
        this.isLoading = false;
        this.error = err?.error ?? 'Echec inscription';
      }
    });
  }
}
