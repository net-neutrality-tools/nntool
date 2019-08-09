## Building ##

### Prerequisites ###
Node.js >= 10
npm >= 6
uglify-es >= 3
Linux or macOS (any Version, required for uglify)

### Build ###
1. Build *ias*, see instructions in *ias/README.md*
2. In dir *ias-desktop*, run *npm i* to install dependencies
3. Put Public keys of TLS-certificates which should be connected to in *certificates/*

There are two build options available:
1. For uglifying, run *npm run build* on Linux or macOS with fullfilled prerequisites. The output in *build/* will be usable in other electron projects.
2. For creating an executable, run *npm run dist:{mac|linux|win}* with fullfilled prerequisites. The executable will be build in os-specific formats (see *package.json*) and placed in *dist/*. Currently, there is *no* cross-compiling available, i.e., every :dist has to be build on the target OS.

---------------

## Execution ##
There are three execution options available:
1. Run *npm run electron:{mac|linux|win}* with fullfilled prerequisites without building. The debug UI will be shown.
2. After building, run the build executable. The default UI will be shown.
3. After building, run the build executable from command-line with the argument *--cli*. The default measurement configuration will be used. there are several cli options available:
	- *--debug* debug UI will be shown
	- *--verbose* verbose measurement output on cli
	- *--rtt={true|false}* perform rtt measurement
	- *--download={true|false}* perform download measurement
	- *--upload={true|false}* perform upload measurement

To perform rtt, download, or upload measurements, the *ias-server* module has to be deployed on the measurement peer.
