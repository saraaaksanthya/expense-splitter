import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Group } from '../../models/group.model';
import { Expense } from '../../models/expense.model';
import { GroupService } from '../../services/group.service';
import { ExpenseService } from '../../services/expense.service';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-group-detail',
  templateUrl: './group-detail.component.html'
})
export class GroupDetailComponent implements OnInit {

  groupId!: number;
  group: Group | null = null;
  expenses: Expense[] = [];
  refreshCounter = 0;

  constructor(
    private route: ActivatedRoute,
    private groupService: GroupService,
    private expenseService: ExpenseService
  ) {}

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private groupService: GroupService,
    private expenseService: ExpenseService
  ) {}
  ngOnInit(): void {
    this.groupId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadGroup();
    this.loadExpenses();
  }

  loadGroup(): void {
    this.groupService.getById(this.groupId).subscribe(group => this.group = group);
  }

  loadExpenses(): void {
    this.expenseService.getForGroup(this.groupId).subscribe(expenses => this.expenses = expenses);
  }

  onExpenseAdded(): void {
    this.loadExpenses();
    this.refreshCounter++; // triggers SettlementViewComponent to reload via ngOnChanges
  }
  deleteGroup(): void {
    if (!this.group) { return; }
    if (!confirm(`Delete the group "${this.group.name}"? This also deletes all of its expenses.`)) {
      return;
    }
    this.groupService.delete(this.groupId).subscribe({
      next: () => this.router.navigate(['/'])
    });
  }
}
