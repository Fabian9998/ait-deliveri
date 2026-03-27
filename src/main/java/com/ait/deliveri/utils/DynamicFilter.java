package com.ait.deliveri.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public final class DynamicFilter {

	private static final DateTimeFormatter FLDT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	private static final DateTimeFormatter FLD = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	
	private DynamicFilter() {
		
	}

    public static List<Predicate> generateFilter(Map<String, String> params, Class<?> clazz, Root<?> root,
			CriteriaBuilder criteriaBuilder) {

		List<Predicate> listPredicate = new ArrayList<Predicate>();
		for (Map.Entry<String, String> e : params.entrySet()) {
			try {
				List<String> rawValues = Arrays.stream(e.getValue().split(",")).map(String::trim)
						.filter(v -> v != null && !v.isEmpty()).toList();
				if (!rawValues.isEmpty()) {
					String fieldName = e.getKey();
					if (fieldName.contains(".")) {
						String[] parts = fieldName.split("\\.");
						String relationName = parts[0];
						String relatedField = parts[1];

						Field relationField = clazz.getDeclaredField(relationName);
						Class<?> relatedClass;

						Type genericType = relationField.getGenericType();
						if (genericType instanceof ParameterizedType) {
							ParameterizedType paramType = (ParameterizedType) genericType;
							Type rawType = paramType.getRawType();

							if (rawType == Set.class || rawType == List.class || rawType == Collection.class) {
								relatedClass = (Class<?>) paramType.getActualTypeArguments()[0];
							} else {
								relatedClass = relationField.getType();
							}
						} else {
							relatedClass = relationField.getType();
						}

						Join<?, ?> join = root.join(relationName, JoinType.INNER);

						Field relatedProperty = relatedClass.getDeclaredField(relatedField);
						List<?> values = processData(relatedProperty.getType().getSimpleName(), rawValues);

						if (!values.isEmpty()) {
							Predicate predicate = values.size() == 1
									? predicateEqual(relatedProperty, values.get(0), criteriaBuilder, join)
									: predicateIn(relatedProperty, values, criteriaBuilder, join);

							if (predicate != null) {
								listPredicate.add(predicate);
							}
						}
					} else {
						Field campo = clazz.getDeclaredField(e.getKey());
						List<?> values = (e.getValue() != null && !e.getValue().toString().isBlank())
								? processData(campo.getType().getSimpleName(), rawValues)
								: new ArrayList<>();

						if (!values.isEmpty()) {
							Predicate predicate = values.size() == 1
									? predicateEqual(campo, values.get(0), criteriaBuilder, root)
									: predicateIn(campo, values, criteriaBuilder, root);
							if (predicate != null) {
								listPredicate.add(predicate);
							}
						}
					}
				}
			} catch (Exception ex) {
			}
		}

		return listPredicate;
	}
	
	private static List<?> processData(String typeField, List<String> values) {
		return switch (typeField) {
		case "String" -> values;
		case "UUID" -> values.stream().map(v -> UUID.fromString(v)).collect(Collectors.toList());
		case "Integer" -> values.stream().map(v -> Integer.parseInt(v)).collect(Collectors.toList());
		case "Long" -> values.stream().map(v -> Long.parseLong(v)).collect(Collectors.toList());
		case "Boolean" -> values.stream().map(v -> Boolean.parseBoolean(v)).collect(Collectors.toList());
		case "LocalDateTime" -> values.stream().map(v -> LocalDateTime.parse(v, FLDT)).collect(Collectors.toList());
		case "LocalDate" -> values.stream().map(v -> LocalDate.parse(v, FLD)).collect(Collectors.toList());
		default -> values;
		};
	}
	
	private static Predicate predicateEqual(Field field, Object value, CriteriaBuilder criteriaBuilder,
			Path<?> path) {

		return switch (field.getType().getSimpleName()) {
		case "UUID", "Integer", "Long", "Boolean" -> criteriaBuilder.equal(path.get(field.getName()), value);
		case "String" -> criteriaBuilder.like(path.get(field.getName()), "%" + value + "%");
		case "LocalDateTime" -> criteriaBuilder.between(path.get(field.getName()), (LocalDateTime)value, (LocalDateTime)value);
		case "LocalDate" -> criteriaBuilder.between(path.get(field.getName()), (LocalDate)value, (LocalDate)value);
		default -> criteriaBuilder.equal(path.get(field.getName()), value);
		};
	}

	private static Predicate predicateIn(Field field, List<?> values, CriteriaBuilder criteriaBuilder,
			Path<?> path) {

		return switch (field.getType().getSimpleName()) {
		case "String", "UUID", "Integer", "Long", "Boolean" -> path.get(field.getName()).in(values);
		case "LocalDateTime" -> criteriaBuilder.between(path.get(field.getName()), (LocalDateTime)values.get(0), (LocalDateTime)values.get(1));
		case "LocalDate" -> criteriaBuilder.between(path.get(field.getName()), (LocalDate)values.get(0), (LocalDate)values.get(1));
		default -> path.get(field.getName()).in(values);
		};
	}
}
