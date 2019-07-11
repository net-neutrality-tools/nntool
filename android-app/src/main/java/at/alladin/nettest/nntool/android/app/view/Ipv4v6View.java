package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ip.IpResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class Ipv4v6View extends RelativeLayout {

    private final static String TAG = Ipv4v6View.class.getSimpleName();

    TextView ipv4Text;
    TextView ipv6Text;

    public Ipv4v6View(Context context) {
        super(context);
        init();
    }

    public Ipv4v6View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Ipv4v6View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void updateIpStatus(final Map<IpResponse.IpVersion, IpResponse> ipVersionToResponseMap) {
        final Map<IpResponse.IpVersion, String> localAddresses = getLocalIpAddresses();
        for (IpResponse.IpVersion ip : IpResponse.IpVersion.values()) {
            TextView toSet = null;
            switch (ip) {
                case IPv4:
                    toSet = ipv4Text;
                    break;
                case IPv6:
                    toSet = ipv6Text;
                    break;
            }
            if (toSet == null) {
                continue;
            }
            if (ipVersionToResponseMap != null && ipVersionToResponseMap.containsKey(ip)) {
                if (localAddresses.containsKey(ip) && localAddresses.get(ip).equals(ipVersionToResponseMap.get(ip).getIpAddress())) {
                    toSet.setText(R.string.ifont_check);
                    toSet.setTextColor(getResources().getColor(R.color.ip_address_correct));
                } else {
                    toSet.setText(R.string.ifont_check);
                    toSet.setTextColor(getResources().getColor(R.color.ip_address_behind_nat));
                }

            } else {
                toSet.setText(R.string.ifont_close);
                toSet.setTextColor(getResources().getColor(R.color.ip_address_unavailable));
            }
        }
    }

    private void init() {
        inflate(getContext(), R.layout.ipv4v6_view, this);
        ipv4Text = findViewById(R.id.ipv4_status_text);
        ipv6Text = findViewById(R.id.ipv6_status_text);
    }

    //Basic implementation idea from https://stackoverflow.com/questions/40770555/getting-ipv4-and-ipv6-programatically-of-android-device-with-non-deprecated-meth#answer-47009976
    private Map<IpResponse.IpVersion, String> getLocalIpAddresses() {
        final Map<IpResponse.IpVersion, String> ipVersionToAddressMap = new HashMap<>(IpResponse.IpVersion.values().length);
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                final NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress()) {
                        if (!ipVersionToAddressMap.containsKey(IpResponse.IpVersion.IPv4) && inetAddress instanceof Inet4Address) {
                            ipVersionToAddressMap.put(IpResponse.IpVersion.IPv4, inetAddress.getHostAddress());
                        } else if (!ipVersionToAddressMap.containsKey(IpResponse.IpVersion.IPv6) && inetAddress instanceof Inet6Address) {
                            String ipaddress = inetAddress.getHostAddress();
                            final int index = ipaddress.indexOf('%');
                            if (index != -1 ) {
                                ipVersionToAddressMap.put(IpResponse.IpVersion.IPv6, ipaddress.substring(0, index));
                            } else {
                                ipVersionToAddressMap.put(IpResponse.IpVersion.IPv6, ipaddress);
                            }
                        }
                        if (ipVersionToAddressMap.size() == IpResponse.IpVersion.values().length) {
                            return ipVersionToAddressMap;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ipVersionToAddressMap;
    }
}
