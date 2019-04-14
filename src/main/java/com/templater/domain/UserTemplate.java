package com.templater.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class UserTemplate {
    private UserTemplateId pk;

    @EmbeddedId
    public UserTemplateId getPk() {
        return pk;
    }

    public void setPk(UserTemplateId pk) {
        this.pk = pk;
    }
}
