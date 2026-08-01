export interface Balance {
  personId: number;
  personName: string;
  netBalance: number;
}

export interface Settlement {
  fromPersonId: number;
  fromPersonName: string;
  toPersonId: number;
  toPersonName: string;
  amount: number;
}
