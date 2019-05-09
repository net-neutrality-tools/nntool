import {Component, Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs";


@Injectable()
export class TestGuard implements CanDeactivate<NetTestComponent> {

    constructor () {}

    canDeactivate(
        component: NetTestComponent,
        currentRoute: ActivatedRouteSnapshot,
        currentState: RouterStateSnapshot,
        nextState: RouterStateSnapshot
    ): Observable<boolean>|Promise<boolean>|boolean {
        return true;
    }
}


@Component({
    template: ""
})
export class NetTestComponent {
}