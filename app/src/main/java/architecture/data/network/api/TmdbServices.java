package architecture.data.network.api;
import com.google.gson.JsonObject;
import java.util.Map;
import architecture.data.model.genre.Genres;
import architecture.data.model.image.ApiTmDbImage;
import architecture.data.model.movie.category.ApiMovieDetails;
import architecture.data.model.movie.category.MovieByGenre;
import architecture.data.model.movie.category.PlayingApiMovie;
import architecture.data.model.movie.category.PopularApiMovie;
import architecture.data.model.movie.category.SimilarApiMovie;
import architecture.data.model.movie.category.TopRatedApiMovie;
import architecture.data.model.movie.category.TrendingApiMovie;
import architecture.data.model.movie.category.UpcomingApiMovie;
import architecture.data.model.movie.other.ApiMovieClip;
import architecture.data.model.people.ApiPeople;
import architecture.data.model.people.ApiPeopleDetail;
import architecture.data.model.people.MoviePeople;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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

    @GET("3/authentication/guest_session/new")
    Single<JsonObject> requestSession();

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

    @GET("3/movie/{movie_id}")
    Single<ApiMovieDetails> loadMovieDetails(@Path("movie_id") int movieId);

    @GET("3/movie/{movie_id}/credits")
    Single<MoviePeople> loadMoviePeople(@Path("movie_id") int movieId);

    @GET("3/movie/{movie_id}/videos")
    Single<ApiMovieClip> loadMovieClips(@Path("movie_id") int movieId);

    @GET("3/movie/{movie_id}/similar")
    Single<SimilarApiMovie> loadSimilarMovies(@Path("movie_id") int movieId, @Query("page") int page);

    // ------------------------------Discover movie-------------------------------
    @GET("3/discover/movie")
    Single<MovieByGenre> loadMovieByGenre(@QueryMap Map<String, Object> filters);

    // ------------------------------Preview people-------------------------------
    @GET("3/person/popular")
    Single<ApiPeople> loadPopularPeople(@Query("page") int page);

    @GET("3/trending/person/day")
    Single<ApiPeople> loadTrendingPeople(@Query("page") int page);

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("3/movie/{movie_id}/rating")
    Completable addRating(@Path("movie_id") int movieId, @Query("guest_session_id") String session, @Body JsonObject rating);

    @GET("3/person/{person_id}")
    Single<ApiPeopleDetail> loadPeopleDetail(@Path("person_id") int personId);

    @GET("3/person/{person_id}/images")
    Single<ApiTmDbImage> loadPeopleImages(@Path("person_id") int personId);
}
