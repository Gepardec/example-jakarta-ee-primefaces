import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, MatToolbarModule, MatButtonModule, MatIconModule],
  template: `
    <mat-toolbar color="primary">
      <span class="title">Notizblock</span>
      <span class="spacer"></span>
      <a mat-button routerLink="/notes" routerLinkActive="active-link">
        <mat-icon>note</mat-icon>
        Notizen
      </a>
    </mat-toolbar>
  `,
  styles: [`
    :host {
      display: block;
    }

    .title {
      font-size: 1.5rem;
      font-weight: 500;
    }

    .spacer {
      flex: 1 1 auto;
    }

    .active-link {
      background-color: rgba(255, 255, 255, 0.1);
    }

    a {
      margin-left: 8px;
    }
  `]
})
export class HeaderComponent {}
