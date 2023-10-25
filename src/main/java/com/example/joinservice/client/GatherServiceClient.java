package com.example.joinservice.client;

import com.example.joinservice.vo.ResponseGather;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gather-service")
public interface GatherServiceClient {
    @GetMapping("/gather-service/{gatherId}")
    ResponseGather getGather(@PathVariable String gatherId);
}
