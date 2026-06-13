package org.example.expert.domain.user.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Test
    void userRole_has_spring_security_authority() {
        assertThat(UserRole.ADMIN.getRole()).isEqualTo("ROLE_ADMIN");
        assertThat(UserRole.USER.getRole()).isEqualTo("ROLE_USER");
    }
}
