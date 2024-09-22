package architecture.domain;

import java.util.ArrayList;
import java.util.List;
import architecture.data.local.entity.Movie;
import architecture.data.model.genre.Genre;
import architecture.data.model.movie.result.ApiMovieResult;

public class MovieConversionHelper {
    private String compressGenres(List<Integer> genreIds, List<Genre> appGenres) {
        StringBuilder compressedGenres = new StringBuilder();
        ListProcessingHelper helper = new ListProcessingHelper();
        for(int i = 0; i < genreIds.size(); i++) {
            String genreName = helper.getGenreById(genreIds.get(i), appGenres).getName();
            compressedGenres.append(genreName);
            if(i != genreIds.size() - 1) {
                compressedGenres.append(",");
            }
        }
        return compressedGenres.toString();
    }

    public List<Movie> convertApiDataToLocalData(List<ApiMovieResult> apiResult, String movieTag, List<Genre> appGenres) {
        List<Movie> listEntity = new ArrayList<>();
        for(ApiMovieResult result : apiResult) {
            Movie movie = result.toEntity(movieTag);
            movie.genres = compressGenres(movie.genresIds, appGenres);
            listEntity.add(movie);
        }
        return listEntity;
    }
}
