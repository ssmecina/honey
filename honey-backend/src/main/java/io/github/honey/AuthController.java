package io.github.honey;

import static io.github.honey.HoneyController.responseEither;
import static io.github.honey.shared.ApiResponse.badRequestError;
import static io.github.honey.shared.Either.right;
import static io.javalin.community.routing.Route.POST;

import io.github.honey.shared.ApiResponse;
import io.github.honey.shared.Either;
import io.javalin.http.Context;

final class AuthController extends HoneyControllerRegistry {

  private final UserDetailsService userDetailsService;

  AuthController(final UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;

    routes(
        new HoneyController("/api/auth/login", responseEither(this::login), POST),
        new HoneyController("/api/auth/register", responseEither(this::register), POST));
  }

  private Either<ApiResponse, UserDetails> login(final Context context) {
    final AuthRequest request = context.bodyAsClass(AuthRequest.class);
    final UserDetails userDetails =
        userDetailsService.authenticate(request.getUsername(), request.getPassword());
    if (userDetails != null) {
      return right(userDetails);
    }

    return badRequestError("Invalid credentials");
  }

  private Either<ApiResponse, UserDetails> register(final Context context) {
    final AuthRequest request = context.bodyAsClass(AuthRequest.class);
    final UserDetails userDetails =
        userDetailsService.registerUser(request.getUsername(), request.getPassword());
    if (userDetails != null) {
      return right(userDetails);
    }

    return badRequestError("Username already exists");
  }
}
