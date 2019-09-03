package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Provider;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ProviderAsnMapping;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.ProviderRepository;

/**
 * 
 * @author lb@alladin.at
 *
 */
@Service
public class ProviderService {

	@Autowired
	private ProviderRepository providerRepository;

	public Provider getByAsn(final long asn, final String reverseDns) {
		final List<Provider> providers = providerRepository.findByAsn(asn);
	
		if (providers == null || providers.isEmpty()) {
			return null;
		}
		
		final String reverseDnsLc = reverseDns == null ? null : reverseDns.toLowerCase(Locale.US);
		for (final Provider provider : providers) {
			final List<ProviderAsnMapping> mappings = provider.getAsnMappings();
			if (mappings != null) {
				for (final ProviderAsnMapping mapping : mappings) {
					if (mapping.getAsn() != asn) {
						continue;
					}
					
					final String suffix = mapping.getConditionRdnsSuffix();
					
					if (suffix == null) {
						return provider;
					}
					
					if (reverseDnsLc != null && reverseDnsLc.endsWith(suffix.toLowerCase(Locale.US))) {
						return provider;
					}
				}
			}
		}
		
		return null;
	}
}
