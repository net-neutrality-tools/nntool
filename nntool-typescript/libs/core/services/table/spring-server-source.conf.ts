/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Akveo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

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
