import { Component, Input, OnChanges } from '@angular/core';
import { Balance, Settlement } from '../../models/settlement.model';
import { SettlementService } from '../../services/settlement.service';

@Component({
  selector: 'app-settlement-view',
  templateUrl: './settlement-view.component.html'
})
export class SettlementViewComponent implements OnChanges {

  @Input() groupId!: number;
  @Input() refreshTrigger: unknown;

  balances: Balance[] = [];
  settlements: Settlement[] = [];
  loading = false;

  paidKeys = new Set<string>();

  constructor(private settlementService: SettlementService) {}

  ngOnChanges(): void {
    if (this.groupId) {
      this.load();
    }
  }

  load(): void {
    this.loading = true;
    this.settlementService.getBalances(this.groupId).subscribe({
      next: (balances) => this.balances = balances,
      complete: () => this.loading = false
    });
    this.settlementService.getSettlements(this.groupId).subscribe({
      next: (settlements) => this.settlements = settlements
    });
  }

  balanceClass(balance: number): string {
    if (balance > 0.01) { return 'balance-positive'; }
    if (balance < -0.01) { return 'balance-negative'; }
    return 'balance-zero';
  }

  settlementKey(s: Settlement): string {
    return `${s.fromPersonId}-${s.toPersonId}-${s.amount}`;
  }

  isPaid(s: Settlement): boolean {
    return this.paidKeys.has(this.settlementKey(s));
  }

  togglePaid(s: Settlement): void {
    const key = this.settlementKey(s);
    if (this.paidKeys.has(key)) {
      this.paidKeys.delete(key);
    } else {
      this.paidKeys.add(key);
    }
  }
}