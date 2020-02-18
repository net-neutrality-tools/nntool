Pod::Spec.new do |s|
    s.name             = 'qos-client-cpp'
    s.version          = '0.0.1'
    s.summary          = 'qos-client-cpp for iOS'

    s.description      = 'qos-client-cpp for iOS'

    s.homepage         = 'https://net-neutrality.tools'
    s.license          = { :type => 'Apache', :file => 'LICENSE' }
    s.authors          = 'alladin-IT GmbH'

    s.source           = {
      :git => 'https://github.com/net-neutrality-tools/nntool',
      #:tag => ''
    }

    s.ios.deployment_target = '10.0'

    s.cocoapods_version = '>= 1.4.0'
    s.static_framework = false
    s.prefix_header_file = false

    s.source_files = [
      'src/*.{h,m,mm,cpp}'
    ]
    s.public_header_files = [
      'src/AudioStreamingWrapper.h'
    ]
    s.exclude_files = [
      # Exclude alternate implementations for other platforms
      '*_test.cpp',
      'src/JniConnector*'
    ]

    s.ios.frameworks = 'GStreamer', 'AVFoundation', 'CoreMedia', 'AssetsLibrary'

    s.library = 'c++', 'iconv', 'resolv'
    s.pod_target_xcconfig = {
        'CLANG_CXX_LANGUAGE_STANDARD' => 'c++14',
        'HEADER_SEARCH_PATHS' => '"$(inherited)" "$(HOME)/Library/Developer/GStreamer/iPhone.sdk/GStreamer.framework/Headers"',
        'FRAMEWORK_SEARCH_PATHS' => '"$(HOME)/Library/Developer/GStreamer/iPhone.sdk"'
    }

    s.xcconfig = {
        'HEADER_SEARCH_PATHS' => '"$(inherited)" "$(HOME)/Library/Developer/GStreamer/iPhone.sdk/GStreamer.framework/Headers"',
        'FRAMEWORK_SEARCH_PATHS' => '"$(HOME)/Library/Developer/GStreamer/iPhone.sdk"'
    }

    s.compiler_flags = '$(inherited) -Wreorder -Werror=reorder'
end

