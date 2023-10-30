package com.example.joinservice.config;

import com.example.joinservice.service.JoinService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
@RequiredArgsConstructor
public class KafkaStreamsConfig {
    private final Environment env;
    private final JoinService joinService;
    private final ObjectMapper objectMapper;
//    private final MemberServiceClient memberServiceClient;
//
//    @Bean
//    public KStream<String, String> kStream(KStream<String, String> input) {
//        JsonSerde<JsonNode> jsonSerde = new JsonSerde<>(JsonNode.class);
//
//        input.flatMapValues(value -> {
//            JsonNode jsonNode = objectMapper.readTree(value);
//
//            String gatherId = jsonNode.get("gatherId").asText();
//            joinService.getJoins(gatherId).stream()
//        })
//    }
}
