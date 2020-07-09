Pod::Spec.new do |s|
    s.name = 'nntool-shared-swift'
    s.version = '0.0.1'
    s.license = 'Apache 2.0'
    s.summary = 'Shared Swift code for nntool project.'
    s.description = 'Shared Swift code for nntool project.'
    s.homepage = 'https://net-neutrality.tools'
    s.authors = { 'alladin-IT GmbH' => 'office@alladin.at' }
    s.source = { :git => 'https://github.com/net-neutrality-tools/nntool', :tag => s.version }
    s.documentation_url = 'https://net-neutrality.tools'

    s.ios.deployment_target = '10.0'

    s.swift_version = '5.0'

    s.source_files = 'nntool-shared-swift/Sources/**/*.swift'

    s.dependency 'SwiftLint', '~> 0.39.1'
    s.dependency 'XCGLogger', '~> 7.0.1'
    s.dependency 'CodableJSON', '~> 1.2.0'
    s.dependency 'Repeat', '~> 0.5.8'

    #s.dependency 'Nimble', '~> 8.0.5'
end
