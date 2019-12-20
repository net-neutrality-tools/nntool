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

package at.alladin.nettest.service.map.domain.model;

import com.google.gson.annotations.Expose;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import at.alladin.nettest.service.map.domain.model.MapServiceOptions;

public class MapServiceSettings {

    @Expose
    private Map<String, MapServiceOptions> mapServiceOptions;

    private Map<String, MapFilter> mapFilterMap;

    private List<SQLFilter> defaultMapFilters;

    private SQLFilter accuracyMapFilter;

    public Map<String, MapServiceOptions> getMapServiceOptions() {
        return mapServiceOptions;
    }

    public void setMapServiceOptions(Map<String, MapServiceOptions> mapServiceOptions) {
        this.mapServiceOptions = mapServiceOptions;
    }

    public Map<String, MapFilter> getMapFilterMap() {
        return mapFilterMap;
    }

    public void setMapFilterMap(Map<String, MapFilter> mapFilterMap) {
        this.mapFilterMap = mapFilterMap;
    }

    public List<SQLFilter> getDefaultMapFilters() {
        return defaultMapFilters;
    }

    public void setDefaultMapFilters(List<SQLFilter> defaultMapFilters) {
        this.defaultMapFilters = defaultMapFilters;
    }

    public SQLFilter getAccuracyMapFilter() {
        return accuracyMapFilter;
    }

    public void setAccuracyMapFilter(SQLFilter accuracyMapFilter) {
        this.accuracyMapFilter = accuracyMapFilter;
    }

    //TODO: rewrite filters
    /**
     *
     * @author Specure GmbH
     *
     */
    public static abstract class MapFilter {

        /**
         *
         * @param input
         * @return
         */
        public abstract SQLFilter getFilter(String input);
    }

    /**
     *
     * @author Specure GmbH
     *
     */
    public static class StaticMapFilter extends MapFilter {

        /**
         *
         */
        private final SQLFilter filter;

        /**
         *
         * @param where
         */
        public StaticMapFilter(String where) {
            filter = new SQLFilter(where);
        }

        /*
         * (non-Javadoc)
         * @see at.alladin.rmbt.mapServer.MapServerOptions.MapFilter#getFilter(java.lang.String)
         */
        @Override
        public SQLFilter getFilter(String input) {
            return filter;
        }
    }

    /**
     *
     * @author Specure GmbH
     *
     */
    public static class SQLFilter {

        /**
         *
         * @param where
         */
        public SQLFilter(final String where) {
            this.where = where;
        }

        /**
         *
         */
        private final String where;

        /**
         *
         * @param i
         * @param ps
         * @return
         * @throws SQLException
         */
        public int fillParams(final int i, final PreparedStatement ps) throws SQLException {
            return i;
        }

        public String getWhereClause() {
            return where;
        }
    }
}
