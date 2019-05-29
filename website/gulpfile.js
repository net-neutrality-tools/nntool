const srcDir = 'src';
const clientDir = "./clients";
const stageDir = 'stage';
const buildDir = 'dist';
const bundleDir = 'bundled';

const yargs = require('yargs');
const merge = require('deepmerge');
var argv = yargs.argv;
var client = argv.client || "berec";
var clientDeploySettings = {};
var clientIncludeList = [];
var loadingText = "We are currently loading your content";
var includeTest = false;
var deployProfile = argv.deploy || "default";
var package;
var settingsApp = {};
var settingsPaths = argv.settings || [];
var i18n = {};
var i18nPaths = argv.i18n || [];

try {
    clientDeploySettings = require('./' + clientDir + '/' + client + '/deploy.js');
    if (!clientDeploySettings.hasOwnProperty("default")) {
        console.warn("No default deploy profile - Assuming no profiles");
    }

    if (clientDeploySettings.hasOwnProperty(deployProfile)) {
        clientDeploySettings = clientDeploySettings[deployProfile];
    } else {
        console.warn("Deploy profile not found - defaulting..");
        clientDeploySettings = clientDeploySettings.default;
    }
} catch (e) {
    console.error(clientDir + "/" + client + "/deploy.js not found");
}

try {
    clientIncludeList = require('./' + clientDir + '/' + client + '/include.js');
} catch (e) {
    console.warn(clientDir + "/" + client + "/include.js not found");
}

try {
    loadingText = require(clientDir + '/' + client + '/loadingText.js');
} catch (e) {
    console.warn(clientDir + "/" + client + "/loadingText.js not found");
}

try {
    package = require('./package.json');
} catch (e) {
    console.warn("./package.json not found");
}

try {
    if (typeof settingsPaths === "string") {
        settingsPaths = [settingsPaths];
    }
    settingsPaths.unshift(clientDir + '/' + client + '/settings.js');

    for (var i = 0; i < settingsPaths.length; i++) {
        try {
            var loaded = require(settingsPaths[i]);
            settingsApp = merge(settingsApp, loaded);
        } catch (e) {
            console.error("Failed to load settings file: " + settingsPaths[i], e);
        }
    }
    includeTest = settingsApp.features.nettest;
} catch (e) {
    console.error("Failed to load settings");
}

function loadI18n () {
    console.info("loading i18n");
    i18n = {};
    if (typeof i18nPaths === "string") {
        i18nPaths = [i18nPaths];
    }

    for (var i = 0; i < settingsApp.languages.length; i++) {
        var base = {};
        var lang = settingsApp.languages[i];
        try {
            delete require.cache[require.resolve('./' + srcDir + '/i18n/' + lang + '.json')];
            base = require('./' + srcDir + '/i18n/' + lang + '.json');
        } catch (e) {
            if (e.code && e.code === "MODULE_NOT_FOUND") {
                console.warn("No i18n base file: " + lang);
            } else {
                console.error("No i18n base file: " + lang, e);
            }
        }
        try {
            delete require.cache[require.resolve(clientDir + '/' + client + '/i18n/' + lang + '.json')];
            base = merge(base, require(clientDir + '/' + client + '/i18n/' + lang + '.json'));
        } catch (e) {
            if (e.code && e.code === "MODULE_NOT_FOUND") {
                console.warn("No i18n client file: " + lang);
            } else {
                console.warn("No i18n client file: " + lang, e);
            }
        }

        for (var j = 0; j < i18nPaths.length; j++) {
            try {
                delete require.cache[require.resolve(i18nPaths[j] + '/' + lang + '.json')];
                var loaded = require(i18nPaths[j] + '/' + lang + '.json');
                base = merge(base, loaded);
            } catch (e) {
                if (e.code && e.code === "MODULE_NOT_FOUND") {
                    console.error("Failed to load settings file: " + settingsPaths[i] + " / " + lang);
                } else {
                    console.error("Failed to load settings file: " + settingsPaths[i] + " / " + lang, e);
                }
            }
        }
        if (Object.keys(base).length === 0) {
            throw new Error("No translation for " + lang);
        }
        i18n[lang] = base;
    }
}

try {
    loadI18n();
} catch (e) {
    console.error("Failed to load i18n", e);
}

const gulp = require('gulp');
const del = require('del');
const typescript = require('gulp-typescript');
const tscConfig = require('./tsconfig.json');
const sourcemaps = require('gulp-sourcemaps');
const gulpTslint = require('gulp-tslint');
const tslint = require('tslint');
const lite = require('lite-server');
var server = null;
const rename = require('gulp-rename');

const less = require('gulp-less');
const replace = require('gulp-replace');
const adoc = require('gulp-asciidoctor');
const rsync = require('gulp-rsync');
const inject = require('gulp-inject');
const bundler = require('systemjs-builder');
const gconcat = require('gulp-concat');
var isProd = true;
const pump = require('pump');
const uglify = require('gulp-uglify');
const jsonminify = require('gulp-jsonminify');
const cleanCSS = require('gulp-clean-css');
const gulpIgnore = require('gulp-ignore');
const spawnSync = require('child_process').spawnSync;
const fs = require('fs');
const git = require('gulp-git');
const stringifyObject = require('stringify-object');
const saveLicense = require('uglify-save-license');


// TODO: Set var for angular
gulp.task('set-env:dev', function() {
    isProd = false;
    return gulp
        .src(clientDir + '/dev.settings.ts')
        .pipe(rename('env.ts'))
        .pipe(gulp.dest(stageDir + '/app/settings/'));
});

gulp.task('set-env:prod', function() {
    return gulp
        .src(clientDir + '/prod.settings.ts')
        .pipe(rename('env.ts'))
        .pipe(gulp.dest(stageDir + '/app/settings/'));
});


function serve () {
    server = lite.server();
    return server;
}

gulp.task('serve', function (cb) {
   serve();
   cb();
});

gulp.task('serve:reload', function (cb) {
    if (server) {
        server.reload();
    }
    console.log("Reloaded");
    cb();
});

gulp.task('serve:pause', function (cb) {
    if (server) {
        server.pause();
    }
    console.log("Paused");
    cb();
});

gulp.task('serve:resume', function (cb) {
    if (server) {
        server.resume();
    }
    console.log("Resumed");
    cb();
});


/**
 * Clean the contents of the build directory
 */
gulp.task('clean:build', () => {
    return del(buildDir + '/**/*');
});

/**
 * Clean the contents of the staging directory
 */
gulp.task('clean:stage', function () {
    return del(stageDir + '/**/*');
});

/**
 * Clean the contents of the bundled directory
 */
gulp.task('clean:bundle', function () {
    return del(bundleDir + '/**/*');
});

/**
 * Clean the contents of all directories
 */
gulp.task('clean:all', gulp.series('clean:build', 'clean:stage', 'clean:bundle'));

/**
 * Clean compiled js - stage
 */
gulp.task('clean:app:stage', function () {
    return del([
        stageDir + '/app/**/*.js*',
        '!' + stageDir + '/app'
    ]);
});

/**
 * Clean compiled js - build
 */
gulp.task('clean:app:build', function () {
    return del([
        buildDir + '/app/**/*.js*',
        '!' + buildDir + '/app'
    ]);
});

/**
 * Clean compiled js - build+stage
 */
gulp.task('clean:app', gulp.series('clean:app:stage', 'clean:app:build'));

/**
 * Clean libraries - build
 */
gulp.task('clean:libs', function () {
    return del([
        buildDir + '/lib/**/*'
    ]);
});

gulp.task('clean:assets', function () {
    return del([
        buildDir + '/assets/**/*',
        buildDir + '/app/**/*.html',
        '!' + buildDir + '/assets',
        '!' + buildDir + '/assets/css',
        '!' + buildDir + '/assets/css/client.css'
    ]);
});

/**
 * Clean language files
 */
gulp.task('clean:i18n', function () {
    return del([
        buildDir + '/i18n/*.json'
    ]);
});

/**
 * Clean client css
 */
gulp.task('clean:less', function () {
    return del([
        buildDir + '/assets/css/client.css'
    ]);
});

/**
 * Clean client directory
 */
gulp.task('clean:client', function () {
    return del([
        buildDir + '/client/**/*'
    ]);
});

/**
 * Clean adoc language files
 */
gulp.task('clean:adoc', function () {
    return del([
        buildDir + '/i18n/view/**/*'
    ]);
});


/**
 * Lint typescript on stage
 */
gulp.task('tslint', function () {
    var program = tslint.Linter.createProgram('./tsconfig.json');

    return gulp.src(stageDir + '/app/**/*.ts')
        .pipe(gulpTslint({
            program: program,
            formatter: "prose"
        }))
        .pipe(gulpTslint.report());
});


/**
 * TypeScript compile (only) - stage->build
 */
gulp.task('compile:ts:do', function () {
    return gulp
        .src([stageDir + '/app/**/*.ts', stageDir + '/app/**/*.js'], {base: stageDir + '/'})
        .pipe(sourcemaps.init())
        .pipe(typescript(tscConfig.compilerOptions))
        .pipe(sourcemaps.write('.', {sourceRoot: stageDir/*'/src'*/}))
        .pipe(gulp.dest(buildDir));
});

/**
 * Copy and compile typescript - src->stage->build
 */
gulp.task('compile:ts', function (cb) {
    gulp.series(
        //['copy:ts', 'clean:app:build'],
        'copy:ts', 
        'clean:app:build',
        'tslint',
        'compile:ts:do',
        //cb
    )(cb);
});

/**
 * Compile client less - client->src
 */
// TODO: staging
gulp.task('compile:less', gulp.series('clean:less', () => {
    return gulp
        .src([
            clientDir + '/' + client + '/less/client.less'
        ])
        .pipe(less())
        .pipe(replace('../' + srcDir + '/', '../'))
        // change root relative to relative paths for electron
        .pipe(replace('url(\'../', 'url(\'./../'))
        .pipe(replace('url(\'/', 'url(\'./../../'))
        .pipe(gulp.dest(buildDir + '/assets/css/'))
}));

/**
 * Compile client adoc - src->build
 */
gulp.task('compile:adoc', gulp.series('clean:adoc', () => {
    return gulp
        .src([
            clientDir + '/' + client + '/adoc/*/*.adoc'
        ])
        .pipe(adoc({
            header_footer: false,
            attributes: ['showToc', 'showNumberedHeadings', 'showTitle']
        }))
        .pipe(gulp.dest(buildDir + '/i18n/view/'))
}));


/**
 * Copy libraries - direct
 */
gulp.task('copy:libs', gulp.series('clean:libs', () => {
    return gulp.src(
        [
            'core-js/client/shim.min.js',
            'zone.js/dist/**',
            'leaflet/dist/leaflet.js',
            'leaflet/dist/leaflet.css',
            'leaflet/dist/images/*',
            //'leaflet.gridlayer.googlemutant/Leaflet.GoogleMutant.js',
            'reflect-metadata/Reflect.js',
            'systemjs/dist/system-polyfills.js',
            'systemjs/dist/system.src.js',
            'rxjs/**/*.js*',
            'tslib/tslib.js',
            '@ngx-translate/core/bundles/*.js',
            '@ngx-translate/http-loader/bundles/*.js',
            'ng2-nvd3/build/index.js',
            'ng2-nvd3/build/lib/*.js',
            'd3/d3.min.js',
            'nvd3/build/nv.d3.min.js',
            'nvd3/build/nv.d3.min.css',
            'nvd3/build/nv.d3.min.js.map',
            '@angular/*/bundles/*',
            'nettest-test-module/dist/**/*',
            '@zafaco/breitbandtest/*.js'
        ],
        {
          cwd: "node_modules",
          base: "node_modules"
        }
    ).pipe(
        gulp.dest(buildDir + '/lib')
    )
}));

gulp.task('copy:assets:main:test', function () {
    return gulp.src(
        [srcDir + '/app/test/**/*.html'],
        {base : srcDir + '/'}
    )
        .pipe(gulpIgnore.exclude(!includeTest))
        .pipe(
            gulp.dest(buildDir + '/')
        );
});

/**
 * Copy static assets - i.e. non TypeScript compiled source / non app js - direct? (except test)
 */
gulp.task('copy:assets:main', gulp.series('copy:assets:main:test', () => {
    return gulp.src(
        [
            srcDir + '/app/**/*.html',
            "!" + srcDir + '/app/test/**/*.html',
            srcDir + '/assets/**/*',
            srcDir + '/systemjs.config.js',
            srcDir + '/browserconfig.xml'
        ],
        {base : srcDir + '/'}
    ).pipe(
        gulp.dest(buildDir + '/')
    )
}));

/**
 * Copy images from client - direct
 */
gulp.task('copy:client:img', () => {
    return gulp.src(
        [
            clientDir + '/' + client + '/img/*'
        ],
        {base : clientDir + '/' + client + '/img/'}
    ).pipe(
        gulp.dest(buildDir + '/assets/img/')
    )
});

/**
 * Copy fonts from client - direct
 */
gulp.task('copy:client:fonts', () => {
    return gulp.src(
        [
            clientDir + '/' + client + '/fonts/*'
        ],
        {base : clientDir + '/' + client + '/fonts/'}
    ).pipe(
        gulp.dest(buildDir + '/assets/fonts/')
    )
});

/**
 * Copy client js - direct
 */
gulp.task('copy:client:js', () => {
    return gulp.src(
        [
            clientDir + '/' + client + '/js/*'
        ],
        {base : clientDir + '/' + client + '/js/'}
    ).pipe(
        gulp.dest(buildDir + '/assets/js/')
    )
});

/**
 * Copy client download files - direct
 */
gulp.task('copy:client:download', () => {
    return gulp.src(
        [
            clientDir + '/' + client + '/download/*'
        ],
        {base : clientDir + '/' + client + '/download/'}
    ).pipe(
        gulp.dest(buildDir + '/assets/download/')
    )
});

/**
 * Copy templates from client
 *
 * src->build
 */
gulp.task('copy:client:templates', function () {
    return gulp.src(
        [
            clientDir + '/' + client + '/ts/*/*.html'
        ],
        {base : clientDir + '/' + client + '/ts/'}
    ).pipe(
        gulp.dest(buildDir + '/app/')
    )
});

/**
 * Copy client and own assets - direct?
 */
gulp.task('copy:assets',  function (cb) {
    gulp.series(
        'clean:assets',
        'copy:assets:main',
        /*[
            'copy:client:js',
            'copy:client:img',
            'copy:client:fonts',
            'copy:client:download',
            'copy:client:templates',
            'copy:i18n'
        ],*/
        'copy:client:js',
        'copy:client:img',
        'copy:client:fonts',
        'copy:client:download',
        'copy:client:templates',
        'copy:i18n',
        //cb
    )(cb);
});

/**
 * Copy language files
 *
 * src->build; client->build;
 */
gulp.task('copy:i18n', gulp.series('clean:i18n', (cb) => {
    gulp.series(
        'create:i18n',
        //cb
    )(cb);
}));

/**
 * Copy typescript to staging
 *
 * src->stage; client->stage;
 */
gulp.task('copy:ts', function (cb) {
    gulp.series(
        'clean:app:stage',
        //['copy:ts:main', 'create:constants'],
        //['create:settings', 'copy:ts:client'],
        'copy:ts:main', 
        'create:constants',
        'create:settings', 
        'copy:ts:client',
        //cb
    )(cb);
});

gulp.task('copy:ts:test:real', function () {
    return gulp.src(
        [
            srcDir + '/app/test/**/*.ts',
            srcDir + "/app/**/*.js",
            "!" + srcDir + "/app/test/notest.component.ts",
            "!" + srcDir + "/app/test/notest.module.ts"
        ],
        {base : srcDir + '/'}
    )
        .pipe(gulpIgnore.exclude(!includeTest))
        .pipe(
            gulp.dest(stageDir + '/')
        );
});

gulp.task('copy:ts:test:mock:module', function () {
    return gulp.src(
        [srcDir + "/app/test/notest.module.ts"]
    )
        .pipe(gulpIgnore.exclude(includeTest))
        .pipe(rename('test.module.ts'))
        .pipe(
            gulp.dest(stageDir + '/app/test/')
        );
});

gulp.task('copy:ts:test:mock:component', function () {
    return gulp.src(
        [srcDir + "/app/test/notest.component.ts"]
    )
        .pipe(gulpIgnore.exclude(includeTest))
        .pipe(rename('test.component.ts'))
        .pipe(
            gulp.dest(stageDir + '/app/test/')
        );
});

gulp.task('copy:ts:test:mock', gulp.series('copy:ts:test:mock:module', 'copy:ts:test:mock:component'));

/**
 * Copy main typescript to staging (except test)
 *
 * src->stage;
 */
gulp.task('copy:ts:main', gulp.series('copy:ts:test:real', 'copy:ts:test:mock', () => {
    return gulp.src(
        [srcDir + '/app/**/*.ts', srcDir + "/app/**/*.js", "!" + srcDir + "/app/test/**/*"],
        {base : srcDir + '/'}
    ).pipe(
        gulp.dest(stageDir + '/')
    );
}));

gulp.task('create:constants', function (cb) {
    // git rev-parse [--abbrev-ref] --branches HEAD -> list [name of] branches
    // git rev-parse [--abbrev-ref] --tags HEAD -> list [name of] tags
    // --short -> short hash

    git.revParse({args:'--abbrev-ref HEAD'}, function (err, branch) {
        //console.log('current git branch: ' + branch);

        var ciCommitRefName = process.env.CI_COMMIT_REF_NAME;
        if (ciCommitRefName && ciCommitRefName.length > 0) {
            console.log("In CI context, using CI_COMMIT_REF_NAME (" + ciCommitRefName + ") instead of 'git rev-parse --abbrev-ref HEAD'");
            branch = ciCommitRefName;
        }

        //console.log('current git branch: ' + branch);

        git.revParse({args:'--short HEAD'}, function (err, hash) {
            //console.log('current git commit: ' + hash);
            
            fs.writeFileSync(
                stageDir + '/app/constants.js',
                "module.exports = {\n" +
                '   "version": "' + package.version + '",\n' +
                '   "branch": "' + branch + '",\n' +
                '   "revision": "' + hash + '",\n' +
                '   "client": "' + client + '"\n' +
                "}"
            );
            cb();
        });
    });
});

/**
 * Create settings file (client) locations ->stage
 */
gulp.task('create:settings', function (cb) {
    fs.writeFileSync(
        stageDir + '/app/settings/settings.ts',
        "import {WebsiteSettings} from \"./settings.interface\";\n" +
        "\n\n" +
        "export const settings: WebsiteSettings = " +
        stringifyObject(settingsApp) + ";" +
        "\n"
    );
    cb();
});


/**
 * Create i18n files locations ->dist
 */
gulp.task('create:i18n', function (cb) {
    var dir = buildDir + '/i18n/';
    loadI18n();
    if (!fs.existsSync(dir)){
        fs.mkdirSync(dir);
    }
    for (var lang in i18n) {
        if (!i18n.hasOwnProperty(lang)) {
            continue;
        }
        fs.writeFileSync(
            dir + lang + '.json',
            JSON.stringify(i18n[lang])
        );
    }

    cb();
});


gulp.task('copy:bundle:leaflet', function () {
    // TODO: rewrite leaflet css (replace str)
    return gulp
        .src(
            [
                buildDir + '/lib/leaflet/dist/images/*'
            ],
            {base : buildDir + '/lib/leaflet/dist/'}
        ).pipe(gulp.dest(bundleDir + '/assets/css/'));
});

/**
 * Copy bundle - build->bundle
 */
gulp.task('copy:bundle', gulp.series(/*'copy:bundle:css', */'copy:bundle:leaflet', () => {
    return gulp
        .src(
            [
                buildDir + '/**/*',
                '!' + buildDir + '/app/**/*.js',
                '!' + buildDir + '/assets/js/**/*.js',
                '!' + buildDir + '/**/*.css',
                '!' + buildDir + '/**/*.js.map',
                '!' + buildDir + '/lib',
                '!' + buildDir + '/lib/**/*',
                '!' + buildDir + '/i18n/**/*.json',
                '!' + buildDir + '/systemjs.config.js'
            ],
            {base : buildDir + '/'}
        ).pipe(gulp.dest(bundleDir + '/'));
}));


/**
 * Copy typescript from client - client->stage
 */
gulp.task('copy:ts:client', function () {
    return gulp.src(
        [clientDir + '/' + client + '/ts/**/*.ts'],
        {base : clientDir + '/' + client + '/ts/'}
    ).pipe(
        gulp.dest(stageDir + '/app')
    )
});


/**
 * Inject js/css into index.html - build
 */
gulp.task('inject', gulp.series('copy:assets', 'copy:libs', () => {
    var target = gulp.src(srcDir + '/index.html');
    var sources = gulp.src(
        [
            buildDir + '/assets/css/normalize.css',
            buildDir + '/lib/nvd3/build/nv.d3.min.css',
            buildDir + '/lib/leaflet/dist/leaflet.css',
            buildDir + '/assets/css/**/*',
            '!' + buildDir + '/assets/css/**/*.min.css',
            buildDir + '/lib/nettest-test-module/dist/**/*.js',
            buildDir + '/lib/@zafaco/breitbandtest/ias.web.js',
            buildDir + '/lib/leaflet/dist/leaflet.js',
            buildDir + '/lib/d3/d3.min.js',
            buildDir + '/lib/nvd3/build/nv.d3.min.js',
            buildDir + '/assets/js/**/*',
            '!' + buildDir + '/assets/js/stacktable.js'
        ].concat(clientIncludeList), {read: false}
    );
    target
        .pipe(inject(gulp.src('.'), {
            starttag: '<!-- inject:external -->',
            transform: function () {
                var res = "";
                for (var i = 0, len = clientIncludeList.length; i < len; i++) {
                    res += '<script src="' + clientIncludeList[i] + '"></script>'
                }
                return res;
            },
            empty: true
        }))
        .pipe(inject(gulp.src('.'), {
            starttag: '<!-- inject:loading -->',
            transform: function () {
                return loadingText;
            },
            empty: true
        }))
        .pipe(gulp.dest(buildDir + '/'));

    return target
        .pipe(inject(sources, {ignorePath: buildDir, addRootSlash: false}))
        .pipe(gulp.dest(buildDir))
}));

/**
 * Inject js/css into index-bundled.html - build
 */
gulp.task('inject:bundle', gulp.series('copy:assets', 'copy:libs', () => {
    var target = gulp.src(srcDir + '/index-bundled.html');
    var sources = gulp.src(
        [
            bundleDir + '/assets/css/bundle.min.css',
            bundleDir + '/assets/css/**/*',
            bundleDir + '/assets/js/vendor.min.js',
            bundleDir + '/assets/js/**/*',
            '!' + bundleDir + '/assets/css/bundle.css',
            '!' + bundleDir + '/assets/js/vendor.js',
            '!' + bundleDir + '/assets/js/bundle.app.js',
            '!' + bundleDir + '/assets/js/stacktable.js'
        ].concat(clientIncludeList), {read: false}
    );
    target
        .pipe(rename('index.html'))
        .pipe(inject(gulp.src(''), {
            starttag: '<!-- inject:external -->',
            transform: function () {
                var res = "";
                for (var i = 0, len = clientIncludeList.length; i < len; i++) {
                    res += '<script src="' + clientIncludeList[i] + '"></script>'
                }
                return res;
            },
            empty: true
        }))
        .pipe(inject(gulp.src(''), {
            starttag: '<!-- inject:loading -->',
            transform: function () {
                return loadingText;
            },
            empty: true
        }))
        .pipe(gulp.dest(bundleDir + '/'));

    return target
        .pipe(inject(sources, {ignorePath: bundleDir, addRootSlash: true}))
        .pipe(gulp.dest(bundleDir));
}));


/**
 * Build a part - src->stage->build; client->build; src->build
 */
gulp.task('build:part',  function (cb) {
    gulp.series('compile:ts', 'copy:assets'/*, cb*/)(cb);
});

gulp.task('build', function (cb) {
    gulp.series(
        //['compile:ts', 'compile:less', 'compile:adoc'],
        //['inject'/*, 'copy:i18n'*/],
        'compile:ts', 
        'compile:less', 
        'compile:adoc',
        'inject'/*, 'copy:i18n'*/,
        //cb
    )(cb);
});

gulp.task('build:dev', function (cb) {
    /*return*/ gulp.series(
        'clean:all',
        'set-env:dev',
        'build',
        //cb
    )(cb);
});

gulp.task('build:prod', function (cb) {
    gulp.series(
        'clean:all',
        'set-env:prod',
        'build',
        //cb
    )(cb);
});

gulp.task('build:bundle', function (cb) {
    gulp.series(
        'clean:all',
        'set-env:prod',
        'build',
        'bundle',
        //cb
    )(cb);
});

gulp.task('bundle:css', function () {
    return pump([
        gulp.src([
            buildDir + '/assets/css/normalize.css',
            buildDir + '/lib/nvd3/build/nv.d3.min.css',
            buildDir + '/lib/leaflet/dist/leaflet.css',
            buildDir + '/assets/css/client.css',
            buildDir + '/assets/css/**/*.css'
        ], {base : buildDir + '/'}),
        gconcat('bundle.css'),
        gulp.dest(bundleDir + '/assets/css/'),
        rename('bundle.min.css'),
        cleanCSS({
            compatibility: 'ie8',
            level: 1 //(0..nothing, 2..max)
        }),
        gulp.dest(bundleDir + '/assets/css/')
    ]);
});

/**
 * minify reflect - keep copyright but remove comments
 */
gulp.task('bundle:js:reflect', function (cb) {
    pump([
            gulp.src([
                buildDir + '/lib/reflect-metadata/Reflect.js'
            ]),
            uglify({
                output: {
                    comments: saveLicense,
                    webkit: true
                },
                compress: {
                    keep_fnames: false,
                    passes: 1,
                    typeofs: false,
                    unused: false
                },
                mangle: {
                    keep_fnames: false
                },
                ie8: true
            }),
            rename('Reflect.min.js'),
            gulp.dest(buildDir + '/lib/reflect-metadata/')
        ],
        cb
    );
});

gulp.task('bundle:js', gulp.series('bundle:js:reflect', (cb) => {
    var it = function () {
        pump([
                gulp.src([
                    bundleDir + '/assets/js/vendor.min.js',
                    buildDir + '/assets/js/stacktable.min.js'
                ]),
                gconcat('vendor.min.js'),
                gulp.dest(bundleDir + '/assets/js/')
            ],
            cb
        )
    };
    pump([
            gulp.src([
                buildDir + '/lib/core-js/client/shim.min.js',
                buildDir + '/lib/zone.js/dist/zone.min.js',
                buildDir + '/lib/reflect-metadata/Reflect.min.js',
                buildDir + '/lib/leaflet/dist/leaflet.js',
                buildDir + '/lib/d3/d3.min.js',
                buildDir + '/lib/nvd3/build/nv.d3.min.js',
                buildDir + '/assets/js/**/*.js',
                '!' + buildDir + '/assets/js/stacktable.*js'
            ]),
            // NOTE: vendor.js has no stacktable!!!!
            gconcat('vendor.js'),
            gulp.dest(bundleDir + "/assets/js/"),
            rename('vendor.min.js'),
            uglify({
                output: {
                    comments: 'all', // not - saveLicense,
                    webkit: true
                },
                compress: {
                    keep_fnames: false,
                    passes: 1,
                    typeofs: false,
                    unused: false
                },
                mangle: {
                    keep_fnames: false
                },
                ie8: true
            }),
            gulp.dest(bundleDir + "/assets/js/")
        ],
        it
    );
}));

gulp.task('bundle:i18n', function (cb) {
    pump([
            gulp.src([
                buildDir + '/i18n/**/*.json'
            ]),
            jsonminify(),
            gulp.dest(bundleDir + '/i18n/')
        ],
        cb
    );
});

gulp.task('bundle:app', function (done) {
    bundle("app/main.js", bundleDir + '/assets/js/bundle.app.js', done);
});

gulp.task('bundle:app:minify', gulp.series('bundle:app', (cb) => {
    pump([
            gulp.src(bundleDir + '/assets/js/bundle.app.js'),
            rename('bundle.app.min.js'),
            uglify({
                output: {
                    comments: saveLicense,
                    webkit: true
                },
                compress: {
                    keep_fnames: false,
                    passes: 1,
                    typeofs: false,
                    unused: false
                },
                mangle: {
                    keep_fnames: false
                },
                ie8: true
            }),
            gulp.dest(bundleDir + "/assets/js/")
        ],
        cb
    );
}));

function bundle(src, dst, opt) {
    const bundleOptions = Object.assign({
        baseUrl: buildDir,
        minify: false,
        sourceMaps: false,
        lowResSourceMaps: false // mapping granularity is per-line instead of per-character
    }, opt);

    const builder = new bundler(bundleOptions.baseUrl, buildDir + '/systemjs.config.js');
    return builder.buildStatic(src, dst, bundleOptions);
}

gulp.task('bundle', function (cb) {
    gulp.series(
        'clean:bundle',
        'copy:bundle',
        //['bundle:app:minify', 'bundle:css', 'bundle:js', 'bundle:i18n'],
        'bundle:app:minify', 
        'bundle:css', 
        'bundle:js', 
        'bundle:i18n',
        'inject:bundle',
        cb
    );
});


/**
 * Upload production
 */
gulp.task('upload:prod', function() {
    if (!clientDeploySettings) {
        throw new Error("No client deploy settings");
    }

    return gulp.src(buildDir + '/**')
        .pipe(rsync(Object.assign({
            root: buildDir + '/',
            archive: true,
            progress: true,
            compress: true,
            clean: true
        }, clientDeploySettings)));
});

/**
 * Upload bundle
 */
gulp.task('upload:bundle', function() {
    if (!clientDeploySettings) {
        throw new Error("No client deploy settings");
    }

    return gulp.src(bundleDir + '/')
        .pipe(rsync(Object.assign({
            root: bundleDir + '/',
            archive: true,
            progress: true,
            exclude: ['.*', '.*/'],
            compress: true,
            clean: true,
            recursive: true
        }, clientDeploySettings)));
});

gulp.task('upload', gulp.series('upload:prod'));

/**
 * Deploy to production - build->remote
 */
gulp.task('deploy:prod', function(cb) {
    gulp.series(
        'build:prod',
        'upload:prod',
        cb
    );
});


/**
 * Deploy to production - build->bundled->remote
 */
gulp.task('deploy:bundle', function(cb) {
    gulp.series(
        'build:bundle',
        'upload:bundle',
        cb
    );
});

gulp.task('deploy', gulp.series('deploy:bundle'));


function watch () {
    gulp.watch(["package.js"]).on('change', function (e) {
        console.log('Npm ' + e.path + ' was ' + e.type + '. Building..');
        gulp.series('inject', 'serve:reload');
    });
    gulp.watch([clientDir + '/' + client + '/settings.js']).on('change', function (e) {
        console.log('Settings ' + e.path + ' was ' + e.type + '. Copy..');
        gulp.series('build:part', 'serve:reload');
    });
    gulp.watch([clientDir + '/' + client + '/less/client.less', 'src/less/**/*.less']).on('change', function (e) {
        console.log('Less ' + e.path + ' was ' + e.type + '. Compiling..');
        gulp.series('compile:less', 'copy:assets', 'serve:reload');
    });
    gulp.watch([clientDir + '/' + client + '/adoc/**/*.adoc']).on('change', function (e) {
        console.log('Adoc ' + e.path + ' was ' + e.type + '. Compiling..');
        gulp.series('compile:adoc', 'serve:reload');
    });
    gulp.watch([clientDir + '/' + client + '/ts/**/*.html']).on('change', function (e) {
        console.log('Client template ' + e.path + ' was ' + e.type + '. Compiling..');
        gulp.series('build:part', 'serve:reload');
    });
    gulp.watch([srcDir + '/i18n/*.json', clientDir + '/' + client + '/i18n/*.json']).on('change', function (e) {
        console.log('i18n ' + e.path + ' was ' + e.type + '. Compiling..');
        gulp.series('copy:i18n', 'serve:reload');
    });
    gulp.watch([srcDir + "/systemjs.config.js"]).on('change', function (e) {
        console.log('System ' + e.path + ' was ' + e.type + '. Building..');
        gulp.series('build:dev', 'serve:reload');
    });
    gulp.watch([srcDir + "/app/**/*.ts", srcDir + "/app/**/*.js", clientDir + "/" + client + "/ts/**/*.ts"]).on('change', function (e) {
        console.log('TypeScript file ' + e.path + ' was ' + e.type + '. Compiling..');
        gulp.series('build:part', 'serve:reload');
    });
    gulp.watch([
        srcDir + "/**/*.html", srcDir + "/**/*.css", srcDir + "/**/*.js",
        clientDir + '/' + client + '/asstest/js/**/*.js',
        "!" + srcDir + "/systemjs.config.js", "!" + srcDir + "/app/**/*.js"
    ]).on('change', function (e) {
        console.log('Resource file ' + e.path + ' was ' + e.type + '. Updating..');
        gulp.series('inject', 'serve:reload');
    });
    gulp.watch([
        clientDir + '/' + client + '/loadingText.js'
    ]).on('change', function (e) {
        console.log('Resource file ' + e.path + ' was ' + e.type + '. Updating..');
        try {
            loadingText = require(clientDir + '/' + client + '/loadingText.js');
        } catch (e) {
            console.warn(clientDir + "/" + client + "/loadingText.js not found");
        }
        gulp.series('inject', 'serve:reload');
    });
    gulp.watch([
        clientDir + '/' + client + '/img/**/*'
    ]).on('change', function (e) {
        console.log('Client image file ' + e.path + ' was ' + e.type + '. Updating..');
        gulp.series('copy:client:img', 'serve:reload');
    });
    gulp.watch([
        clientDir + '/' + client + '/fonts/**/*'
    ]).on('change', function (e) {
        console.log('Client font file ' + e.path + ' was ' + e.type + '. Updating..');
        gulp.series('copy:client:fonts', 'serve:reload');
    });
    gulp.watch([
        clientDir + '/' + client + '/download/**/*'
    ]).on('change', function (e) {
        console.log('Client download file ' + e.path + ' was ' + e.type + '. Updating..');
        gulp.series('copy:client:download', 'serve:reload');
    });
}


gulp.task('watch', gulp.series('build:dev', (cb) => {
    serve();
    watch();
    cb();
}));


gulp.task('default', gulp.series('build:bundle'));
