package at.alladin.nettest.nntool.android.app.util.connection;

import at.alladin.nntool.shared.map.MapMarkerRequest;
import at.alladin.nntool.shared.map.MapMarkerResponse;
import at.alladin.nntool.shared.map.info.MapInfoResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public interface MapService {

    @POST("/api/v0/tiles/markers")
    Call<MapMarkerResponse> getMeasurementsRequest(@Body MapMarkerRequest mapMarkerRequest);

    @GET("/api/v0/tiles/info")
    Call<MapInfoResponse> getMapInfoResponse();
}
