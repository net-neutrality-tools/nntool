// https://github.com/akveo/ng2-smart-table/blob/master/src/ng2-smart-table/lib/data-source/server/server-source.conf.ts
// https://github.com/ff-iovcloud-ops/ng2-spring-smart-table/blob/master/src/ng2-smart-table/lib/data-source/server/server-source.conf.ts

//import { ServerSourceConf } from 'ng2-smart-table/lib/lib/data-source/server/server-source.conf';

//import { ServerSourceConf } from 'ng2-smart-table';

export class SpringServerSourceConf /*extends ServerSourceConf*/ {
  protected static readonly SPRING_SORT_FIELD_KEY = 'sort'; // spring pageable
  //protected static readonly SPRING_SORT_DIR_KEY = 'order'; // this is not used
  protected static readonly SPRING_PAGER_PAGE_KEY = 'page'; // spring pageable
  protected static readonly SPRING_PAGER_LIMIT_KEY = 'size'; // spring pageable
  //protected static readonly SPRING_FILTER_FIELD_KEY = '#field#'; // filter attr=value
  protected static readonly SPRING_TOTAL_KEY = 'data.total_elements';
  protected static readonly SPRING_DATA_KEY = 'data.content';

  mapFunction: any;
  additonalParameters: any; //Map<string, any>

  endPoint: string;

  sortFieldKey: string;
  sortDirKey: string;
  pagerPageKey: string;
  pagerLimitKey: string;
  filterFieldKey: string;
  totalKey: string;
  dataKey: string;

  constructor({
    endPoint = '',
    sortFieldKey = '',
    sortDirKey = '',
    pagerPageKey = '',
    pagerLimitKey = '',
    filterFieldKey = '',
    totalKey = '',
    dataKey = '',
    mapFunction = {},
    additonalParameters = {}
  } = {}) {
    /*super({
      endPoint,
      sortFieldKey,
      sortDirKey,
      pagerPageKey,
      pagerLimitKey,
      filterFieldKey,
      totalKey,
      dataKey
    });*/

    this.endPoint = endPoint ? endPoint : '';

    this.mapFunction = mapFunction;
    this.additonalParameters = additonalParameters;

    this.sortFieldKey = sortFieldKey ? sortFieldKey : SpringServerSourceConf.SPRING_SORT_FIELD_KEY;
    this.pagerPageKey = pagerPageKey ? pagerPageKey : SpringServerSourceConf.SPRING_PAGER_PAGE_KEY;
    this.pagerLimitKey = pagerLimitKey ? pagerLimitKey : SpringServerSourceConf.SPRING_PAGER_LIMIT_KEY;
    this.totalKey = totalKey ? totalKey : SpringServerSourceConf.SPRING_TOTAL_KEY;
    this.dataKey = dataKey ? dataKey : SpringServerSourceConf.SPRING_DATA_KEY;
  }
}
