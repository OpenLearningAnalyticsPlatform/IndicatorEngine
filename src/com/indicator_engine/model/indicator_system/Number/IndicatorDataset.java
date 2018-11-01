/*
 * Open Platform Learning Analytics : Indicator Engine
 * Copyright (C) 2015  Learning Technologies Group, RWTH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.indicator_engine.model.indicator_system.Number;

import de.rwthaachen.openlap.dataset.OpenLAPPortConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Arham on 1/22/2017.
 */
public class IndicatorDataset implements Serializable, Cloneable {
    private List<String> selectedSource;
    private List<String> selectedPlatform;
    private List<String> selectedAction;

    private List<Integer> selectedMinor;
    private String selectedMajor;
    private String selectedType;

    private List<EntityValues>  entityValues = new ArrayList<EntityValues>();
    private List<UserSearchSpecifications>  userSpecifications = new ArrayList<UserSearchSpecifications>();
    private List<SessionSpecifications>  sessionSpecifications = new ArrayList<SessionSpecifications>();
    private List<TimeSearchSpecifications>  timeSpecifications = new ArrayList<TimeSearchSpecifications>();

    private String retrievableObjects;
    private List<String> entityDisplayObjects;

    private String datasetName;

    private Long analyticsMethodId;
    private OpenLAPPortConfig queryToMethodConfig;
    private String analyticsMethodParams;

    public List<String> getSelectedSource() {
        return selectedSource;
    }

    public void setSelectedSource(List<String> selectedSource) {
        this.selectedSource = selectedSource;
    }

    public List<String> getSelectedPlatform() {
        return selectedPlatform;
    }

    public void setSelectedPlatform(List<String> selectedPlatform) {
        this.selectedPlatform = selectedPlatform;
    }

    public List<String> getSelectedAction() {
        return selectedAction;
    }

    public void setSelectedAction(List<String> selectedAction) {
        this.selectedAction = selectedAction;
    }

    public List<Integer> getSelectedMinor() {
        return selectedMinor;
    }

    public void setSelectedMinor(List<Integer> selectedMinor) {
        this.selectedMinor = selectedMinor;
    }

    public String getSelectedMajor() {
        return selectedMajor;
    }

    public void setSelectedMajor(String selectedMajor) {
        this.selectedMajor = selectedMajor;
    }

    public String getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
    }

    public List<EntityValues> getEntityValues() {
        return entityValues;
    }

    public void setEntityValues(List<EntityValues> entityValues) {
        this.entityValues = entityValues;
    }

    public List<UserSearchSpecifications> getUserSpecifications() {
        return userSpecifications;
    }

    public void setUserSpecifications(List<UserSearchSpecifications> userSpecifications) {
        this.userSpecifications = userSpecifications;
    }

    public List<SessionSpecifications> getSessionSpecifications() {
        return sessionSpecifications;
    }

    public void setSessionSpecifications(List<SessionSpecifications> sessionSpecifications) {
        this.sessionSpecifications = sessionSpecifications;
    }

    public List<TimeSearchSpecifications> getTimeSpecifications() {
        return timeSpecifications;
    }

    public void setTimeSpecifications(List<TimeSearchSpecifications> timeSpecifications) {
        this.timeSpecifications = timeSpecifications;
    }

    public String getRetrievableObjects() {
        return retrievableObjects;
    }

    public void setRetrievableObjects(String retrievableObjects) {
        this.retrievableObjects = retrievableObjects;
    }

    public List<String> getEntityDisplayObjects() {
        return entityDisplayObjects;
    }

    public void setEntityDisplayObjects(List<String> entityDisplayObjects) {
        this.entityDisplayObjects = entityDisplayObjects;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public Long getAnalyticsMethodId() {
        return analyticsMethodId;
    }

    public void setAnalyticsMethodId(Long analyticsMethodId) {
        this.analyticsMethodId = analyticsMethodId;
    }

    public OpenLAPPortConfig getQueryToMethodConfig() {
        return queryToMethodConfig;
    }

    public void setQueryToMethodConfig(OpenLAPPortConfig queryToMethodConfig) {
        this.queryToMethodConfig = queryToMethodConfig;
    }

    public String getAnalyticsMethodParams() {
        return analyticsMethodParams;
    }

    public void setAnalyticsMethodParams(String analyticsMethodParams) {
        this.analyticsMethodParams = analyticsMethodParams;
    }

    @Override
    public IndicatorDataset clone() throws CloneNotSupportedException {
        return (IndicatorDataset)super.clone();
    }
}
