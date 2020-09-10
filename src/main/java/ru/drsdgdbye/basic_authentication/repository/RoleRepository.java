package ru.drsdgdbye.basic_authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.drsdgdbye.basic_authentication.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
