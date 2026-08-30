package com.example.common.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
@ToString
public class Pagination {

    @JsonProperty("page")
    private int page;
    @JsonProperty("size")
    private int size;
    @JsonProperty("total_pages")
    private int totalPages;
    @JsonProperty("total_counts")
    private long totalCounts;

    public Pagination() {
        this(1, 15, 0, 0);
    }

    public Pagination(int page, int size, int totalPages, int totalCounts) {
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalCounts = totalCounts;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) this.totalCounts / size);
    }

    @JsonIgnore
    public int getOffSet() {
        return this.page * this.size;
    }

    public void setSize(int size) {
        this.size = size;
        if (size > 100) {
            this.size = 15;
        }
    }

    @JsonIgnore
    public int getJPAPage() {
        return this.page - 1;
    }

    @JsonIgnore
    public PageRequest getJPAPageRequest() {
        return PageRequest.of(this.getJPAPage(), this.getSize());
    }

    @JsonIgnore
    public PageRequest getJPAPageRequest(Sort sort) {
        return PageRequest.of(this.getJPAPage(), this.getSize(), sort);
    }

}
