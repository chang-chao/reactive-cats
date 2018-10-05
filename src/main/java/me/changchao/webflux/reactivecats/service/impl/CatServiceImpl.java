package me.changchao.webflux.reactivecats.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import me.changchao.webflux.reactivecats.dto.CatDto;
import me.changchao.webflux.reactivecats.service.CatService;
import reactor.cache.CacheFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

@Service
public class CatServiceImpl implements CatService {
	private static final String CACHE_NAME = "sr";
	private static final String KEY = "k";

	@Autowired
	private CacheManager cacheManager;

	@Override
	public Flux<CatDto> search() {
		// https://stackoverflow.com/questions/49095366/right-way-to-use-spring-webclient-in-multi-thread-environment
		WebClient client = WebClient.create("https://api.thecatapi.com/v1/images/search");
		Flux<CatDto> fromServer = client.get().retrieve().bodyToFlux(CatDto.class);

		return CacheFlux.lookup(reader(), KEY).onCacheMissResume(fromServer).andWriteWith(writer());
	}

	private BiFunction<String, List<Signal<CatDto>>, Mono<Void>> writer() {
		return (k, sigs) -> Flux.fromIterable(sigs).dematerialize().collectList()
				.doOnNext(l -> cacheManager.getCache(CACHE_NAME).put(k, l)).then();
	}

	@SuppressWarnings("unchecked")
	private Function<String, Mono<List<Signal<CatDto>>>> reader() {
		return k -> Mono
				.justOrEmpty(
						(Optional.ofNullable((List<CatDto>) (cacheManager.getCache(CACHE_NAME).get(k, List.class)))))
				.flatMap(v -> Flux.fromIterable(v).materialize().collectList());
	}

}
