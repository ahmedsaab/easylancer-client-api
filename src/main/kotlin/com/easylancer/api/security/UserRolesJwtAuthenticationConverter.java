package com.easylancer.api.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

/** JWT converter that takes the roles from persistent user roles. */
public class UserRolesJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

  private final UserDetailsService userDetailsService;

  public UserRolesJwtAuthenticationConverter(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Override
  public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
    return userDetailsService
        .findByUsername(jwt.getSubject())
        .map(u -> new UsernamePasswordAuthenticationToken(u, "n/a", u.getAuthorities()));
  }
}
