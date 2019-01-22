/*******************************************************************************
 * Copyright 2013-2017 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

package at.alladin.nettest.shared.rmbt;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import com.google.common.io.BaseEncoding;
import com.google.common.net.InetAddresses;

/**
 * 
 * @author alladin-IT GmbH (?@alladin.at)
 *
 */
public abstract class Helperfunctions {
    
	/**
	 * 
	 * @param secret
	 * @param data
	 * @return
	 */
	public static String calculateHMAC(final String secret, final String data) {
        try {
            final SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
            final Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            final byte[] rawHmac = mac.doFinal(data.getBytes());
            final String result = BaseEncoding.base64().encode(rawHmac);
            return result;
        } catch (final GeneralSecurityException e) {
            System.out.println("Unexpected error while creating hash: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * 
     * @return
     */
    public static String getTimezoneId() {
        return TimeZone.getDefault().getID();
    }
    
    /**
     * 
     * @param id
     * @return
     * @deprecated is this still in use?
     */
    @Deprecated
    public static TimeZone getTimeZone(final String id) {
    	return (id == null) ? TimeZone.getDefault() : TimeZone.getTimeZone(id);
    }
    
    /**
     * 
     * @param timezoneId
     * @return
     * @deprecated is this still in use?
     */
    @Deprecated
    public static Calendar getTimeWithTimeZone(final String timezoneId) {
        final TimeZone timeZone = TimeZone.getTimeZone(timezoneId);
        final Calendar timeWithZone = Calendar.getInstance(timeZone);
        
        return timeWithZone;
    }

    /**
     * 
     * @param lang
     * @return
     * @deprecated is this still in use?
     */
    @Deprecated
    public static SimpleDateFormat getDateFormat(final String lang) {
        final SimpleDateFormat format;
        
        if ("de".equals(lang)) {
            format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
            
        return format;
    }
    
    /**
     * 
     * @param geoLat
     * @param geoLong
     * @return
     */
    public static String geoToString(final Double geoLat, final Double geoLong) {
        if (geoLat == null || geoLong == null) {
            return null;
        }
        
        int latd, lond; // latitude degrees and minutes, longitude degrees and
                        // minutes
        double latm, lonm; // latitude and longitude seconds.
        
        // decimal degrees to degrees minutes seconds
        
        double temp;
        // latitude
        temp = Math.abs(geoLat);
        latd = (int) temp;
        latm = (temp - latd) * 60.0;
        if (geoLat < 0) {
            latd = -latd;
        }
        
        // longitude
        temp = Math.abs(geoLong);
        lond = (int) temp;
        lonm = (temp - lond) * 60.0;
        if (geoLong < 0) {
            lond = -lond;
        }
        
        final String dirLat;
        if (geoLat >= 0) {
            dirLat = "N";
        } else {
            dirLat = "S";
        }
        
        final String dirLon;
        if (geoLong >= 0) {
            dirLon = "E";
        } else {
            dirLon = "W";
        }
        
        return String.format("%s %02d°%02.3f'  %s %02d°%02.3f'", dirLat, latd, latm, dirLon, lond, lonm);
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public static String getNetworkTypeName(final int type) {
        // TODO: read from DB
        switch (type) {
        case 1:
            return "2G (GSM)";
        case 2:
            return "2G (EDGE)";
        case 3:
            return "3G (UMTS)";
        case 4:
            return "2G (CDMA)";
        case 5:
            return "2G (EVDO_0)";
        case 6:
            return "2G (EVDO_A)";
        case 7:
            return "2G (1xRTT)";
        case 8:
            return "3G (HSDPA)";
        case 9:
            return "3G (HSUPA)";
        case 10:
            return "3G (HSPA)";
        case 11:
            return "2G (IDEN)";
        case 12:
            return "2G (EVDO_B)";
        case 13:
            return "4G (LTE)";
        case 14:
            return "2G (EHRPD)";
        case 15:
            return "3G (HSPA+)";
        case 97:
            return "CLI";
        case 98:
            return "BROWSER";
        case 99:
            return "WLAN";
        case 101:
            return "2G/3G";
        case 102:
            return "3G/4G";
        case 103:
            return "2G/4G";
        case 104:
            return "2G/3G/4G";
        case 105:
            return "MOBILE";
        case 106:
            return "Ethernet";
        case 107:
            return "Bluetooth";
        default:
            return "UNKNOWN";
        }
    }
    
    public static String getRoamingTypeKey (final int roamingType) {
    	switch (roamingType) {
        case 0:
            return "value_roaming_none";
        case 1:
            return "value_roaming_national";
        case 2:
            return "value_roaming_international";
        default:
            return "?";
        }
    }
    
    /**
     * 
     * @param labels
     * @param roamingType
     * @return
     */
    public static String getRoamingType(final ResourceBundle labels, final int roamingType) {
        if (labels == null) {
            return "?";
        }
        final String roamingValue = getRoamingTypeKey(roamingType);
        return roamingValue.equals("?") ? roamingValue : labels.getString(roamingValue);
    }
    
    /**
     * 
     * @param adr
     * @return
     */
    public static boolean isIPLocal(final InetAddress adr) {
        return adr.isLinkLocalAddress() || adr.isLoopbackAddress() || adr.isSiteLocalAddress();
    }

    /**
     * 
     * @param inetAddress
     * @return
     */
    public static String IpType(InetAddress inetAddress) {
        if (inetAddress == null) {
            return null;
        }
        try {
            final String ipVersion;
            if (inetAddress instanceof Inet4Address) {
                ipVersion = "ipv4";
            } else if (inetAddress instanceof Inet6Address) {
                ipVersion = "ipv6";
            } else {
                ipVersion = "ipv?";
            }

            if (inetAddress.isAnyLocalAddress()) {
                return "wildcard_" + ipVersion;
            }
            
            if (inetAddress.isSiteLocalAddress()) {
                return "site_local_" + ipVersion;
            }
            
            if (inetAddress.isLinkLocalAddress()) {
                return "link_local_" + ipVersion;
            }
            
            if (inetAddress.isLoopbackAddress()) {
                return "loopback_" + ipVersion;
            }
                
            return "public_" + ipVersion;
        } catch (final IllegalArgumentException e) {
            return "illegal_ip";
        }
    }
    
    /**
     * 
     * @param inetAddress
     * @return
     */
    public static String anonymizeIp(final InetAddress inetAddress) {
        try {
            final byte[] address = inetAddress.getAddress();
            address[address.length - 1] = 0;
            
            if (address.length > 4) { // ipv6
                for (int i = 6; i < address.length; i++) {
                    address[i] = 0;
                }
            }
            
            String result = InetAddresses.toAddrString(InetAddress.getByAddress(address));
            if (address.length == 4) {
                result = result.replaceFirst(".0$", "");
            }
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 
     * @param localAdr
     * @param publicAdr
     * @return
     */
    public static String getNatType(final InetAddress localAdr, final InetAddress publicAdr) {
        if (localAdr == null || publicAdr == null) {
            return "illegal_ip";
        }
        try {
            final String ipVersion;
            if (publicAdr instanceof Inet4Address) {
                ipVersion = "ipv4";
            } else if (publicAdr instanceof Inet6Address) {
                ipVersion = "ipv6";
            } else {
                ipVersion = "ipv?";
            }
            
            if (localAdr.equals(publicAdr)) {
                return "no_nat_" + ipVersion;
            } else {
                final String localType = isIPLocal(localAdr) ? "local" : "public";
                final String publicType = isIPLocal(publicAdr) ? "local" : "public";
                return String.format("nat_%s_to_%s_%s", localType, publicType, ipVersion);
            }
        } catch (final IllegalArgumentException e) {
            return "illegal_ip";
        }
    }
    
    /**
     * 
     * @param adr
     * @return
     */
    public static String reverseDNSLookup(final InetAddress adr) {
        try {
        	System.out.println("reverse dns lookup: " + adr); // TODO: logger
            final Name name = ReverseMap.fromAddress(adr);
            
            final Lookup lookup = new Lookup(name, Type.PTR);
            lookup.setResolver(new SimpleResolver());
            lookup.setCache(null);
            final Record[] records = lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL) {
                for (final Record record : records) {
                    if (record instanceof PTRRecord) {
                        final PTRRecord ptr = (PTRRecord) record;
                        return ptr.getTarget().toString();
                    }
                }
            }
        } catch (final Exception e) {
        	e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 
     * @param adr
     * @param postfix
     * @return
     */
    public static Name getReverseIPName(final InetAddress adr, final Name postfix) {
        final byte[] addr = adr.getAddress();
        final StringBuilder sb = new StringBuilder();
        if (addr.length == 4) {
            for (int i = addr.length - 1; i >= 0; i--) {
                sb.append(addr[i] & 0xFF);
                if (i > 0) {
                    sb.append(".");
                }
            }
        } else {
            final int[] nibbles = new int[2];
            for (int i = addr.length - 1; i >= 0; i--) {
                nibbles[0] = (addr[i] & 0xFF) >> 4;
                nibbles[1] = addr[i] & 0xFF & 0xF;
                for (int j = nibbles.length - 1; j >= 0; j--) {
                    sb.append(Integer.toHexString(nibbles[j]));
                    if (i > 0 || j > 0) {
                        sb.append(".");
                    }
                }
            }
        }
        
        try {
            return Name.fromString(sb.toString(), postfix);
        } catch (final TextParseException e) {
            throw new IllegalStateException("name cannot be invalid");
        }
    }
    
    /**
     * 
     * @param adr
     * @return
     */
    public static Long getASN(final InetAddress adr) {
        try {
            final Name postfix;
            if (adr instanceof Inet6Address) {
                postfix = Name.fromConstantString("origin6.asn.cymru.com");
            } else {
                postfix = Name.fromConstantString("origin.asn.cymru.com");
            }
            
            final Name name = getReverseIPName(adr, postfix);
            //System.out.println("lookup: " + name); // TODO: logger
            
            final Lookup lookup = new Lookup(name, Type.TXT);
            lookup.setResolver(new SimpleResolver());
            lookup.setCache(null);
            final Record[] records = lookup.run();

            if (lookup.getResult() == Lookup.SUCCESSFUL) {
                for (final Record record : records) {
                    if (record instanceof TXTRecord) {
                        final TXTRecord txt = (TXTRecord) record;
                        @SuppressWarnings("unchecked")
                        final List<String> strings = txt.getStrings();
                        if (strings != null && !strings.isEmpty()) {
                            final String result = strings.get(0);
                            final String[] parts = result.split(" ?\\| ?");
                            if (parts != null && parts.length >= 1) {
                            	//fix: some rDNS requests return more than one ASN (separated by a space), just take the first one
                            	final String[] asnNumbers = parts[0].split(" ");
                            	return new Long(asnNumbers[0]);
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
        	
        }

        return null;
    }
    
    /**
     * 
     * @param asn
     * @return
     */
    public static String getASName(final long asn) {
        try {
            final Name postfix = Name.fromConstantString("asn.cymru.com.");
            final Name name = new Name(String.format("AS%d", asn), postfix);
            System.out.println("lookup: " + name); // TODO: logger
            
            final Lookup lookup = new Lookup(name, Type.TXT);
            lookup.setResolver(new SimpleResolver());
            lookup.setCache(null);
            final Record[] records = lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL) {
                for (final Record record : records) {
                    if (record instanceof TXTRecord) {
                        final TXTRecord txt = (TXTRecord) record;
                        @SuppressWarnings("unchecked")
                        final List<String> strings = txt.getStrings();
                        if (strings != null && !strings.isEmpty()) {
                            System.out.println(strings); // TODO: logger
                            
                            final String result = strings.get(0);
                            final String[] parts = result.split(" ?\\| ?");
                            if (parts != null && parts.length >= 1) { // TODO: why not check for length >= 4 since we access parts[4] ??
                                return parts[4]; 
                                // TODO: java.lang.ArrayIndexOutOfBoundsException: 4 at at.alladin.rmbt.shared.Helperfunctions.getASName(Helperfunctions.java:519)
                                // lookup: AS21078.asn.cymru.com.
                                // [21078 |  | ripencc |  |]
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
        	e.printStackTrace();
        }

        return null;
    }

    /**
     * 
     * @param asn
     * @return
     */
    public static String getAScountry(final long asn) {
        try {
            final Name postfix = Name.fromConstantString("asn.cymru.com.");
            final Name name = new Name(String.format("AS%d", asn), postfix);
            System.out.println("lookup: " + name); // TODO: logger
            
            final Lookup lookup = new Lookup(name, Type.TXT);
            lookup.setResolver(new SimpleResolver());
            lookup.setCache(null);
            final Record[] records = lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL) {
                for (final Record record : records) {
                    if (record instanceof TXTRecord) {
                        final TXTRecord txt = (TXTRecord) record;
                        @SuppressWarnings("unchecked")
                        final List<String> strings = txt.getStrings();
                        if (strings != null && !strings.isEmpty()) {
                            final String result = strings.get(0);
                            final String[] parts = result.split(" ?\\| ?");
                            if (parts != null && parts.length >= 1) {
                                return parts[1];
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
        	e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 
     * @param glue
     * @param array
     * @return
     */
    public static <T extends Object> String join(String glue, T[] array) {
    	final StringBuilder sb = new StringBuilder("");
    	
    	int len = array.length;
    	
    	if (len < 1) {
    		return null;
    	}
    	
    	for (int i = 0; i < (len-1); i++) {
    		sb.append(String.valueOf(array[i]));
    		sb.append(glue);
    	}
    	
    	sb.append(String.valueOf(array[len-1]));
    	
    	return sb.toString();
    }

    /**
     * 
     * @param json
     * @return
     */
	public static String json2htmlWithLinks(JSONObject json) {
    	final StringBuilder result = new StringBuilder();
    	
    	final Iterator<String> jsonIterator = json.keys();
    	
    	try {
        	while (jsonIterator.hasNext()) {
        		String key = jsonIterator.next();

       			if (json.opt(key) instanceof JSONObject) {
       				result.append("\"" + key + "\" => \"" + json2hstore(json.optJSONObject(key), null) + "\"");
       			} else {
       				String keyValue = json.getString(key).replaceAll("\"","\\\\\"").replaceAll("'","\\\\'");
       				if ("on_success".equals(key) || "on_failure".equals(key)) {
       				    String link = "<a href=\"#" + keyValue.replaceAll("[\\-\\+\\.\\^:,]", "_") + "\">" + keyValue + "</a>";
       					result.append("\"" + key + "\" => \"" + link + "\"");
       				} else {
       					result.append("\"" + key + "\" => \"" + keyValue + "\"");
       				}
       			}
       			
       			if (jsonIterator.hasNext()) {
       				result.append(", ");
       			}
        	}    		
    	} catch (JSONException e) {
    		return null;
    	}
    	
    	return result.toString();
    }
    
    /**
     * 
     * @param json
     * @param excludeKeys
     * @return
     */
	public static String json2hstore(JSONObject json, Set<String> excludeKeys) {
		final StringBuilder result = new StringBuilder();
    	
    	final Iterator<String> jsonIterator = json.keys();
    	
    	try {
        	boolean isFirst = true;
        	while (jsonIterator.hasNext()) {
        		final String key = jsonIterator.next();
        
        		if (excludeKeys == null || !excludeKeys.contains(key)) {
        			if (!isFirst) {
            			if (json.opt(key) instanceof JSONObject) {
            				result.append(", \"" + key + "\" => \"" + json2hstore(json.optJSONObject(key), excludeKeys) + "\"");
            			}
            			else {
            				Object data = json.get(key);
            				if (data != null) {
            					if (data instanceof String) {
            						data = "\"" + ((String) data).replaceAll("\"","\\\\\"").replaceAll("'","\\\\'") + "\"";
            					}
            					else if (data instanceof JSONArray) {
            						data = "\"" + ((JSONArray) data).toString().replaceAll("\"","\\\\\"").replaceAll("'","\\\\'") + "\"";
            					}
            				}
            				result.append(", \"" + key + "\" => " + data);
            				//result.append(", \"" + key + "\" => \"" + json.getString(key).replaceAll("\"","\\\\\"").replaceAll("'","\\\\'") + "\"");	
            			}
        			} else {
        				isFirst = false;
            			if (json.opt(key) instanceof JSONObject) {
            				result.append("\"" + key + "\" => \"" + json2hstore(json.optJSONObject(key), excludeKeys) + "\"");
            			} else {
            				Object data = json.get(key);
            				if (data != null) {
            					if (data instanceof String) {
            						data = "\"" + ((String) data).replaceAll("\"","\\\\\"").replaceAll("'","\\\\'") + "\"";
            					}
            					else if (data instanceof JSONArray) {
            						data = "\"" + ((JSONArray) data).toString().replaceAll("\"","\\\\\"").replaceAll("'","\\\\'") + "\"";
            					}
            				}
            				result.append("\"" + key + "\" => " + data);
            				//result.append("\"" + key + "\" => \"" + json.getString(key).replaceAll("\"","\\\\\"").replaceAll("'","\\\\'") + "\"");
            			}
        			}
        		}
        	}    		
    	} catch (JSONException e) {
    		return null;
    	}
    	
    	return result.toString();
    }
}
