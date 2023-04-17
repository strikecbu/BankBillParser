package com.andy.bankbillparser.parser;

import reactor.core.publisher.Flux;

public interface Parser<T> {

    Flux<T> parse(Flux<String> source);

    default Flux<String> range(Flux<String> source, String start, String end) {
        return source.skipWhile(line -> !line.contains(start)).takeUntil(line -> line.contains(end));
    }

}
