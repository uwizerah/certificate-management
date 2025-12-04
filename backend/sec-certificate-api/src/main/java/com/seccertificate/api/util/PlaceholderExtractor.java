package com.seccertificate.api.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlaceholderExtractor {

    private static final Pattern PATTERN = Pattern.compile("\\{\\{(.*?)\\}\\}");

    public static List<String> extract(String html) {
        Matcher matcher = PATTERN.matcher(html);

        return matcher.results()
                .map(m -> m.group(1).trim())
                .distinct()
                .collect(Collectors.toList());
    }
}
