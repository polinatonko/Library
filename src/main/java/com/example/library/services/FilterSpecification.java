package com.example.library.services;

import com.example.library.dto.SearchRequestDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FilterSpecification<T> {
    public Specification<T> getSearchSpecification(SearchRequestDto searchRequestDto)
    {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(searchRequestDto.getColumn()),
                        searchRequestDto.getValue());
    }

    public Specification<T> getSearchSpecification(List<SearchRequestDto> searchRequestDtoList)
    {
        return (root, query, criteriaBuilder) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            for (SearchRequestDto searchRequestDto : searchRequestDtoList)
            {
                Predicate predicate;
                String column = searchRequestDto.getColumn(), value = searchRequestDto.getValue();
                predicate = switch (searchRequestDto.getOperation()) {
                    case LIKE ->
                            criteriaBuilder.like(criteriaBuilder.upper(root.get(column)), "%" + value.toUpperCase() + "%");
                    case IN -> {
                        String[] vars = searchRequestDto.getValue().split(",");
                        yield root.get(searchRequestDto.getColumn()).in(Arrays.asList(vars));
                    }
                    case BETWEEN -> {
                        String[] limits = searchRequestDto.getValue().split(",");
                        yield criteriaBuilder.between(root.get(column), Long.parseLong(limits[0]), Long.parseLong(limits[1]));
                    }
                    case JOIN -> {
                        String[] vars = column.split("-");
                        yield criteriaBuilder.equal(root.join(vars[0]).get(vars[1]), value);
                    }
                    default -> criteriaBuilder.equal(root.get(column), value);
                };
                predicates.add(predicate);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}