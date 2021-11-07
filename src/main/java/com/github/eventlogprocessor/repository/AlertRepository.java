package com.github.eventlogprocessor.repository;

import com.github.eventlogprocessor.model.persistence.Alert;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends CrudRepository<Alert, String> {
}
