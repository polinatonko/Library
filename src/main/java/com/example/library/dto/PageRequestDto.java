package com.example.library.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class PageRequestDto {
    private Integer pageNo = 0;
    private Integer pageSize = 12;
    private Sort.Direction sort = Sort.Direction.ASC;
    private String sortByColumn = "id";
    public PageRequestDto(Optional<Integer> pageNo, Optional<Integer> pageSize)
    {
        pageNo.ifPresent(integer -> this.pageNo = integer);
        pageSize.ifPresent(integer -> this.pageSize = integer);
    }

    public Pageable getPageable(PageRequestDto dto) {
        Integer page = dto.getPageNo();
        Integer size = dto.getPageSize();
        Sort.Direction sort = dto.getSort();
        String sortByColumn = dto.getSortByColumn();

        return PageRequest.of(page, size, sort, sortByColumn);
    }
}
