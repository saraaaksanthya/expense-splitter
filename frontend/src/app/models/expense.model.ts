export interface ExpenseSplit {
  id?: number;
  person: { id: number; name: string };
  shareAmount: number;
}

export interface Expense {
  id?: number;
  description: string;
  amount: number;
  date?: string;
  paidBy: { id: number; name: string };
  splits: ExpenseSplit[];
}

export interface ExpenseRequest {
  description: string;
  amount: number;
  paidById: number;
  participantIds: number[];
}
