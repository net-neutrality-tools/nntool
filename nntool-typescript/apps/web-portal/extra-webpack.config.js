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

const webpack = require('webpack');
const CopyPlugin = require('copy-webpack-plugin');

const asciidoctor = require('asciidoctor.js')();

module.exports = (config, options) => {
  config.plugins.push(
    new webpack.DefinePlugin({
      __TEST_CONFIG__: JSON.stringify({
        // TODO
        version: '1.2.3',
        branch: 'master',
        revision: 'test',
        client: 'berec'
      })
    })
  );

  var asciidoctorOptions = {
    safe: 'secure', //unsafe, safe, server or secure
    doctype: options.doctype || 'article', //book,inline
    attributes: ['showToc', 'showNumberedHeadings', 'showTitle'],
    header_footer: false
  };

  config.plugins.push(
    new CopyPlugin([
      {
        from: 'src/adoc/**/*.adoc',
        to: 'assets/i18n/view/',
        transform(content, path) {
          var renderedContent = asciidoctor.convert(content, asciidoctorOptions);
          return renderedContent;
        },
        transformPath(targetPath, absolutePath) {
          //console.log(targetPath);
          //console.log(absolutePath);
          //console.log(targetPath.replace('src/adoc/', '').replace('.adoc', '.html'));
          return Promise.resolve(targetPath.replace('src/adoc/', '').replace('.adoc', '.html'));
        }
      }
    ])
  );

  /*config.plugins.push(
    new webpack.BannerPlugin('----- nntool.eu -----')
  );*/

  return config;
};
