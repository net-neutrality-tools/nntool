import { SlideableItem } from '../../animation/slideable-item';

export class FullMeasurementResponse {
  public uuid: string;

  public open_data_uuid: string;

  public system_uuid: string;

  public start_time: string;

  public end_time: string;

  public duration_ns: number;

  public measurements: SingleMeasurement;
}

export class SingleMeasurement {
  public QOS: QoSResponse;
  public SPEED: SpeedResponse;
}

export class QoSResponse {
  public implausible: boolean;
  public version_protocol: string;
  public version_library: string;
  public relative_start_time_ns: string;
  public relative_end_time_ns: string;
  public start_time: string;
  public end_time: string;
  public duration_ns: string;
  public status: string;
  public reason: string;

  public results: QoSResult[];
  public key_to_translation_map: Map<string, string>;
  public qos_type_to_description_map: Map<string, QoSTypeDescription>; // TODO: make key an enum
}

export class QoSResult extends SlideableItem {
  public type: string;
  public summary: string;
  public description: string;
  public evaluation_count: number;
  public success_count: number;
  public failure_count: number;
  public implausible: boolean;
  public evaluation_keys: Map<string, string>;
  public result_keys: Map<string, string>;
}

export class QoSTypeDescription {
  public name: string;
  public description: string;
  public icon: string;
}

export class SpeedResponse {
  public implausible: boolean;
  public version_protocol: string;
  public version_library: string;
  public relative_start_time_ns: number;
  public relative_end_time_ns: number;
  public start_time: string;
  public end_time: string;
  public duration_ns: number;
  public status: string;
  public reason: string;
  public throughput_avg_download_bps: number;
  public throughput_avg_download_log: number;
  public throughput_avg_upload_bps: number;
  public throughput_avg_upload_log: number;
  public bytes_download: number;
  public bytes_download_including_slow_start: number;
  public bytes_upload: number;
  public bytes_upload_including_slow_start: number;
  public requested_duration_download_ns: number;
  public requested_duration_upload_ns: number;
  public duration_rtt_ns: number;
  public duration_download_ns: number;
  public duration_upload_ns: number;
  public relative_start_time_rtt_ns: number;
  public relative_start_time_download_ns: number;
  public relative_start_time_upload_ns: number;
  public rtt_info: any;
  public download_raw_data: any;
  public upload_raw_data: any;
  public connection_info: any;
}
