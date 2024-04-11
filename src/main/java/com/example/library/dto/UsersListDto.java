package com.example.library.dto;

import com.example.library.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class UsersListDto extends ObjectsListDto<User> {
}