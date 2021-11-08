package com.grpcapp.aggregator.service;

import com.grpcapp.aggregator.dto.RecommendedMovie;
import com.grpcapp.aggregator.dto.UserGenre;
import com.semarslan.grpcapp.common.Genre;
import com.semarslan.grpcapp.movie.MovieSearchRequest;
import com.semarslan.grpcapp.movie.MovieSearchResponse;
import com.semarslan.grpcapp.movie.MovieServiceGrpc;
import com.semarslan.grpcapp.user.UserGenreUpdateRequest;
import com.semarslan.grpcapp.user.UserResponse;
import com.semarslan.grpcapp.user.UserSearchRequest;
import com.semarslan.grpcapp.user.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMovieService {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    @GrpcClient("movie-service")
    private MovieServiceGrpc.MovieServiceBlockingStub movieStub;


    public List<RecommendedMovie> getUserMovieSuggestions(String loginId) {
        UserSearchRequest userSearchRequest = UserSearchRequest.newBuilder().setLoginId(loginId).build();

        UserResponse userResponse = this.userStub.getUserGenre(userSearchRequest);

        MovieSearchRequest movieSearchRequest = MovieSearchRequest.newBuilder().setGenre(userResponse.getGenre()).build();

        MovieSearchResponse movieResponse = this.movieStub.getMovies(movieSearchRequest);

        System.out.println(movieResponse.getMovieList()
                .stream()
                .map(movieDto -> new RecommendedMovie(movieDto.getTitle(), movieDto.getYear(), movieDto.getRating()))
                .collect(Collectors.toList()));
        return movieResponse.getMovieList()
                .stream()
                .map(movieDto -> new RecommendedMovie(movieDto.getTitle(), movieDto.getYear(), movieDto.getRating()))
                .collect(Collectors.toList());

    }

    public void setUserGenre(UserGenre userGenre) {
        UserGenreUpdateRequest userGenreUpdateRequest = UserGenreUpdateRequest.newBuilder()
                .setLoginId(userGenre.getLoginId())
                .setGenre(Genre.valueOf(userGenre.getGenre().toUpperCase()))
                .build();

        UserResponse userResponse = this.userStub.updateUserGenre(userGenreUpdateRequest);

    }
}
