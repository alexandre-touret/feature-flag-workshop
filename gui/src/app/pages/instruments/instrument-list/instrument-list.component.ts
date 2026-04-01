import { Component, inject, resource, signal, viewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';

import { InstrumentService } from '../../../services/instrument.service';
import { Instrument } from '../../../models/instrument.model';
import { ConfirmDialogComponent } from '../../../dialogs/confirm-dialog/confirm-dialog.component';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-instrument-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatCardModule
  ],
  template: `
    <div class="header">
      <h1>Instruments</h1>
      <button mat-flat-button color="primary" routerLink="new">
        <mat-icon>add</mat-icon> New Instrument
      </button>
    </div>

    <mat-card class="m-2">
      <mat-card-content>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Filter</mat-label>
          <input matInput (keyup)="applyFilter($event)" placeholder="Ex. Fender" #input>
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>

        @if (instrumentsResource.isLoading()) {
          <div class="spinner-container">
            <mat-progress-spinner mode="indeterminate"></mat-progress-spinner>
          </div>
        } @else {
          <div class="table-container">
            <table mat-table [dataSource]="dataSource" matSort>
              <ng-container matColumnDef="id">
                <th mat-header-cell *matHeaderCellDef mat-sort-header> ID </th>
                <td mat-cell *matCellDef="let row"> {{row.id}} </td>
              </ng-container>

              <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef mat-sort-header> Name </th>
                <td mat-cell *matCellDef="let row"> {{row.name}} </td>
              </ng-container>

              <ng-container matColumnDef="manufacturer">
                <th mat-header-cell *matHeaderCellDef mat-sort-header> Manufacturer </th>
                <td mat-cell *matCellDef="let row"> {{row.manufacturer}} </td>
              </ng-container>

              <ng-container matColumnDef="type">
                <th mat-header-cell *matHeaderCellDef mat-sort-header> Type </th>
                <td mat-cell *matCellDef="let row"> {{row.type}} </td>
              </ng-container>

              <ng-container matColumnDef="price">
                <th mat-header-cell *matHeaderCellDef mat-sort-header> Price </th>
                <td mat-cell *matCellDef="let row"> {{row.price | currency:'EUR'}} </td>
              </ng-container>

              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef> Actions </th>
                <td mat-cell *matCellDef="let row">
                  <button mat-icon-button color="primary" [routerLink]="['edit', row.id]">
                    <mat-icon>edit</mat-icon>
                  </button>
                  <button mat-icon-button color="warn" (click)="deleteInstrument(row)">
                    <mat-icon>delete</mat-icon>
                  </button>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>

              <tr class="mat-row" *matNoDataRow>
                <td class="mat-cell" colspan="6">No data matching the filter "{{input.value}}"</td>
              </tr>
            </table>

            <mat-paginator [pageSizeOptions]="[5, 10, 25, 100]" aria-label="Select page of instruments"></mat-paginator>
          </div>
        }
      </mat-card-content>
    </mat-card>
  `,
  styles: `
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 16px;
    }
    .spinner-container {
      display: flex;
      justify-content: center;
      padding: 40px;
    }
    .table-container {
      overflow: auto;
    }
    table {
      width: 100%;
    }
  `
})
export class InstrumentListComponent {
  private instrumentService = inject(InstrumentService);
  private dialog = inject(MatDialog);

  displayedColumns: string[] = ['id', 'name', 'manufacturer', 'type', 'price', 'actions'];
  dataSource = new MatTableDataSource<Instrument>();

  paginator = viewChild(MatPaginator);
  sort = viewChild(MatSort);

  // Use Resource API for modern async data fetching
  instrumentsResource = resource({
    loader: async () => {
      const data = await firstValueFrom(this.instrumentService.getInstruments());
      this.dataSource.data = data;
      // Assign paginator and sort after data is loaded and signals update
      setTimeout(() => {
        this.dataSource.paginator = this.paginator() ?? null;
        this.dataSource.sort = this.sort() ?? null;
      });
      return data;
    }
  });

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  async deleteInstrument(instrument: Instrument) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Delete Instrument',
        message: `Are you sure you want to delete ${instrument.name}?`
      }
    });

    const confirmed = await firstValueFrom(dialogRef.afterClosed());
    if (confirmed && instrument.id) {
      await firstValueFrom(this.instrumentService.deleteInstrument(instrument.id));
      this.instrumentsResource.reload();
    }
  }
}
