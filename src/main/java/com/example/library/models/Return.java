package com.example.library.models;

import com.example.library.enums.ReturnStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DiscriminatorValue("RETURN")
public class Return extends Action {
    private ReturnStatus status;
    public Return(Edition edition, User user, Date day/*, ReturnStatus status*/)
    {
        this.edition = edition;
        this.user = user;
        this.day = day;
       // this.status = status;
    }
    public void setStatus(Date needed, Date real)
    {
        status = real.before(needed) ? ReturnStatus.ON_TIME : ReturnStatus.OVERDUE;
    }
    @Override
    public String getType() {
        return this.getClass().getName();
    }
}
