package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MccMnc;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Provider;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ProviderAsnMapping;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ProviderMccMncMapping;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.ProviderRepository;

/**
 * 
 * @author lb@alladin.at
 *
 */
@Service
public class ProviderService {
	
	private final Logger logger = LoggerFactory.getLogger(ProviderService.class);
	
	@Autowired
	private ProviderRepository providerRepository;

	/**
	 * 
	 * @param asn
	 * @param reverseDns
	 * @return
	 */
	public Provider getByAsn(final long asn, final String reverseDns) {
		logger.debug("provider lookup for ASN: {} and reverseDns", asn, reverseDns);
		
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
	
	/**
	 * 
	 * @param mccMncSim
	 * @param mccMncNetwork
	 * @param date
	 * @return
	 */
	public Provider getByMccMnc(MccMnc mccMncSim, MccMnc mccMncNetwork, LocalDateTime date) {
    if (mccMncSim == null) {
      logger.debug("getByMccMnc -> mccMncSim is null, returning null.");
      return null;
    }

		logger.debug("provider lookup for sim mcc-mnc: {} and network mcc-mnc: {}", mccMncSim, mccMncNetwork);
		
		final List<Provider> providers = providerRepository.findBySimMccMnc(mccMncSim.getMcc(), mccMncSim.getMnc());
		
		if (providers.isEmpty()) {
			return null;
		}
		
		for (final Provider provider : providers) {
			final List<ProviderMccMncMapping> mappings = provider.getMccMncMappings();

			if (mappings != null) {
				for (final ProviderMccMncMapping mapping : mappings) {
					if (!mccMncSim.equals(mapping.getSimMccMnc())) {
						continue;
					}

					final LocalDateTime validFrom = mapping.getConditionValidFrom();
					if (validFrom != null && date.isBefore(validFrom)) {
						continue;
					}

					final LocalDateTime validTo = mapping.getConditionValidTo();
					if (validTo != null && date.isAfter(validTo)) {
						continue;
					}
					
					final MccMnc mappingNetworkMccMnc = mapping.getNetworkMccMnc();
					if (mappingNetworkMccMnc == null || (mccMncNetwork != null && mccMncNetwork.equals(mappingNetworkMccMnc))) {
						return provider;
					}
				}
			}
		}

		return null;
	}
}
