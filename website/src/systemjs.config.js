(function (global) {
    var paths = {
        // paths serve as alias
        'npm:': 'lib/'
    };
    var map = {
        // our app is within the app folder
        app: 'app',
        // angular bundles
        '@angular': 'lib/@angular',
        'tslib': 'lib/tslib/tslib.js',
        '@angular/common/http':       'npm:@angular/common/bundles/common-http.umd.js',
        'angular2-in-memory-web-api': 'lib/angular2-in-memory-web-api',
        '@ngx-translate/core': 'lib/@ngx-translate/core/bundles',
        '@ngx-translate/http-loader': 'lib/@ngx-translate/http-loader/bundles',
        'ng2-nvd3': 'npm:ng2-nvd3',
        'rxjs': 'lib/rxjs',
        'rxjs/operators': 'lib/rxjs/operators',
        'leaflet': 'lib/leaflet/dist',
        'nettest-test-module': 'lib/nettest-test-module/dist',
        '@zafaco/breitbandtest': 'lib/@zafaco/breitbandtest',
    };
    var packages = {
        app: {main: './main.js', defaultExtension: 'js'},
        rxjs: {main: 'index.js', defaultExtension: 'js'},
        'rxjs/operators': {main: 'index.js', defaultExtension: 'js'},
        'nettest-test-module': {main: 'index.js', defaultExtension: 'js'},
        '@zafaco/breitbandtest': {
            main: 'ias.web.js',
            defaultExtension: 'js',
            map: {
                'platform': '@empty',
                'tiny-worker': '@empty',
                'path': '@empty',
                'ws': '@empty',
            }
        },
        'leaflet': {main: 'leaflet.js', defaultExtension: 'js'},
        '@ngx-translate/core': {main: 'ngx-translate-core.umd.min.js', defaultExtension: 'js'},
        '@ngx-translate/http-loader': {main: 'ngx-translate-http-loader.umd.min.js', defaultExtension: 'js'},
        'ng2-nvd3': {main: 'build/index.js', defaultExtension: 'js'}
    };
    var ngPackageNames = [
        'common',
        'compiler',
        'core',
        'http',
        'forms',
        'platform-browser',
        'platform-browser-dynamic',
        'router'
    ];

    // Individual files (~300 requests):
    function packIndex(pkgName) {
        packages['@angular/'+pkgName] = { main: 'index.js', defaultExtension: 'js' };
    }
    // Bundled (~40 requests):
    function packUmd(pkgName) {
        packages['@angular/'+pkgName] = { main: '/bundles/' + pkgName + '.umd.js', defaultExtension: 'js' };
    }
    // Most environments should use UMD; some (Karma) need the individual index files
    var setPackageConfig = System.packageWithIndex ? packIndex : packUmd;
    // Add package entries for angular packages
    ngPackageNames.forEach(setPackageConfig);

    System.config({
        paths: paths,
        // map tells the System loader where to look for things
        map: map,
        // packages tells the System loader how to load when no filename and/or no extension
        packages: packages
    });
})(this);