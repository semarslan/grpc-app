package com.grpcapp.movie.service;

import com.grpcapp.movie.repository.MovieRepository;
import com.semarslan.grpcapp.movie.MovieDto;
import com.semarslan.grpcapp.movie.MovieSearchRequest;
import com.semarslan.grpcapp.movie.MovieSearchResponse;
import com.semarslan.grpcapp.movie.MovieServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class MovieService extends MovieServiceGrpc.MovieServiceImplBase {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public void getMovies(MovieSearchRequest request, StreamObserver<MovieSearchResponse> responseObserver) {
        List<MovieDto> movieDtoList = this.movieRepository.getMovieByGenreOrderByYearDesc(request.getGenre().toString())
                .stream()
                .map(movie -> MovieDto.newBuilder()
                        .setTitle(movie.getTitle())
                        .setYear(movie.getYear())
                        .setRating(movie.getRating())
                        .build()
                ).collect(Collectors.toList());

        MovieSearchResponse movieSearchResponse = MovieSearchResponse.newBuilder().addAllMovie(movieDtoList).build();

        responseObserver.onNext(movieSearchResponse);
        responseObserver.onCompleted();
    }
}
