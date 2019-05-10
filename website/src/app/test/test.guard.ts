import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot} from "@angular/router";

import {Observable} from "rxjs";

import {NetTestComponent} from "./test.component";


@Injectable()
export class TestGuard implements CanDeactivate<NetTestComponent> {

    constructor () {}

    canDeactivate(
        component: NetTestComponent,
        currentRoute: ActivatedRouteSnapshot,
        currentState: RouterStateSnapshot,
        nextState: RouterStateSnapshot
    ): Observable<boolean> | Promise<boolean> | boolean {
        return !component.testInProgress;
    }
}