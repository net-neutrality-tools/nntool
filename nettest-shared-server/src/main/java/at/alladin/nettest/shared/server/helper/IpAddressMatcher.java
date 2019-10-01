/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.alladin.nettest.shared.server.helper;


import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Matches a request based on IP Address or subnet mask matching against the remote
 * address.
 * <p>
 * Both IPv6 and IPv4 addresses are supported, but a matcher which is configured with an
 * IPv4 address will never match a request which returns an IPv6 address, and vice-versa.
 * </p>
 * <p>
 * (source: <a href='https://github.com/spring-projects/spring-security/blob/master/web/src/main/java/org/springframework/security/web/util/matcher/IpAddressMatcher.java'>
 * https://github.com/spring-projects/spring-security/blob/master/web/src/main/java/org/springframework/security/web/util/matcher/IpAddressMatcher.java
 * </a>
 * </p>
 * <p>
 * modified by Lukasz Budryk
 * </p>
 *
 * @author Luke Taylor
 * @since 3.0.2
 */
public final class IpAddressMatcher {
	private final int nMaskBits;
	private final InetAddress requiredAddress;

	/**
	 * Takes a specific IP address or a range specified using the IP/Netmask (e.g.
	 * 192.168.1.0/24 or 202.24.0.0/14).
	 *
	 * @param ipAddress the address or range of addresses from which the request must
	 * come.
	 */
	public IpAddressMatcher(String ipAddress) {

		if (ipAddress.indexOf('/') > 0) {
			String[] addressAndMask = StringUtils.split(ipAddress, "/");
			ipAddress = addressAndMask[0];
			nMaskBits = Integer.parseInt(addressAndMask[1]);
		}
		else {
			nMaskBits = -1;
		}
		requiredAddress = parseAddress(ipAddress);
		Assert.isTrue(requiredAddress.getAddress().length * 8 >= nMaskBits,
				String.format("IP address %s is too short for bitmask of length %d",
						ipAddress, nMaskBits));
	}

	public boolean matches(String address) {
		InetAddress remoteAddress = parseAddress(address);

		if (remoteAddress == null) {
			//unable to parse address
			return false;
		}
		
		if (!requiredAddress.getClass().equals(remoteAddress.getClass())) {
			return false;
		}

		if (nMaskBits < 0) {
			return remoteAddress.equals(requiredAddress);
		}

		byte[] remAddr = remoteAddress.getAddress();
		byte[] reqAddr = requiredAddress.getAddress();

		int nMaskFullBytes = nMaskBits / 8;
		byte finalByte = (byte) (0xFF00 >> (nMaskBits & 0x07));

		for (int i = 0; i < nMaskFullBytes; i++) {
			if (remAddr[i] != reqAddr[i]) {
				return false;
			}
		}

		if (finalByte != 0) {
			return (remAddr[nMaskFullBytes] & finalByte) == (reqAddr[nMaskFullBytes] & finalByte);
		}

		return true;
	}

	private InetAddress parseAddress(String address) {
		try {
			return InetAddress.getByName(address);
		}
		catch (UnknownHostException e) {
			return null;
		}
	}
}