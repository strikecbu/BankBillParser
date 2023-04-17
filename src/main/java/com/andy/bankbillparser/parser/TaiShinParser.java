package com.andy.bankbillparser.parser;

import com.andy.bankbillparser.model.BasicTransaction;
import com.andy.bankbillparser.model.CardTransaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TaiShinParser implements Parser<CardTransaction> {

    @Value("${bank.taishin.cards}")
    private String cards;

    private String nowCard = "";

    @Override
    public Flux<CardTransaction> parse(Flux<String> source) {
        List<String> cardList = Arrays.asList(cards.split(","));

        return range(source, "消費日 入帳起息日 消費明細 新臺幣金額 外幣折算日 消費地 幣 別 外幣金額", " 玫瑰Giving卡本期回饋計算：")
                .doOnNext(line -> {
                    Optional<String> card = cardList.stream().filter(line::contains).findFirst();
                    card.ifPresent(s -> nowCard = s);
                })
                .map(line -> nowCard + line)
                .groupBy(line -> {
                    Optional<String> card = cardList.stream().filter(line::contains).findFirst();
                    return card.orElse("");
                })
                .flatMap(group -> {
                    String key = group.key();
                    if (key.isEmpty()) {
                        return Flux.empty();
                    }
                    return group
                            .map(line -> line.replace(key, ""))
                            .map(line -> {
                                Pattern pattern = Pattern.compile("^(\\d{3}/\\d{2}/\\d{2})\\s+.+$");
                                Matcher matcher = pattern.matcher(line);
                                if (matcher.find()) {
                                    return "@@" + line;
                                }
                                return line;
                            })
                            .reduce("", (s1, s2) -> s1 + s2)
                            .flatMapMany(s -> Flux.fromArray(s.split("@@")))
                            .flatMap(line -> {
                                Pattern pattern = Pattern.compile("^(\\d{3}/\\d{2}/\\d{2})\\s+(\\d{3}/\\d{2}/\\d{2})\\s+(.+?)\\s+(-?\\d+)(.*)$");
                                Matcher matcher = pattern.matcher(line);
                                if (matcher.matches()) {
                                    String dateStr = matcher.group(1); // 112/01/20
                                    String[] dateParts = dateStr.split("/");
                                    String date = Integer.parseInt(dateParts[0]) + 1911 + "-" + dateParts[1] + "-" + dateParts[2];
                                    String description = matcher.group(3); // ＰＣＨＯＭＥ１ 第03/06期 /TW
                                    String amount = matcher.group(4); // 566
                                    CardTransaction cardTransaction = new CardTransaction();
                                    cardTransaction.setSource(key);
                                    cardTransaction.setDate(date);
                                    cardTransaction.setDescription(description);
                                    cardTransaction.setAmount(Long.parseLong(amount));
                                    cardTransaction.setCard(key);
                                    return Flux.just(cardTransaction);
                                }
                                return Flux.empty();
                            })
                            .doOnNext(System.out::println);
                });
    }
}
