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

package at.alladin.nettest.nntool.android.app.util.info;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public abstract class ListenableGatherer<T, L extends InformationServiceListener> extends Gatherer<T> {

    protected List<L> listenerList = new ArrayList<>();

    public void addListener(final L listener) {
        if (listener != null && !listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public boolean removeListener(final L listener) {
        return listenerList.remove(listener);
    }

    public List<L> getListenerList() {
        return listenerList;
    }
}
