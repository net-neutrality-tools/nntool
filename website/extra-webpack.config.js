const webpack = require('webpack');
const pkg = require('./package.json');

module.exports = (config, options) => {
  config.plugins.push(
    new webpack.DefinePlugin({
      __TEST_CONFIG__: JSON.stringify({ // TODO
          version: "1.2.3",
          branch: "master",
          revision: "test",
          client: "berec"
      })
    }),
  );

  /*config.plugins.push(
    new webpack.BannerPlugin('----- nntool.eu -----')
  );*/

  return config;
};