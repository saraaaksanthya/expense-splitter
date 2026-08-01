import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Person } from '../../models/person.model';
import { ExpenseService } from '../../services/expense.service';

@Component({
  selector: 'app-expense-form',
  templateUrl: './expense-form.component.html'
})
export class ExpenseFormComponent {

  @Input() groupId!: number;
  @Input() members: Person[] = [];
  @Output() expenseAdded = new EventEmitter<void>();

  description = '';
  amount: number | null = null;
  paidById: number | null = null;
  participantIds: number[] = [];

  errorMessage = '';

  constructor(private expenseService: ExpenseService) {}

  toggleParticipant(personId: number, checked: boolean): void {
    if (checked) {
      this.participantIds = [...this.participantIds, personId];
    } else {
      this.participantIds = this.participantIds.filter(id => id !== personId);
    }
  }

  isParticipant(personId: number): boolean {
    return this.participantIds.includes(personId);
  }

  selectAllParticipants(): void {
    this.participantIds = this.members.map(m => m.id!);
  }

  submit(): void {
    this.errorMessage = '';

    if (!this.description.trim() || !this.amount || this.amount <= 0) {
      this.errorMessage = 'Enter a description and a positive amount';
      return;
    }
    if (!this.paidById) {
      this.errorMessage = 'Select who paid';
      return;
    }
    if (this.participantIds.length === 0) {
      this.errorMessage = 'Select at least one participant to split the cost with';
      return;
    }

    this.expenseService.add(this.groupId, {
      description: this.description.trim(),
      amount: this.amount,
      paidById: this.paidById,
      participantIds: this.participantIds
    }).subscribe({
      next: () => {
        this.description = '';
        this.amount = null;
        this.paidById = null;
        this.participantIds = [];
        this.expenseAdded.emit();
      },
      error: (err) => this.errorMessage = err?.error?.message || 'Failed to add expense'
    });
  }
}
