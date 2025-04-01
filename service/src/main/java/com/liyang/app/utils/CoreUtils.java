package com.liyang.app.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CoreUtils {

    public List<String> readTxt(String txtPath) throws IOException {
        Path path = Paths.get(txtPath);
        List<String> data = Files.readAllLines(path);
        return data;
    }

    public <T> List<T> findMostFrequentElements(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List不能为空");
        }

        Map<T, Long> frequencyMap = list.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long maxFrequency = Collections.max(frequencyMap.values());

        return frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
