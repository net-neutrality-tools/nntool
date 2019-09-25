package at.alladin.nettest.service.statistic.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ProviderStatisticsResponse {

    List<ProviderStatisticDto> providerStatistics = new ArrayList<>();

    public List<ProviderStatisticDto> getProviderStatistics() {
        return providerStatistics;
    }

    public void setProviderStatistics(List<ProviderStatisticDto> providerStatistics) {
        this.providerStatistics = providerStatistics;
    }
}
