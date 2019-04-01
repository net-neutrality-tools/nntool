import {TestState} from "./test-state";
import {Subject} from "rxjs";
import {Injectable} from "@angular/core";

export abstract class TestImplementation<TS extends TestState> {
    public abstract init: ($state: Subject<TS>) => void;
    public abstract start: () => void;
    public abstract destroy: () => void;
}