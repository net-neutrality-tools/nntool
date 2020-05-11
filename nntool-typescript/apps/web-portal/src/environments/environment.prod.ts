/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

export const environment = {
  production: true,
  //////////
  features: {
    home: true,
    tc: true,
    nettest: true,
    statistics: true,
    map: true,
    history: true,
    testresults: true,
    help: true,
    opendata: true,
    about: false,
    footer: true,
    settings: true,
    docu: false
  },
  landing_page: 'home',
  user_agent: 'abc',
  languages: ['en', 'de'],
  servers: {
    control: 'https://controller-de-01.net-neutrality.tools/api/v1/',
    result: 'https://result-de-01.net-neutrality.tools/api/v1/',
    search: 'https://search-de-01.net-neutrality.tools/api/v1/',
    map: 'https://map-de-01.net-neutrality.tools/api/v0/',
    statistic: 'https://statistic-de-01.net-neutrality.tools/api/v1/'
  },
  keys: {
    bing: ''
  },
  classificationColorStyle: 'background',
  colors: {
    groups: {
      pink: ['#da7883', '#d16b74', '#a65056', '#8a3d43'],
      orange: ['#ffce99', '#ffab65', '#ff8118', '#e67416'],
      purple: ['#e097ff', '#bb8ed8', '#a34ed8', '#69328b'],
      turquoise: ['#c8ebef', '#90bcbf', '#739c9e', '#64878a'],
      green: ['#bfdb2d', '#a6bf27', '#8a9f21', '#71821b']
    },
    gauge: {
      arc_background: '#EFEFEF',
      arc_inner: '#921F56',
      arc_outer: '#29348A',
      fontName: 'arial',
      font: '#FFFFFF'
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
  deserializeTypes: {
    registrationRequestDeserializeType:
      'at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest',
    settingsRequestDeserializeType:
      'at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsRequest',
    speedMeasurementPeerRequestDeserializeType:
      'at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerRequest'
  },
  map: {
    showLegend: true,
    view: {
      default_map: 'google',
      map_provider: ['google', 'googleSatellite', 'googleTerrain', 'googleHybrid', 'osm'],
      position: [48.209, 16.37],
      zoom_initial: 12,
      zoom_min: 1,
      zoom_max: 20,
      hybrid: {
        zoom_level: 15
      }
    },
    filter_defaults: {
      map_options: 'all/download'
    },
    options: {
      statistical_method: [],
      map_options: [],
      period: [],
      technology: [],
      operator: [],
      layer_type: [
        { value: 'hybrid', name: 'MAP.SELECT.LAYER.HYBRID', unit: '', default: true },
        { value: 'heatmap', name: 'MAP.SELECT.LAYER.HEATMAP', unit: '' },
        { value: 'points', name: 'MAP.SELECT.LAYER.POINTS', unit: '' }
      ]
    }
  },
  opendata: {
    startDate: {
      year: 2019,
      month: 9
    }
  },
  statistics: {},
  user: {
    allow_query_uuid: true,
    allow_set_uuid: false,
    shown: {
      force_ip4: true,
      invisible: false,
      anonymous_mode: true,
      no_anonymize_before_delete_user: false,
      delete_user: true,
      client_uuid: true,
      measurement_selection: true,
      measurement_selection_speed: true,
      measurement_selection_qos: true
    }
  },
  socialMediaSettings: {
    history: {
      medias: ['FACEBOOK', 'TWITTER', 'MAIL', 'WHATSAPP']
    }
  }
};
