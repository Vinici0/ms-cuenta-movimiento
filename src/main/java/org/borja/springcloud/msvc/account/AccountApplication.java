package org.borja.springcloud.msvc.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;

//@SpringBootApplication
public class AccountApplication {
    public static void main(String[] args) {
//        SpringApplication.run(AccountApplication.class, args);

        // Example 1: Basic number transformation
        Flux<Integer> numbers = Flux.fromIterable(Arrays.asList(1, 2, 3, 4, 5));
        numbers.map(n -> n * 2)
                .subscribe(System.out::println);

        // Example 2: String transformation
        Flux<String> letters = Flux.fromIterable(Arrays.asList("a", "b", "c"));
        letters.map(String::toUpperCase)
                .subscribe(System.out::println);

        // Example 3: Combining streams numeros and letras
        Flux<Integer> numeros = Flux.fromIterable(Arrays.asList(1, 2, 3, 4, 5));
        Flux<String> letras = Flux.fromIterable(Arrays.asList("a", "b", "c"));
        numeros.zipWith(letras, (n, l) -> n + l)
                .subscribe(System.out::println);


        // Example 4: Ejemplo clave
        Mono<List<String>> monoDeLista = Mono.just(List.of("Elemento1", "Elemento2", "Elemento3"));
        Flux<String> fluxDeElementos = monoDeLista.flatMapMany(Flux::fromIterable);

        fluxDeElementos
                .subscribe(
                        elem -> System.out.println("Recibido: " + elem),
                        error -> System.err.println("Error: " + error),
                        () -> System.out.println("Completado")
                );


    }
}
