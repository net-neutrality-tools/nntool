import {ADocComponent} from '../adoc/adoc.component';
import { OnInit } from '@angular/core';

export class DocuComponent extends ADocComponent implements OnInit {
    key = 'docu';

    ngOnInit(): void {
        this.injectedVars = {
            'ala-settings-server-statistic': this.configService.getServerStatistic(),
            'ala-settings-server-control': this.configService.getServerControl()
        };
        super.ngOnInit();
    }
}
