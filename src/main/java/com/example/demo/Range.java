package com.example.demo;


public class Range {
    private long start;
    private long end;

    public Range(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public static Range parseRangeHeader(String rangeHeader, long fileSize) {
        if (rangeHeader == null) {
            return null;
        }

        String[] ranges = rangeHeader.split("=")[1].split("-");
        long start = Long.parseLong(ranges[0]);
        long end = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileSize - 1;

        return new Range(start, end);
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }
}
