import {Component} from "@angular/core";
import {Logger, LoggerService} from "../services/log.service";

@Component({
    selector: "footer",
    templateUrl: "./app/footer/footer.component.html"
})
export class FooterComponent {

    private logger: Logger = LoggerService.getLogger("FooterComponent");

}