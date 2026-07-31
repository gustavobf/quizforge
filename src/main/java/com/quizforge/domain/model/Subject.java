package com.quizforge.domain.model;

import com.quizforge.domain.exception.BusinessException;

public final class Subject {
    private final Long id;
    private final Long ownerId;
    private final String name;
    private final String description;

    private Subject(Long id, Long ownerId, String name, String description) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        validate();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getDescription() {
        return description;
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Subject name cannot be empty");
        }
        if (name.length() > 100) {
            throw new BusinessException("Subject name cannot exceed 100 characters");
        }
    }

    public static class Builder {
        private Long id;
        private Long ownerId;
        private String name;
        private String description;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder ownerId(Long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Subject build() {
            return new Subject(id, ownerId, name, description);
        }
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return id != null && id.equals(subject.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}