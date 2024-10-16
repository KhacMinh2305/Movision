package architecture.data.network.api;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import architecture.data.local.entity.People;
import architecture.data.model.genre.Genres;
import architecture.data.model.movie.type.FavoriteMovie;
import architecture.data.model.movie.type.MovieByGenre;
import architecture.data.model.movie.type.PlayingApiMovie;
import architecture.data.model.movie.type.PopularApiMovie;
import architecture.data.model.movie.type.TopRatedApiMovie;
import architecture.data.model.movie.type.TrendingApiMovie;
import architecture.data.model.movie.type.UpcomingApiMovie;
import architecture.data.model.people.ApiPopularPeople;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface TmdbServices {

    // tokem
    /*Token Api*/
    @GET("3/authentication/token/new")
    Single<JsonObject> requestAccessToken();

    @POST("3/authentication/token/validate_with_login")
    Single<JsonObject> requestSessionId(@Body JsonObject userInfo);

    //--------------------------------Genres--------------------------------
    @GET("3/genre/movie/list")
    Single<Genres> getMovieGenres();

    // ------------------------------Preview movie api-------------------------------
    @GET("3/movie/upcoming")
    Single<UpcomingApiMovie> loadPreviewUpcomingMovie(@Query("page") int page);

    @GET("3/trending/movie/{time_window}")
    Single<TrendingApiMovie> loadPreviewTrendingCategory(@Path("time_window") String timeWindow);

    @GET("3/movie/top_rated")
    Single<TopRatedApiMovie> loadPreviewTopRatedCategory();

    @GET("3/movie/popular")
    Single<PopularApiMovie> loadPreviewPopularCategory();

    @GET("3/movie/now_playing")
    Single<PlayingApiMovie> loadPreviewPlayingCategory();

    // ------------------------------------Loading Movie for Paging--------------------------------------
    @GET("3/trending/movie/{time_window}")
    Single<TrendingApiMovie> loadTrendingMovies(@Path("time_window") String timeWindow, @Query("page") int page);

    @GET("3/movie/top_rated")
    Single<TopRatedApiMovie> loadTopRatedMovies(@Query("page") int page);

    @GET("3/movie/popular")
    Single<PopularApiMovie> loadPopularMovies(@Query("page") int page);

    @GET("3/movie/now_playing")
    Single<PlayingApiMovie> loadPlayingMovies(@Query("page") int page);


    // ------------------------------Discover movie-------------------------------
    @GET("3/discover/movie")
    Single<MovieByGenre> loadMovieByGenre(@QueryMap Map<String, Object> filters);

    // ------------------------------User's movies-------------------------------
    @GET("3/account/{account_id}/favorite/movies")
    Single<FavoriteMovie> loadFavoriteMovie(@Path("account_id") int accountId, @Query("page") int page);

    // ------------------------------Preview people-------------------------------
    @GET("3/person/popular")
    Single<ApiPopularPeople> loadPopularPeople(@Query("page") int page);
}
