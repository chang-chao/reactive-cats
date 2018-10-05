package me.changchao.webflux.reactivecats.service.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import me.changchao.webflux.reactivecats.dto.CatDto;
import me.changchao.webflux.reactivecats.service.CatService;
import reactor.cache.CacheFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

@Service
public class CatServiceImpl implements CatService {
	private static AtomicReference<Context> storeRef = new AtomicReference<>(Context.empty());

	private static String key = "search_result_key";

	@Override
	public Flux<CatDto> search() {
		// https://stackoverflow.com/questions/49095366/right-way-to-use-spring-webclient-in-multi-thread-environment
		WebClient client = WebClient.create("https://api.thecatapi.com/v1/images/search");
		Flux<CatDto> fromServer = client.get().retrieve().bodyToFlux(CatDto.class);

		Function<String, Mono<List<Signal<CatDto>>>> reader = k -> Mono
				.justOrEmpty((storeRef.get().<List<CatDto>>getOrEmpty(k)))
				.flatMap(v -> Flux.fromIterable(v).materialize().collectList());

		BiFunction<String, List<Signal<CatDto>>, Mono<Void>> writer = (k, sigs) -> Flux.fromIterable(sigs)
				.dematerialize().collectList().doOnNext(l -> storeRef.updateAndGet(ctx -> ctx.put(k, l))).then();

		Flux<CatDto> cats = CacheFlux.lookup(reader, key).onCacheMissResume(fromServer).andWriteWith(writer);
		return cats;
	}

}
