-- create schema public;

-- comment on schema public is 'standard public schema';

alter schema public owner to nntool;

create table settings
(
	id varchar(100) not null
		constraint settings_pkey
			primary key,
	json jsonb
);

comment on column settings.json is 'Contains the settings';

alter table settings owner to nntool;

create table devices
(
	id bigserial not null
		constraint devices_pkey
			primary key,
	code_name varchar,
	full_name varchar
);

comment on column devices.code_name is 'Device code name.';

comment on column devices.full_name is 'The device name that is commonly known to users (e.g. Google Pixel).';

alter table devices owner to nntool;

create table translations
(
	language_code varchar(50) not null
		constraint translations_pkey
			primary key,
	json jsonb
);

comment on column translations.json is 'Contains the specific key-value pairs for the given language';

alter table translations owner to nntool;

create table qos_objectives
(
	id serial not null
		constraint qos_objectives_pkey
			primary key,
	enabled boolean default true not null,
	type varchar,
	concurrency_group integer default 0 not null,
	summary_key varchar,
	description_key varchar,
	parameters jsonb,
	evaluations jsonb
);

comment on column qos_objectives.enabled is 'Flag if this QoS measurement objective is enabled.';

comment on column qos_objectives.type is 'The QoS type (e.g. TCP, UDP)';

comment on column qos_objectives.concurrency_group is 'Objectives are ordered based on the concurrency group. Objectives that have the same concurrency group will be executed in parallel. Concurrency groups will be executed after each other in sorted order.';

comment on column qos_objectives.summary_key is 'Translation key for the summary of this QoS objective.';

comment on column qos_objectives.description_key is 'Translation key for the description of this QoS objective.';

comment on column qos_objectives.parameters is 'A map of QoS objective parameters (e.g. timeout, TCP port, web page URL)';

comment on column qos_objectives.evaluations is 'A list of custom evaluations for this QoS objective. Evaluations are used to present human readable results to the end user.';

alter table qos_objectives owner to nntool;

create table network_types
(
	id bigserial not null
		constraint network_types_pkey
			primary key,
	name varchar,
	group_name varchar,
	category varchar
);

comment on column network_types.name is 'Network type name.';

comment on column network_types.group_name is 'Network group name (e.g. 2G, 3G, LAN).';

comment on column network_types.category is 'Contains the different network categories.';

alter table network_types owner to nntool;

create table measurements
(
	open_data_uuid uuid not null
		constraint measurements_pkey
			primary key,
	system_uuid varchar,
	start_time timestamp,
	end_time timestamp,
	duration_ns bigint,
	os_name varchar,
	os_version varchar,
	os_api_level varchar,
	os_cpu_usage jsonb,
	os_mem_usage jsonb,
	device_codename varchar,
	device_model varchar,
	device_fullname varchar,
	network_client_public_ip inet,
	provider_public_ip_asn integer,
	provider_public_ip_rdns varchar,
	provider_public_ip_as_name varchar,
	provider_country_asn varchar(8),
	provider_name varchar,
	provider_shortname varchar,
	agent_app_version_name varchar,
	agent_app_version_code integer,
	agent_language varchar(8),
	agent_app_git_rev varchar,
	agent_timezone varchar,
	agent_uuid uuid not null,
	network_signal_info jsonb,
	mobile_network_operator_mcc integer,
	mobile_network_operator_mnc integer,
	mobile_network_country_code varchar(10),
	mobile_network_operator_name varchar,
	mobile_sim_operator_mcc integer,
	mobile_sim_operator_mnc integer,
	mobile_sim_operator_name varchar,
	mobile_is_roaming boolean,
	mobile_sim_country_code varchar(10),
	mobile_roaming_type varchar,
	initial_network_type_id integer
		constraint measurements_network_type_id_fkey
			references network_types,
	geo_location_accuracy double precision,
	mobile_network_signal_strength_2g3g_dbm integer,
	mobile_network_lte_rsrp_dbm integer,
	mobile_network_lte_rsrq_db integer,
	mobile_network_lte_rssnr_db integer,
	wifi_network_link_speed_bps integer,
	wifi_network_rssi_dbm integer,
	network_group_name varchar,
	network_client_public_ip_country_code varchar,
	agent_type varchar,
	geo_location_latitude double precision,
	geo_location_longitude double precision,
	geo_location_geometry geometry,
	mobile_network_frequency integer,
	tag varchar,
	wifi_initial_bssid varchar,
	wifi_initial_ssid varchar
);

comment on column measurements.open_data_uuid is 'The unique identifier (UUIDv4) of the measurement';

comment on column measurements.system_uuid is 'Measurement source. Can be either own system or imported from open-data.';

comment on column measurements.start_time is 'Start date and time for this measurement';

comment on column measurements.end_time is 'End date and time for this measurement';

comment on column measurements.duration_ns is 'Duration of a measurement';

comment on column measurements.os_name is 'Operating system name (e.g. Android or iOS)';

comment on column measurements.os_version is 'Operating system version.';

comment on column measurements.os_api_level is 'API level of operating system or SDK (e.g. Android API level or Swift SDK version)';

comment on column measurements.os_cpu_usage is 'CPU usage during the test';

comment on column measurements.os_mem_usage is 'Memory usage during the test';

comment on column measurements.device_codename is 'Device code name.';

comment on column measurements.device_model is 'Detailed device designation.';

comment on column measurements.device_fullname is 'The device name that is commonly known to users (e.g. Google Pixel).';

comment on column measurements.network_client_public_ip is 'Public IP address of the agent.';

comment on column measurements.provider_public_ip_asn is 'ASN for the public IP address.';

comment on column measurements.provider_public_ip_rdns is 'Reverse DNS for the public IP address.';

comment on column measurements.provider_public_ip_as_name is 'Name of ASN.';

comment on column measurements.provider_country_asn is 'Country code derived from the AS';

comment on column measurements.provider_name is 'The name of the provider';

comment on column measurements.provider_shortname is 'The short name (or shortcut) of the provider';

comment on column measurements.agent_app_version_name is 'Application version name (e.g. 1.0.0).';

comment on column measurements.agent_app_version_code is 'Application version code number (e.g. 10).';

comment on column measurements.agent_language is 'The agent''s language.';

comment on column measurements.agent_app_git_rev is 'Git revision.';

comment on column measurements.agent_timezone is 'The agent''s time zone.';

comment on column measurements.agent_uuid is 'The agent''s UUID.';

comment on column measurements.network_signal_info is 'Contains signal information captured during the test.';

comment on column measurements.mobile_network_operator_mcc is 'The MCC of the mobile network operator';

comment on column measurements.mobile_network_operator_mnc is 'The MNC of the mobile network operator';

comment on column measurements.mobile_network_country_code is 'The mobile network operator country code';

comment on column measurements.mobile_network_operator_name is 'The mobile network operator name';

comment on column measurements.mobile_sim_operator_mcc is 'The MCC of the SIM operator';

comment on column measurements.mobile_sim_operator_mnc is 'The MNC of the SIM operator';

comment on column measurements.mobile_sim_operator_name is 'SIM operator name';

comment on column measurements.mobile_is_roaming is 'Indicates if this is a roaming connection';

comment on column measurements.mobile_sim_country_code is 'The SIM operator country code';

comment on column measurements.mobile_roaming_type is 'The roaming type';

comment on column measurements.initial_network_type_id is 'Initial network type ID';

comment on column measurements.geo_location_accuracy is 'Geographic location accuracy';

comment on column measurements.mobile_network_signal_strength_2g3g_dbm is 'The received signal strength of 2G or 3G connections, in dBm';

comment on column measurements.mobile_network_lte_rsrp_dbm is 'The LTE reference signal received power, in dBm';

comment on column measurements.mobile_network_lte_rsrq_db is 'The LTE reference signal received quality, in dB';

comment on column measurements.mobile_network_lte_rssnr_db is 'The LTE reference signal signal-to-noise ratio, in dB';

comment on column measurements.wifi_network_link_speed_bps is 'The current WiFi link speed, in bits per second';

comment on column measurements.wifi_network_rssi_dbm is 'The received signal strength indicator of the current 802.11 network, in dBm';

comment on column measurements.network_group_name is 'Network group name (e.g. 2G, 3G, LAN).';

comment on column measurements.network_client_public_ip_country_code is 'Country code derived from the agent''s IP (e.g. "AT").';

comment on column measurements.agent_type is 'The type of agent (e.g. MOBILE, BROWSER, DESKTOP)';

comment on column measurements.geo_location_latitude is 'Geographic location latitude.';

comment on column measurements.geo_location_longitude is 'Geographic location longitude.';

comment on column measurements.geo_location_geometry is 'Geographic location geometry, in 900913 projection for easy use.';

comment on column measurements.mobile_network_frequency is 'Contains the ARFCN (Absolute Radio Frequency Channel Number) (e.g. 16-bit GSM ARFCN or 18-bit LTE EARFCN)';

comment on column measurements.tag is 'Contains a tag provided by the agent.';

comment on column measurements.wifi_initial_bssid is 'Initial BSSID of the network.';

comment on column measurements.wifi_initial_ssid is 'Initial SSID of the network.';

alter table measurements owner to nntool;

create table qos_measurements
(
	id bigserial not null
		constraint qos_measurements_pkey
			primary key,
	relative_start_time_ns bigint,
	relative_end_time_ns bigint,
	start_time timestamp,
	end_time timestamp,
	duration_ns bigint,
	measurement_open_data_uuid uuid
		constraint qos_measurements_measurement_open_data_uuid_fkey
			references measurements,
	status varchar,
	reason varchar,
	version_protocol varchar,
	version_library varchar,
	implausible boolean
);

comment on column qos_measurements.relative_start_time_ns is 'Start time in nanoseconds relative to the start time of the overall measurement object.';

comment on column qos_measurements.relative_end_time_ns is 'End time in nanoseconds relative to the end time of the overall measurement object.';

comment on column qos_measurements.start_time is 'Start date and time for this qos measurement';

comment on column qos_measurements.end_time is 'End date and time for this qos measurement';

comment on column qos_measurements.duration_ns is 'Duration of the whole QoS measurement.';

comment on column qos_measurements.status is 'The status of a measurement';

comment on column qos_measurements.reason is 'The reason why a measurement failed';

comment on column qos_measurements.version_protocol is 'The protocol version this measurement used';

comment on column qos_measurements.version_library is 'The library version this measurement used';

comment on column qos_measurements.implausible is 'Flag to mark a measurement as implausible';

alter table qos_measurements owner to nntool;

create table qos_measurement_results
(
	id bigserial not null
		constraint qos_measurement_results_pkey
			primary key,
	qos_measurement_id bigint not null
		constraint qos_measurement_results_qos_measurement_id_fkey
			references qos_measurements,
	implausible boolean default false,
	result jsonb,
	type varchar,
	qos_objective_id bigint
		constraint qos_measurement_results_qos_objective_id_fkey
			references qos_objectives,
	success_count integer,
	failure_count integer
);

comment on column qos_measurement_results.implausible is 'Flag to mark a QoS measurement as implausible';

comment on column qos_measurement_results.result is 'Stores the result key-value pairs gathered from the QoS measurement execution.';

comment on column qos_measurement_results.type is 'The QoS type (e.g. TCP, UDP)';

comment on column qos_measurement_results.success_count is 'The count of positive evalutations (successes)';

comment on column qos_measurement_results.failure_count is 'The count of negative evalutations (failures)';

alter table qos_measurement_results owner to nntool;

create table ias_measurements
(
	id bigserial not null
		constraint speed_measurements_pkey
			primary key,
	relative_start_time_ns bigint,
	relative_end_time_ns bigint,
	start_time timestamp,
	end_time timestamp,
	duration_ns bigint,
	measurement_open_data_uuid uuid
		constraint speed_measurements_measurement_open_data_uuid_fkey
			references measurements,
	status varchar,
	reason varchar,
	version_protocol varchar,
	version_library varchar,
	implausible boolean,
	throughput_avg_download_bps bigint,
	throughput_avg_upload_bps bigint,
	throughput_avg_download_log double precision,
	throughput_avg_upload_log double precision,
	bytes_download bigint,
	requested_duration_download_ns bigint,
	requested_duration_upload_ns bigint,
	duration_upload_ns bigint,
	duration_download_ns bigint,
	relative_start_time_upload_ns bigint,
	relative_start_time_download_ns bigint,
	duration_rtt_ns bigint,
	relative_start_time_rtt_ns bigint,
	connection_info jsonb,
	rtt_median_ns bigint,
	rtt_median_log double precision,
	speed_raw_data jsonb,
	bytes_upload bigint,
	rtt_info jsonb,
	requested_duration_upload_slow_start_ns bigint,
	requested_duration_download_slow_start_ns bigint
);

comment on column ias_measurements.relative_start_time_ns is 'Start time in nanoseconds relative to the start time of the overall measurement object.';

comment on column ias_measurements.relative_end_time_ns is 'End time in nanoseconds relative to the end time of the overall measurement object.';

comment on column ias_measurements.start_time is 'Start Date and time for this speed measurement. Date and time is always stored as UTC.';

comment on column ias_measurements.end_time is 'End date and time for this speed measurement. Date and time is always stored as UTC.';

comment on column ias_measurements.duration_ns is 'Duration of a measurement.';

comment on column ias_measurements.status is 'The status of a measurement.';

comment on column ias_measurements.reason is 'The reason why a measurement failed.';

comment on column ias_measurements.version_protocol is 'The protocol version this measurement used';

comment on column ias_measurements.version_library is 'The library version this measurement used';

comment on column ias_measurements.implausible is 'Flag to mark a measurement as implausible.';

comment on column ias_measurements.throughput_avg_download_bps is 'The calculated (average) download throughput in bits per second.';

comment on column ias_measurements.throughput_avg_upload_bps is 'The calculated (average) upload throughput in bits per second.';

comment on column ias_measurements.throughput_avg_download_log is 'Common logarithm of the (average) download throughput.';

comment on column ias_measurements.throughput_avg_upload_log is 'Common logarithm of the average upload throughput.';

comment on column ias_measurements.bytes_download is 'Bytes received during the speed measurement (Download).';

comment on column ias_measurements.requested_duration_download_ns is 'The nominal measurement duration of the download measurement.';

comment on column ias_measurements.requested_duration_upload_ns is 'The nominal measurement duration of the upload measurement.';

comment on column ias_measurements.duration_upload_ns is 'Duration of the upload measurement.';

comment on column ias_measurements.duration_download_ns is 'Duration of the download measurement.';

comment on column ias_measurements.relative_start_time_upload_ns is 'Relative start time of the upload measurement in nanoseconds.';

comment on column ias_measurements.relative_start_time_download_ns is 'Relative start time of the download measurement in nanoseconds.';

comment on column ias_measurements.duration_rtt_ns is 'Duration of the RTT measurement.';

comment on column ias_measurements.relative_start_time_rtt_ns is 'Relative start time of the RTT measurement in nanoseconds.';

comment on column ias_measurements.connection_info is 'Contains information about the connection(s) used for the speed measurement.';

comment on column ias_measurements.rtt_median_ns is 'RTT median in ns';

comment on column ias_measurements.rtt_median_log is 'Common logarithm of the median RTT';

comment on column ias_measurements.speed_raw_data is 'Stores the raw data (amount of bytes in time) values for download and upload.';

comment on column ias_measurements.bytes_upload is 'Bytes transferred during the speed measurement (Upload).';

comment on column ias_measurements.rtt_info is 'Bytes transferred during the speed measurement (Upload).';

comment on column ias_measurements.requested_duration_upload_slow_start_ns is 'The nominal duration for the upload slow-start phase.';

comment on column ias_measurements.requested_duration_download_slow_start_ns is 'The nominal duration for the download slow-start phase.';

alter table ias_measurements owner to nntool;

create table providers
(
	id bigserial not null
		constraint providers_pkey
			primary key,
	name varchar(255) not null,
	short_name varchar(64) not null,
	asn_mappings jsonb,
	mcc_mnc_mappings jsonb
);

comment on column providers.name is 'The name of the provider.';

comment on column providers.short_name is 'The short name (or shortcut) of the provider.';

comment on column providers.asn_mappings is 'Contains a list of all valid/possible ASN mappings for this provider.';

comment on column providers.mcc_mnc_mappings is 'Contains a list of all valid/possible MCC/MNC mappings for this provider.';

alter table providers owner to nntool;

create table measurement_servers
(
	uuid uuid not null
		constraint measurement_servers_pkey
			primary key,
	type varchar,
	name varchar,
	port integer,
	port_tls integer,
	address_ipv4 varchar,
	address_ipv6 varchar,
	enabled boolean,
	secret_key varchar,
	info_city varchar,
	info_country varchar,
	geo_location_latitude double precision,
	geo_location_longitude double precision
);

comment on column measurement_servers.type is 'Measurement server type (e.g. SPEED, QOS)';

comment on column measurement_servers.name is 'Name (label) of this measurement server.';

comment on column measurement_servers.port is 'Port used for non-encrypted communication.';

comment on column measurement_servers.port_tls is 'Port used for encrypted communication.';

comment on column measurement_servers.address_ipv4 is 'The measurement server''s IPv4 address or name.';

comment on column measurement_servers.address_ipv6 is 'The measurement server''s IPv6 address or name.';

comment on column measurement_servers.enabled is 'Flag that indicates if this measurement server is enabled.';

comment on column measurement_servers.secret_key is 'The measurement server''s secret key used to generate measurement tokens.';

comment on column measurement_servers.info_city is 'The city the measurement server is located in.';

comment on column measurement_servers.info_country is 'The country the measurement server is located in.';

comment on column measurement_servers.geo_location_latitude is 'Geographic location latitude.';

comment on column measurement_servers.geo_location_longitude is 'Geographic location longitude.';

alter table measurement_servers owner to nntool;

