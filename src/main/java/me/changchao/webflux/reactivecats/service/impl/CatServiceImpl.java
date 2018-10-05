package me.changchao.webflux.reactivecats.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import me.changchao.webflux.reactivecats.dto.CatDto;
import me.changchao.webflux.reactivecats.service.CatService;
import reactor.core.publisher.Flux;

@Service
public class CatServiceImpl implements CatService {

	@Override
	public Flux<CatDto> search() {
		WebClient client = WebClient.create("https://api.thecatapi.com/v1/images/search");
		return client.get().retrieve().bodyToFlux(CatDto.class);
	}

}
