import {Component} from "@angular/core";
import {Logger, LoggerService} from "../services/log.service";

@Component({
    selector: "footer",
    templateUrl: "./footer.component.html"
})
export class FooterComponent {

    private logger: Logger = LoggerService.getLogger("FooterComponent");

}