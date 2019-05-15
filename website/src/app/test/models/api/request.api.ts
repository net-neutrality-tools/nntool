import {BaseAPI} from "./base.api";
import {RequestInfoAPI} from "./request-info.api";

export class RequestAPI<T> extends BaseAPI<T> {

    request_info: RequestInfoAPI;
}

