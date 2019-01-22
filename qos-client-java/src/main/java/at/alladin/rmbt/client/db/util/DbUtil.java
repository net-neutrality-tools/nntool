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

package at.alladin.rmbt.client.db.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import at.alladin.nettest.shared.model.HistoryItem;
import at.alladin.rmbt.client.db.model.DbHistoryItem;
import at.alladin.rmbt.client.db.model.DbMeasurementItem;
import at.alladin.rmbt.client.db.model.HistoryFilter;
/**
 * Created by lb on 12.09.16.
 */
public class DbUtil {

    /**
     * generates a {@link DbHistoryItem} from a {@link HistoryItem}
     * @param historyItem
     * @return
     */
    public static DbHistoryItem toDbHistoryItem(final HistoryItem historyItem) {
        final Gson gson = new Gson();
        final String historyItemStringified = gson.toJson(historyItem);
        final DbHistoryItem dbItem = new DbHistoryItem();
        dbItem.setUuid(historyItem.getTestUuid());
        dbItem.setDevice(historyItem.getModel());
        dbItem.setHistoryItem(historyItemStringified);
        dbItem.setNetworkType(historyItem.getNetworkType());
        dbItem.setTimeStamp(historyItem.getTime());

        return dbItem;
    }

    /**
     *
     * @param dao
     * @return
     */
    public static long getLastHistoryTime(final Dao<DbHistoryItem, String> dao) {
        try {
            final Long maxTimeStamp = dao.queryRaw("select max(timeStamp) from history", new RawRowMapper<Long>() {
                @Override
                public Long mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    try {
                        return Long.parseLong(resultColumns[0]);
                    }
                    catch (final NumberFormatException e) {
                        return 0L;
                    }
                }
            }).getFirstResult();

            return maxTimeStamp != null ? maxTimeStamp : 0;
        }
        catch (final SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Syncs a remote history list with the local db cache. Disassociated measurements must be removed from DB. New measurements must be added to DB.
     * @param historyDao
     * @param measurementDao
     * @param remoteHistoryItemList
     * @return true if everything was ok
     */
    public static boolean syncHistoryList(final Dao<DbHistoryItem, String> historyDao, final Dao<DbMeasurementItem, String> measurementDao, final List<HistoryItem> remoteHistoryItemList) {
        try {
            final GenericRawResults<String> rawResults = historyDao.queryRaw("SELECT uuid FROM history",
                    new RawRowMapper<String>() {
                        @Override
                        public String mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                            return resultColumns[0];
                        }
                    });

            final Set<String> dbUuids = new HashSet<>();
            if (rawResults != null) {
                dbUuids.addAll(rawResults.getResults());
            }

            final List<HistoryItem> newItems = new ArrayList<>();
            for (final HistoryItem hi : remoteHistoryItemList) {
                //if uuid set contains uuid from history item, remove it from the collection.
                if (dbUuids.contains(hi.getTestUuid())) {
                    dbUuids.remove(hi.getTestUuid());
                }
                else {
                    newItems.add(hi);
                }
            }

            //remove all history items left in the uuid collection
            final DeleteBuilder<DbHistoryItem, String> deleteBuilderHistory = historyDao.deleteBuilder();
            deleteBuilderHistory.where().in("uuid", dbUuids);
            final int rowsAffectedHistory = deleteBuilderHistory.delete();

            //remove all measurement items left in the uuid collection
            final DeleteBuilder<DbMeasurementItem, String> deleteBuilderMeasurement = measurementDao.deleteBuilder();
            deleteBuilderMeasurement.where().in("uuid", dbUuids);
            final int rowsAffectedMeasurement = deleteBuilderMeasurement.delete();

            System.out.println("History sync; New items: " + newItems.size() + ", removed items: " + dbUuids.size() + " = " + dbUuids);
            //insert all new history items:
            for (final HistoryItem hi : newItems) {
                //store item in DB only if time is set
                if (hi.getTime() != null) {
                    historyDao.create(toDbHistoryItem(hi));
                }
            }

            return true;
        }
        catch (final SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * get all available values for specific filters
     * @param historyDao
     * @param historyFilters
     * @return
     */
    public static Map<HistoryFilter, List<String>> getAvailableHistoryFilters(final Dao<DbHistoryItem, String> historyDao, final HistoryFilter[] historyFilters) {
        final Map<HistoryFilter, List<String>> resultMap = new HashMap<>();
        final StringBuilder filterFieldNames = new StringBuilder();

        try {
            for (int i = 0; i < historyFilters.length; i++) {
                final GenericRawResults<String> rawResults = historyDao.queryRaw("SELECT DISTINCT " + historyFilters[i].getFieldName() + " FROM history",
                        new RawRowMapper<String>() {
                            @Override
                            public String mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                                return resultColumns[0];
                            }
                        });

                resultMap.put(historyFilters[i], rawResults.getResults());
            }

        }
        catch (final SQLException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    /**
     *
     * @param historyDao
     * @param filterMap
     * @return
     */
    public static List<DbHistoryItem> getFilteredHistoryList(final Dao<DbHistoryItem, String> historyDao, final Map<HistoryFilter, List<String>> filterMap) {
        List<DbHistoryItem> resutlList = null;
        try {
            final QueryBuilder<DbHistoryItem, String> queryBuilder = historyDao.queryBuilder();

            Iterator<Map.Entry<HistoryFilter, List<String>>> filterEntryIterator = filterMap.entrySet().iterator();

            Where<DbHistoryItem, String> whereStmt = null;

            while (filterEntryIterator.hasNext()) {
                final Map.Entry<HistoryFilter, List<String>> filterEntry = filterEntryIterator.next();

                if (whereStmt == null) {
                    whereStmt = queryBuilder.where().in(filterEntry.getKey().getFieldName(), filterEntry.getValue());
                } else {
                    whereStmt.and().in(filterEntry.getKey().getFieldName(), filterEntry.getValue());
                }
            }

            resutlList = queryBuilder.orderBy("timeStamp", false).query();
        }
        catch (final SQLException e) {
            e.printStackTrace();
        }

        return resutlList;
    }
}
