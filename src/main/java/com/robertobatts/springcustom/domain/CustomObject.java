package com.robertobatts.springcustom.domain;

import com.robertobatts.springcustom.cache.Cacheable;
import com.robertobatts.springcustom.cache.Key;

import java.util.Objects;

public class CustomObject implements Cacheable<CustomObject.PrimaryKey> {
    private PrimaryKey pk;
    private String field;

    public CustomObject(PrimaryKey pk, String field) {
        this.pk = pk;
        this.field = field;
    }

    public PrimaryKey getPk() {
        return pk;
    }

    public String getField() {
        return field;
    }

    @Override
    public PrimaryKey getKey() {
        return getPk();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomObject)) return false;
        CustomObject that = (CustomObject) o;
        return Objects.equals(pk, that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk);
    }

    @Override
    public String toString() {
        return "CustomObject{" +
                "pk=" + pk +
                ", field='" + field + '\'' +
                '}';
    }

    public static class PrimaryKey extends Key {
        private String id;
        private String type;

        public PrimaryKey(String id, String type) {
            this.id = id;
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PrimaryKey)) return false;
            PrimaryKey that = (PrimaryKey) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, type);
        }

        @Override
        public String toString() {
            return "PrimaryKey{" +
                    "id='" + id + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
