Pod::Spec.new do |s|
    s.name = 'MeasurementAgentKit'
    s.version = '0.0.1'
    s.license = 'Apache 2.0'
    s.summary = 'LMAP measurement agent library written in swift'
    s.description = 'LMAP measurement agent library written in swift'
    s.homepage = 'https://net-neutrality.tools'
    s.authors = { 'alladin-IT GmbH' => 'office@alladin.at' }
    s.source = { :git => 'https://github.com/net-neutrality-tools/nntool', :tag => s.version }
    s.documentation_url = 'https://net-neutrality.tools'

    s.ios.deployment_target = '10.0'

    s.swift_version = '5.0'

    s.source_files = 'MeasurementAgentKit/Sources/**/*.swift'

    s.dependency 'Siesta', '~> 1.4.3'
    s.dependency 'ReachabilitySwift', '~> 5.0.0'
    s.dependency 'CocoaAsyncSocket', '~> 7.6.3'
    s.dependency 'nntool-shared-swift', '~> 0.0.1'

    s.frameworks = 'CoreLocation', 'SystemConfiguration'
end
