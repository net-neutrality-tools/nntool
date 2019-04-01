import {ADocComponent} from "../adoc/adoc.component";


export class DocuComponent extends ADocComponent {
    key: string = "docu";


    ngOnInit (): void {
        this.injectedVars = {
            'ala-settings-server-statistic': this.configService.getServerStatistic(),
            'ala-settings-server-control': this.configService.getServerControl()
        };
        super.ngOnInit();
    }
}
