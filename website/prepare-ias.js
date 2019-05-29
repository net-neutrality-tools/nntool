const iasDir = '../ias';
const packageDirWithinIas = './build/uglified/web';
const buildScript = './build.sh';
const packageJSON = './ias-package.json'; // TODO: Consider inlining file


const gulp = require('gulp');
const spawnSync = require('child_process').spawnSync;
const rename = require('gulp-rename');


gulp.task('default', function (cb) {
    var buildResult = spawnSync(buildScript, {
        stdio: 'inherit',
        cwd: process.cwd() + '/' + iasDir
    });

    if (buildResult.status === 0) {
        console.log('Build finished');
        return gulp
            .src(packageJSON)
            .pipe(rename('package.json'))
            .pipe(gulp.dest(process.cwd() + '/' + iasDir + '/' + packageDirWithinIas + '/'));
    } else {
        console.log('Build failed');
        cb();
    }
});
