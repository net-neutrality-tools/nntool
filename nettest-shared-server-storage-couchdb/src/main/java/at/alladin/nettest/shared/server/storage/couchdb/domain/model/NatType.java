/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.net.InetAddress;

import at.alladin.nettest.shared.nntool.Helperfunctions;

/**
 * 
 * @author lb
 *
 */
public enum NatType {
	NOT_AVAILABLE,
	NO_NAT,
	LOCAL_TO_LOCAL,
	LOCAL_TO_PUBLIC,
	PUBLIC_TO_PUBLIC,
	PUBLIC_TO_LOCAL;
	
	/**
	 * 
	 * @param localIp
	 * @param publicIp
	 * @return
	 */
	private static NatType fromAddresses(final InetAddress localIp, final InetAddress publicIp) {
        if (localIp.equals(publicIp)) {
            return NO_NAT;
        } 
        else {
            final boolean isLocalLocal = Helperfunctions.isIPLocal(localIp);
            final boolean isPublicLocal = Helperfunctions.isIPLocal(publicIp);
            
            if (isLocalLocal) {
            	return isPublicLocal ? LOCAL_TO_LOCAL : LOCAL_TO_PUBLIC;
            }
            
            return isPublicLocal ? PUBLIC_TO_LOCAL : PUBLIC_TO_PUBLIC;
        }
	}

	/**
	 * 
	 * @param localAdr
	 * @param publicAdr
	 * @return
	 */
    public static NatType getNatType(final String localAdr, final String publicAdr) {
        if (localAdr == null || publicAdr == null) {
            return NatType.NOT_AVAILABLE;
        }
        try {
        	return NatType.getNatType(InetAddress.getByName(localAdr), InetAddress.getByName(publicAdr));
        } catch (final Exception e) {
            return NatType.NOT_AVAILABLE;
        }
    }

    /**
     * 
     * @param localAdr
     * @param publicAdr
     * @return
     */
    public static NatType getNatType(final InetAddress localAdr, final InetAddress publicAdr) {
        if (localAdr == null || publicAdr == null) {
            return NatType.NOT_AVAILABLE;
        }
        try {
        	return NatType.fromAddresses(localAdr, publicAdr);
        } catch (final Exception e) {
            return NatType.NOT_AVAILABLE;
        }
    }
}
