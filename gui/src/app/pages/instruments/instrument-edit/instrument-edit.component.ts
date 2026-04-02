import { Component, inject, input, resource, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { InstrumentService } from '../../../services/instrument.service';
import { InstrumentType } from '../../../models/instrument-type.enum';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-instrument-edit',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatSnackBarModule,
    RouterModule
  ],
  template: `
    <div class="header">
      <button mat-icon-button routerLink="/instruments">
        <mat-icon>arrow_back</mat-icon>
      </button>
      <h1>{{ isEditMode() ? 'Edit' : 'New' }} Instrument</h1>
    </div>

    <mat-card class="m-2">
      <mat-card-content>
        <form [formGroup]="instrumentForm" (ngSubmit)="save()">
          <div class="form-row">
            <mat-form-field appearance="outline">
              <mat-label>Name</mat-label>
              <input matInput formControlName="name" required>
              <mat-error>Name is required</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline">
              <mat-label>Reference</mat-label>
              <input matInput formControlName="reference" required>
              <mat-error>Reference is required</mat-error>
            </mat-form-field>
          </div>

          <div class="form-row">
            <mat-form-field appearance="outline">
              <mat-label>Manufacturer</mat-label>
              <input matInput formControlName="manufacturer" required>
              <mat-error>Manufacturer is required</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline">
              <mat-label>Type</mat-label>
              <mat-select formControlName="type" required>
                @for (type of types; track type) {
                  <mat-option [value]="type">{{ type }}</mat-option>
                }
              </mat-select>
              <mat-error>Type is required</mat-error>
            </mat-form-field>
          </div>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Price</mat-label>
            <input matInput type="number" formControlName="price" required>
            <span matPrefix>€&nbsp;</span>
            <mat-error>Price is required and must be positive</mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Description</mat-label>
            <textarea matInput formControlName="description" rows="4"></textarea>
          </mat-form-field>

          <div class="actions">
            <button mat-button type="button" routerLink="/instruments">Cancel</button>
            <button mat-flat-button color="primary" type="submit" [disabled]="instrumentForm.invalid">
              Save
            </button>
          </div>
        </form>
      </mat-card-content>
    </mat-card>
  `,
  styles: `
    .header {
      display: flex;
      align-items: center;
      margin: 16px;
      gap: 16px;
    }
    .form-row {
      display: flex;
      gap: 16px;
    }
    .form-row mat-form-field {
      flex: 1;
    }
    .actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
      margin-top: 16px;
    }
  `
})
export class InstrumentEditComponent {
  private fb = inject(FormBuilder);
  private instrumentService = inject(InstrumentService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  
  // input signal for 'id' route param (configured in router config)
  id = input<string>();

  isEditMode = signal(false);
  types = Object.values(InstrumentType);

  instrumentForm = this.fb.group({
    id: [null as number | null],
    name: ['', Validators.required],
    reference: ['', Validators.required],
    manufacturer: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0)]],
    description: [''],
    type: [null as InstrumentType | null, Validators.required]
  });

  constructor() {
    effect(() => {
      const id = this.id();
      if (id && id !== 'new') {
        this.isEditMode.set(true);
      } else {
        this.isEditMode.set(false);
        this.instrumentForm.reset();
      }
    });

    effect(() => {
      const instrument = this.instrumentResource.value();
      if (instrument) {
        this.instrumentForm.patchValue(instrument);
      }
    });
  }

  // Resource to load instrument data if in edit mode
  instrumentResource = resource({
    request: () => this.id(),
    loader: async ({ request: id }) => {
      if (!id || id === 'new') return null;
      return await firstValueFrom(this.instrumentService.getInstrument(Number(id)));
    }
  });

  async save() {
    if (this.instrumentForm.invalid) return;

    const data = this.instrumentForm.value as any;
    try {
      if (this.isEditMode() && this.id()) {
        await firstValueFrom(this.instrumentService.updateInstrument(Number(this.id()), data));
        this.snackBar.open('Instrument updated', 'Close', { duration: 3000 });
      } else {
        await firstValueFrom(this.instrumentService.createInstrument(data));
        this.snackBar.open('Instrument created', 'Close', { duration: 3000 });
      }
      this.router.navigate(['/instruments']);
    } catch (error) {
      this.snackBar.open('Error saving instrument', 'Close', { duration: 3000 });
    }
  }
}
