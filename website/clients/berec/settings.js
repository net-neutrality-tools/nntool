module.exports = {
    features: {
        "home": true,
        "tc": true,
        "nettest": true,
        "statistics": true,
        "map": true,
        "history": true,
        "testresults": true,
        "help": true,
        "opendata": true,
        "about": false,
        "footer": true,
        "settings": true,
        "docu": false
    },
    landing_page: "home",
    user_agent: "abc",
    languages: [
        "en",
    ],
    servers: {
        map: "",
        statistic: "",
        control: "http://localhost:8080/api/v1/"
    },
    keys: {
        "google": ""
    },
    classificationColorStyle: "background",
    colors: {
        groups: {
            pink: ["#da7883", "#d16b74", "#a65056", "#8a3d43"],
            orange: ["#ffce99", "#ffab65", "#ff8118", "#e67416"],
            purple: ["#e097ff", "#bb8ed8", "#a34ed8", "#69328b"],
            turquoise: ["#c8ebef", "#90bcbf", "#739c9e", "#64878a"],
            green: ["#bfdb2d", "#a6bf27", "#8a9f21", "#71821b"]
        },
        gauge: {
            arc_background: "#EFEFEF",
            arc_inner: "#921F56",
            arc_outer: "#29348A",
            fontName: "arial",
            font: "#FFFFFF"
        }
    },
    nettest: {
        custom_tc: false,
        tag: null,
        tests: {
            qos: false,
            ndt: true,
            rmbt: true
        }
    },
    map: {
        showLegend: true,
        view: {
            default_map: "google",
            map_provider: ["google", "googleSatellite", "googleTerrain", "googleHybrid", "osm"],
            position: [48.209, 16.37],
            zoom_initial: 12,
            zoom_min: 1,
            zoom_max: 20,
            hybrid: {
                zoom_level: 15
            }
        },
        filter_defaults: {
            map_options: "all/download"
        },
        options: {
            statistical_method: [],
            map_options: [],
            period: [],
            technology: [],
            operator: [],
            layer_type: [
                {value: "hybrid", name: "MAP.SELECT.LAYER.HYBRID", unit: "", "default": true},
                {value: "heatmap", name: "MAP.SELECT.LAYER.HEATMAP", unit: ""},
                {value: "points", name: "MAP.SELECT.LAYER.POINTS", unit: ""}
            ]
        }
    },
    opendata: {
        external: [
            
        ],
        allow_full_download: false,
        report_start_date: "2017/03/01"
    },
    statistics: {
        graphs: {
            list_filters: {

            },
            list_devices: {
                enabled: false
            },
            list_providers: {
                enabled: false
            },
            os_part: {
                enabled: true
            },
            technology_part: {
                enabled: true
            },
            measurements_per_provider_speed: {
                enabled: false
            },
            measurements_per_provider_over_time_speed: {
                enabled: true,
                show_box: 6,
                select_method: "random",
                select_count: 6
            },
            measurements_per_provider_over_time_count: {
                enabled: true
            },
            measurements_per_technology_over_time: {
                enabled: true
            }
        }
    },
    result_list: {
        history_graph_enabled: true
    },
    result: {
        keys: [
            {
                header: "RESULT.DETAIL.SECTION.PROVIDER.HEADER",
                icon: "f",
                values: [
                    {
                        key: "cat_technology"
                    },
                    {
                        key: "ip_anonym"
                    },
                    {
                        key: "country_geoip"
                    },
                    {
                        key: "network_name"
                    },
                    {
                        key: "network_type"
                    }
                ]
            },
            {
                header: "RESULT.DETAIL.SECTION.UNCATECORIZED.HEADER",
                icon: "m",
                values: [
                    {
                        key: "asn"
                    },
                    {
                        key: "bytes_download",
                        formats: [
                            {
                                format: "number",
                                factor: 0.000001
                            },
                            {
                                format: "speed"
                            }
                        ],
                        unit: "MB"
                    },
                    {
                        key: "bytes_upload",
                        formats: [
                            {
                                format: "number",
                                factor: 0.000001
                            },
                            {
                                format: "speed"
                            }
                        ],
                        unit: "MB"
                    },
                    {
                        key: "client_version"
                    },
                    {
                        key: "community"
                    },
                    {
                        key: "connection"
                    },
                    {
                        key: "country_asn"
                    },
                    {
                        key: "country_location"
                    },
                    {
                        key: "district"
                    },
                    {
                        key: "distance"
                    },
                    {
                        key: "download_classification"
                    },
                    {
                        key: "download_kbit",
                        formats: [
                            {
                                format: "number",
                                factor: 0.001
                            },
                            {
                                format: "speed"
                            }
                        ],
                        unit: "Mbit/s"
                    },
                    {
                        key: "duration_download_ms",
                        formats: [
                            {
                                format: "ping"
                            }
                        ],
                        unit: "ms"
                    },
                    {
                        key: "duration_upload_ms",
                        formats: [
                            {
                                format: "ping"
                            }
                        ],
                        unit: "ms"
                    },
                    {
                        key: "lat",
                        formats: [
                            {
                                format: "fixed",
                                places: 4
                            }
                        ]
                    },
                    {
                        key: "long",
                        formats: [
                            {
                                format: "fixed",
                                places: 4
                            }
                        ]
                    },
                    {
                        key: "loc_accuracy"
                    },
                    {
                        key: "loc_src"
                    },

                    {
                        key: "lte_rsrp",
                        unit: "dBm"
                    },

                    {
                        key: "lte_rsrq",
                        unit: "dBm"
                    },
                    {
                        key: "model"
                    },
                    {
                        key: "model_native"
                    },
                    {
                        key: "network_country"
                    },
                    {
                        key: "network_mcc_mnc"
                    },
                    {
                        key: "num_threads"
                    },
                    {
                        key: "num_threads_requested"
                    },
                    {
                        key: "num_threads_ul"
                    },
                    {
                        key: "open_test_uuid"
                    },
                    {
                        key: "ping_classification"
                    },
                    {
                        key: "ping_ms",
                        formats: [
                            {
                                format: "ping"
                            }
                        ],
                        unit: "ms"
                    },
                    {
                        key: "ping_variance",
                        formats: [
                            {
                                format: "ping"
                            }
                        ],
                        unit: "ms"
                    },
                    {
                        key: "platform"
                    },
                    {
                        key: "public_ip_as_name"
                    },
                    {
                        key: "product"
                    },
                    {
                        key: "province"
                    },
                    {
                        key: "provider_name"
                    },
                    {
                        key: "roaming_type"
                    },
                    {
                        key: "server_name"
                    },
                    {
                        key: "signal_classification"
                    },
                    {
                        key: "signal_strength",
                        unit: "dBm"
                    },
                    {
                        key: "sim_country"
                    },
                    {
                        key: "test_duration",
                        unit: "s"
                    },
                    {
                        key: "test_if_bytes_download",
                        formats: [
                            {
                                format: "number",
                                factor: 0.000001
                            },
                            {
                                format: "speed"
                            }
                        ],
                        unit: "MB"
                    },
                    {
                        key: "test_if_bytes_upload",
                        formats: [
                            {
                                format: "number",
                                factor: 0.000001
                            },
                            {
                                format: "speed"
                            }
                        ],
                        unit: "MB"
                    },
                    {
                        key: "testdl_if_bytes_download",
                        formats: [
                            {
                                format: "number",
                                factor: 0.000001
                            },
                            {
                                format: "speed"
                            }
                        ],
                        unit: "MB"
                    },
                    {
                        key: "testdl_if_bytes_upload",
                        formats: [
                            {
                                format: "number",
                                factor: 0.000001
                            },
                            {
                                format: "speed"
                            }
                        ],
                        unit: "MB"
                    },
                    {
                        key: "testul_if_bytes_download",
                        formats: [
                            {
                                format: "number",
                                factor: 0.000001
                            },
                            {
                                format: "speed"
                            }
                        ],
                        unit: "MB"
                    },
                    {
                        key: "testul_if_bytes_upload",
                        formats: [
                            {
                                format: "number",
                                factor: 0.000001
                            },
                            {
                                format: "speed"
                            }
                        ],
                        unit: "MB"
                    },
                    {
                        key: "time",
                        formats: [
                            {
                                format: "utc2local"
                            }
                        ]
                    },
                    {
                        key: "time_dl_ms",
                        formats: [
                            {
                                format: "ping"
                            }
                        ],
                        unit: "ms"
                    },
                    {
                        key: "time_ul_ms",
                        formats: [
                            {
                                format: "ping"
                            }
                        ],
                        unit: "ms"
                    },
                    {
                        key: "upload_classification"
                    },
                    {
                        key: "upload_kbit",
                        formats: [
                            {
                                format: "number",
                                factor: 0.001
                            },
                            {
                                format: "speed"
                            }
                        ],
                        unit: "Mbit/s"
                    },
                    {
                        key: "wifi_link_speed",
                        unit: "Mbit/s"
                    },
                    {
                        key: "zip_code"
                    }
                ]
            }
        ],
        view: {
            zoom_initial: 15,
            zoom_min: 4,
            zoom_max: 18,
            default_map: "google",
            map_provider: ["google", "osm"]
        },
        graph: {
            step_ms: 10
        }
    },
    user: {
        allow_query_uuid: true,
        allow_set_uuid: false,
        shown: {
            force_ip4: false,
            invisible: false,
            anonymous_mode: true,
            no_anonymize_before_delete_user: false,
            delete_user: true,
            client_uuid: true
        }
    }
};
