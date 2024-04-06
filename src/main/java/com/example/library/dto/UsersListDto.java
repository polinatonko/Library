package com.example.library.dto;

import com.example.library.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UsersListDto {
    private List<User> objects = new ArrayList<>();

    public UsersListDto(List<User> users)
    {
        this.objects.addAll(users);
    }
}