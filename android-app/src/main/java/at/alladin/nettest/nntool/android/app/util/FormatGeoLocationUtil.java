/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
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

package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;

import java.util.Locale;

import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class FormatGeoLocationUtil {

    /**
     * formats geo location latitude to:  <strong>N/S XX°XXX'</strong>
     * @param ctx
     * @param latitude
     * @return
     */
    public static String formatGeoLat(final Context ctx, final double latitude) {
        return convertLocation(ctx.getResources(), latitude, true);
    }

    /**
     * formats geo location longitude to:  <strong>E/W XX°XXX'</strong>
     * @param ctx
     * @param longitude
     * @return
     */
    public static String formatGeoLong(final Context ctx, final double longitude) {
        return convertLocation(ctx.getResources(), longitude, false);
    }

    private static String convertLocation(final Resources res, final double coordinate, final boolean isLatitude) {
        final String rawStr = Location.convert(coordinate, Location.FORMAT_MINUTES);
        //[+-]DDD:MM.MMMMM - FORMAT_MINUTES
        final String[] split = rawStr.split(":");
        final String direction;
        float min = 0f;

        try  {
            split[1] = split[1].replace(",",".");
            min = Float.parseFloat(split[1]);
        }
        catch (NumberFormatException e) {
            // ignore
        }

        if (isLatitude) {
            if (coordinate >= 0) {
                direction = res.getString(R.string.geo_location_dir_n);
            }
            else {
                direction = res.getString(R.string.geo_location_dir_s);
            }
        }
        else if (coordinate >= 0) {
            direction = res.getString(R.string.geo_location_dir_e);
        }
        else {
            direction = res.getString(R.string.geo_location_dir_w);
        }

        return String.format(Locale.US, "%s %s°%.3f'", direction, split[0].replace("-", ""), min);
    }

    public static String formatGeoAccuracy(final Context context, final Double accuracy, final Integer satellites) {
        if ((context == null) || (accuracy == null) || (accuracy < 0)) {
            // ignore negative or null
            return "";
        }
        else if (satellites != null && satellites > 0) {
                return String.format(Locale.US, "±%.0f %s (%d %s)", accuracy, context.getResources().getString(R.string.geo_location_m), satellites, context.getResources().getString(R.string.geo_location_sat));
        }
        else {
            return String.format(Locale.US, "±%.0f %s", accuracy, context.getResources().getString(R.string.geo_location_m));
        }
    }

}
