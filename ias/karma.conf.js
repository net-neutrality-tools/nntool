// Karma configuration
// Generated on Sat Jun 08 2019 14:37:18 GMT+0200 (GMT+02:00)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',

    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],

    // list of files / patterns to load in the browser
    files: [
      'build/plain/web/ias.web.js',
      'src/**/*.spec.js'
    ],

    // list of files / patterns to exclude
    exclude: [

    ],

    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
      'build/plain/web/ias.web.js': ['generic']
    },

    // Use genericPreprocessor to instrument source files since karma-coverage doesn't work anymore
    // and we don't use webpack or typescript.
    genericPreprocessor: {
      rules: [{
        process: function (content, file, done, log) {
          var inst = require('istanbul-lib-instrument');
          const instrumenter = inst.createInstrumenter({ produceSourceMap: true });

          instrumenter.instrument(content, file.path, (err, instrumentedSource) => {
            if (err) {
              log.error('%s\n  at %s', err.message, file.originalPath);
              done(err, null);
            } else {
              log.debug("finished instrumenting " + file.path);
              done(instrumentedSource);
            }
          });
        }
      }]
    },
  
    client: {
      clearContext: false // leave Jasmine Spec Runner output visible in browser
    },

    coverageIstanbulReporter: {
      reports: ['html', 'lcovonly', 'text-summary'],
      skipFilesWithNoCoverage: false,
    },

    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress', 'kjhtml', 'coverage-istanbul'],

    // web server port
    port: 9876,

    // enable / disable colors in the output (reporters and logs)
    colors: true,

    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,

    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,

    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['Chrome'],

    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,

    restartOnFileChange: true,

    // Concurrency level
    // how many browser should be started simultaneous
    concurrency: Infinity
  })
}
