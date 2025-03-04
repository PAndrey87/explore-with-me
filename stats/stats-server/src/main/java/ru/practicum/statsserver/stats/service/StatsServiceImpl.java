package ru.practicum.statsserver.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.statsserver.stats.mapper.EndpointHitMapper;
import ru.practicum.statsserver.stats.model.EndpointHit;
import ru.practicum.statsserver.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;

    @Override
    @Transactional
    public void create(EndpointHitDto endpointHitDto) {
        log.info("StatsService: Начало выполнения метода create().");

        log.info("StatsService.create(): Маппинг из dto.");
        EndpointHit endpointHit = endpointHitMapper.toEndpointHit(endpointHitDto);

        log.info("StatsService.create(): Добавление endpoint hit в базу данных.");
        statsRepository.save(endpointHit);
        log.info("StatsService.create(): EndpointHit успешно сохранен.");
    }

    @Override
    public List<ViewStatsDto> getStats(String start,
                                       String end,
                                       List<String> uris,
                                       boolean unique) {
        log.info("StatsService: Начало выполнения метода getStats().");
        log.info("StatsService.getStats(): Парсинг даты и времени.");
        LocalDateTime startTime = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, DATE_TIME_FORMATTER);

        List<EndpointHit> hits = new ArrayList<>();

        log.info("StatsService.getStats(): Проверка на существование списка uris.");
        if (uris != null && !uris.isEmpty()) {
            log.info("StatsService.getStats(): Получение hits со списком uris.");
            hits.addAll(statsRepository.findByTimestampBetweenAndUriIn(startTime, endTime, uris));
        } else {
            log.info("StatsService.getStats(): Получение hits без списка uris.");
            hits.addAll(statsRepository.findByTimestampBetween(startTime, endTime));
        }

        log.info("StatsService.getStats(): Проверка на существование параметра unique.");
        if (unique) {
            log.info("StatsService.getStats(): Получение hits с параметром unique.");
            Map<String, EndpointHit> uniqueHitsByIp = new HashMap<>();

            for (EndpointHit hit : hits) {
                String ip = hit.getIp();
                uniqueHitsByIp.putIfAbsent(ip, hit);
            }
            log.info("StatsService.getStats(): Список с уникальными hits успешно получен.");
            log.info("StatsService.getStats(): Сбор статистики на основе списка uniqueHitsByIp.");
            return toViewStatsDtoList(uniqueHitsByIp.values());
        }

        log.info("StatsService.getStats(): Сбор статистики на основе списка hits.");
        return toViewStatsDtoList(hits);
    }

    private List<ViewStatsDto> toViewStatsDtoList(Collection<EndpointHit> hits) {
        log.info("StatsService: Начало выполнения метода toViewStatsDtoList().");
        log.info("StatsService.toViewStatsDtoList(): Начало сбора статистики.");
        Map<String, Map<String, Long>> groupedStats = hits.stream()
                .collect(Collectors.groupingBy(EndpointHit::getApp,
                        Collectors.groupingBy(EndpointHit::getUri, Collectors.counting())));

        List<ViewStatsDto> result = groupedStats.entrySet().stream()
                .flatMap(appEntry -> appEntry.getValue().entrySet().stream()
                        .map(uriEntry -> ViewStatsDto.builder()
                                .app(appEntry.getKey())
                                .uri(uriEntry.getKey())
                                .hits(uriEntry.getValue().intValue())
                                .build()))
                .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                .toList();
        log.info("StatsService.toViewStatsDtoList(): Статистика успешно собрана.");
        return result;
    }
}