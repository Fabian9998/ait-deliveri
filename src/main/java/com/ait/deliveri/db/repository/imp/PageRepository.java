package com.ait.deliveri.db.repository.imp;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import jakarta.persistence.criteria.Predicate;

import org.springframework.stereotype.Repository;

import com.ait.deliveri.utils.DynamicFilter;

@Repository
public class PageRepository {

	public <T> Page<T> pagination(
            JpaSpecificationExecutor<T> repository,
            Class<T> entity,
            Map<String, String> params,
            Pageable pageable) {

        Page<T> page = repository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = DynamicFilter.generateFilter(params, entity, root, criteriaBuilder);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        return page;
    }
}
