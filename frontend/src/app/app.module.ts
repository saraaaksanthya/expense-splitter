import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GroupListComponent } from './components/group-list/group-list.component';
import { GroupDetailComponent } from './components/group-detail/group-detail.component';
import { ExpenseFormComponent } from './components/expense-form/expense-form.component';
import { SettlementViewComponent } from './components/settlement-view/settlement-view.component';

@NgModule({
  declarations: [
    AppComponent,
    GroupListComponent,
    GroupDetailComponent,
    ExpenseFormComponent,
    SettlementViewComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
