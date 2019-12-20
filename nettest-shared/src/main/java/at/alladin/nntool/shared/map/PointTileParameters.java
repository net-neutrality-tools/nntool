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

package at.alladin.nntool.shared.map;

public class PointTileParameters extends MapTileParameters {

    /**
     *
     */
    protected Double pointDiameter;

    /**
     *
     */
    protected Boolean noFill;

    /**
     *
     */
    protected Boolean noColor;

    /**
     *
     */
    protected String highlight;

    @Override
    public boolean isNoCache() {
        return highlight != null && highlight.length() > 0; //TODO: different caching for null and ""
    }

    @Override
    public MapTileType getMapTileType() {
        return MapTileType.POINTS;
    }

    public Double getPointDiameter() {
        return pointDiameter;
    }

    public void setPointDiameter(Double pointDiameter) {
        this.pointDiameter = pointDiameter;
    }

    public Boolean isNoFill() {
        return noFill;
    }

    public void setNoFill(Boolean noFill) {
        this.noFill = noFill;
    }

    public Boolean isNoColor() {
        return noColor;
    }

    public void setNoColor(Boolean noColor) {
        this.noColor = noColor;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }
}
