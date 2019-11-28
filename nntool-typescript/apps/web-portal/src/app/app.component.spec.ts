import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { RouterTestingModule } from '@angular/router/testing';
import { AppSharedModule } from '@nntool-typescript/module';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, AppSharedModule],
      declarations: [AppComponent]
    }).compileComponents();

    // create component and test fixture
    fixture = TestBed.createComponent(AppComponent);

    // get test component from the fixture
    component = fixture.debugElement.componentInstance;
  }));

  it('should create the webapp', async(() => {
    expect(component).toBeTruthy();
  }));
});
