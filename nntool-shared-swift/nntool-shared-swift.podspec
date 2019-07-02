Pod::Spec.new do |s|
    s.name = 'nntool-shared-swift'
    s.version = '0.0.1'
    s.license = 'TODO: Apache v2'
    s.summary = 'Shared Swift code for nntool project.'
    s.description = 'Shared Swift code for nntool project.'
    s.homepage = 'https://alladin.at'
    s.authors = { 'alladin-IT GmbH' => 'office@alladin.at' }
    s.source = { :git => 'https://github.com/alladin-it/...', :tag => s.version }
    s.documentation_url = 'https://github.com/alladin-it/...'

    s.ios.deployment_target = '10.0'

    s.swift_version = '5.0'

    s.source_files = 'nntool-shared-swift/Sources/**/*.swift'

    s.dependency 'SwiftLint', '~> 0.31.0'
    s.dependency 'XCGLogger', '~> 7.0.0'
    s.dependency 'CodableJSON', '~> 1.1.4'
    s.dependency 'Repeat', '~> 0.5.7'

    #s.dependency 'Nimble', '~> 8.0.1'
end
