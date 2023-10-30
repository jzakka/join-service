package com.example.joinservice.config;

import com.example.joinservice.service.JoinService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableKafkaStreams
public class KafkaStreamsConfig {
    private final Environment env;
    private final JoinService joinService;
    private final ObjectMapper objectMapper;

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        final String SOURCE_TOPIC = env.getProperty("spring.kafka.source-topic");
        final String DESTINATION_TOPIC = env.getProperty("spring.kafka.destination-topic");

        KStream<String, String> input = streamsBuilder.stream(SOURCE_TOPIC);

        input.flatMapValues(value -> {
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(value);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            String gatherId = jsonNode.get("gatherId").asText();

            return getObjectNodes(gatherId);
        }).to(DESTINATION_TOPIC, Produced.with(Serdes.String(), new JsonSerde<>(JsonNode.class)));

        return input;
    }

    @NotNull
    private List<JsonNode> getObjectNodes(String gatherId) {
        final String TITLE_TEMPLATE = env.getProperty("email.title");
        final String CONTENT_TEMPLATE = env.getProperty("email.content");
        return joinService.getJoins(gatherId).stream()
                .map(joinVo -> {
                    String to = joinVo.getEmail();
                    String title = TITLE_TEMPLATE.formatted(joinVo.getGatherName());
                    String content = CONTENT_TEMPLATE.formatted(joinVo.getGatherId());

                    ObjectNode objectNode = objectMapper.createObjectNode();
                    objectNode.put("to", to);
                    objectNode.put("title", title);
                    objectNode.put("content", content);

                    return (JsonNode) objectNode;
                }).toList();
    }
}
