import { Component } from '@angular/core';
import { NgFor } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [NgFor, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './layout.component.html'
})
export class LayoutComponent {
  nav = [
    { label: 'Dashboard', route: '/dashboard', icon: 'M3 12h18v9H3zM3 3h7v7H3zM14 3h7v7h-7z' },
    { label: 'Convives', route: '/convives', icon: 'M4 19v-1a4 4 0 014-4h8a4 4 0 014 4v1M12 11a4 4 0 100-8 4 4 0 000 8' },
    { label: 'Allergenes', route: '/allergenes', icon: 'M12 2l4 8h-8l4-8zm-7 20a7 7 0 0114 0H5z' },
    { label: 'Plats', route: '/plats', icon: 'M4 6h16v2H4zm2 4h12v10H6z' },
    { label: 'Menus', route: '/menus', icon: 'M6 4h12v4H6zM6 10h12v10H6z' },
    { label: 'Services', route: '/services', icon: 'M5 5h14v4H5zM5 11h14v8H5z' },
    { label: 'Alertes', route: '/alertes', icon: 'M12 3l9 16H3l9-16zm0 6v4m0 4h.01' },
    { label: 'Actions', route: '/actions-correctives', icon: 'M4 12h7m-7 4h12M4 8h16' },
    { label: 'Utilisateurs', route: '/utilisateurs', icon: 'M16 14a4 4 0 00-8 0m-4 6a8 8 0 0116 0M12 3a4 4 0 110 8 4 4 0 010-8' },
    { label: 'Parametres', route: '/parametres', icon: 'M12 3l2 3 3 .7-1 3 2.3 2.3-2.3 2.3 1 3-3 .7-2 3-2-3-3-.7 1-3L4.7 12 7 9.7l-1-3 3-.7 2-3z' }
  ];

  constructor(private readonly auth: AuthService, private readonly router: Router) {}

  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
